package com.example.logiciel_RH.service;

import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.logiciel_RH.entity.*;
import com.example.logiciel_RH.repository.*;

@Service
public class Bloc7Service {
 private final DemandeCongeRepository conges; private final DemandeAbsenceRepository absences;
 private final EmployeBloc7Repository employes; private final ContratRepository contrats; private final SoldeCongeRepository soldes;
 private final HistoriqueDemandeCongeRepository historique; private final HistoriqueDemandeAbsenceRepository historiqueAbsence;
 public Bloc7Service(DemandeCongeRepository c,DemandeAbsenceRepository a,EmployeBloc7Repository e,ContratRepository cr,SoldeCongeRepository s,HistoriqueDemandeCongeRepository h,HistoriqueDemandeAbsenceRepository ha){conges=c;absences=a;employes=e;contrats=cr;soldes=s;historique=h;historiqueAbsence=ha;}
 public Employe employe(Utilisateur u){return employes.findByPersonne_IdPersonne(u.getPersonne().getIdPersonne()).orElseThrow(()->new IllegalArgumentException("Aucun dossier employé n'est associé à ce compte."));}
 public List<DemandeConge> conges(Utilisateur u){return switch(u.getRole()){case "RH"->conges.findAllByOrderByDateCreationDesc();case "MANAGER"->conges.pourDepartement(departement(u),Contrat.Statut.EN_COURS);default->conges.findByEmploye_Personne_Utilisateur_IdUtilisateurOrderByDateCreationDesc(u.getIdUtilisateur());};}
 public List<DemandeAbsence> absences(Utilisateur u){return switch(u.getRole()){case "RH"->absences.findAllByOrderByDateCreationDesc();case "MANAGER"->absences.pourDepartement(departement(u),Contrat.Statut.EN_COURS);default->absences.findByEmploye_Personne_Utilisateur_IdUtilisateurOrderByDateCreationDesc(u.getIdUtilisateur());};}
 public List<Employe> employesDepartement(Utilisateur u){return employes.duDepartement(departement(u),Contrat.Statut.EN_COURS);}
 public List<Employe> remplacantsDisponibles(Utilisateur u,DemandeConge demande){
  return remplacantsDisponibles(u,demande.getEmploye(),demande.getDateDebut(),demande.getDateFin());
 }
 public List<Employe> remplacantsDisponibles(Utilisateur u,DemandeAbsence demande){
  return remplacantsDisponibles(u,demande.getEmploye(),demande.getDateDebut(),demande.getDateFin());
 }
 private List<Employe> remplacantsDisponibles(Utilisateur u,Employe demandeur,LocalDate debut,LocalDate fin){
  if(!autoriseManager(u,demandeur))throw new SecurityException("Accès refusé.");
  List<DemandeConge> demandesActives=conges.pourDepartement(departement(u),Contrat.Statut.EN_COURS);
  List<DemandeAbsence> absencesActives=absences.pourDepartement(departement(u),Contrat.Statut.EN_COURS);
  Long posteDemande=contrats.findByEmploye_IdAndStatut(demandeur.getId(),Contrat.Statut.EN_COURS).stream().findFirst().map(c->c.getPoste().getIdPoste()).orElse(null); if(posteDemande==null)return List.of();
  return employes.duPoste(departement(u),posteDemande,Contrat.Statut.EN_COURS).stream().filter(e->!e.getId().equals(demandeur.getId()))
   .filter(e->demandesActives.stream().noneMatch(d->e.getId().equals(d.getEmploye().getId())&&Set.of("EN_ATTENTE_MANAGER","EN_ATTENTE_RH","VALIDEE").contains(d.getStatut())&&chevauche(d.getDateDebut(),d.getDateFin(),debut,fin)))
   .filter(e->absencesActives.stream().noneMatch(a->e.getId().equals(a.getEmploye().getId())&&Set.of("A_TRANSMETTRE_RH","TRANSMISE_RH","JUSTIFIEE","NON_JUSTIFIEE").contains(a.getStatut())&&chevauche(a.getDateDebut(),a.getDateFin(),debut,fin)))
   .toList();
 }
 private static boolean chevauche(LocalDate debutA,LocalDate finA,LocalDate debutB,LocalDate finB){return !debutA.isAfter(finB)&&!debutB.isAfter(finA);}
 private Long departement(Utilisateur u){if(u.getDepartement()==null)throw new IllegalArgumentException("Aucun département n'est associé à ce compte.");return u.getDepartement().getIdDepartement();}
 public long solde(Utilisateur u){return solde(employe(u).getId());}
 public long solde(Long employeId){return soldes.totalRestant(employeId);}
 private static final List<String> CONGES_OCCUPANTS=List.of("EN_ATTENTE_MANAGER","EN_ATTENTE_RH","VALIDEE");
 private static final List<String> ABSENCES_OCCUPANTES=List.of("A_TRANSMETTRE_RH","TRANSMISE_RH","JUSTIFIEE","NON_JUSTIFIEE");
 private void verifierPeriodeDisponible(Long employeId,LocalDate debut,LocalDate fin){
  if(!conges.findByEmploye_IdAndStatutInAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(employeId,CONGES_OCCUPANTS,fin,debut).isEmpty())throw new IllegalArgumentException("La période chevauche un congé déjà demandé ou validé.");
  if(!absences.findByEmploye_IdAndStatutInAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(employeId,ABSENCES_OCCUPANTES,fin,debut).isEmpty())throw new IllegalArgumentException("La période chevauche une absence déjà déclarée.");
 }
 @Transactional public DemandeConge creerConge(Utilisateur u,LocalDate debut,LocalDate fin,String motif){if(!"EMPLOYE".equals(u.getRole()))throw new SecurityException("Accès refusé.");Employe e=employe(u);validerDates(debut,fin);verifierPeriodeDisponible(e.getId(),debut,fin);int jours=joursOuvres(debut,fin);if(jours<1)throw new IllegalArgumentException("La période ne contient aucun jour ouvré.");if(jours>soldes.totalRestant(e.getId()))throw new IllegalArgumentException("Le solde disponible est insuffisant.");DemandeConge d=new DemandeConge();d.setEmploye(e);d.setDemandeur(u);d.setDateDebut(debut);d.setDateFin(fin);d.setJours(jours);d.setMotif(motif);return conges.save(d);}
 @Transactional public DemandeAbsence creerAbsence(Utilisateur demandeur,Long employeId,LocalDate debut,LocalDate fin,String motif,String fichier){if(!Set.of("EMPLOYE","MANAGER").contains(demandeur.getRole()))throw new SecurityException("Accès refusé.");validerDates(debut,fin);Employe e=employes.findById(employeId).orElseThrow(()->new IllegalArgumentException("Employé introuvable."));if("EMPLOYE".equals(demandeur.getRole())&&!e.getId().equals(employe(demandeur).getId()))throw new SecurityException("Accès refusé.");if("MANAGER".equals(demandeur.getRole())&&!employes.appartientAuDepartement(e.getId(),departement(demandeur),Contrat.Statut.EN_COURS))throw new SecurityException("Employé hors de votre département.");verifierPeriodeDisponible(e.getId(),debut,fin);DemandeAbsence d=new DemandeAbsence();d.setEmploye(e);d.setDeclareePar(demandeur);d.setDateDebut(debut);d.setDateFin(fin);d.setMotif(motif);d.setJustificatifUrl(fichier);if("MANAGER".equals(demandeur.getRole()))d.setStatut("TRANSMISE_RH");d=absences.save(d);if("MANAGER".equals(demandeur.getRole()))historiqueAbsence.save(new HistoriqueDemandeAbsence(d,demandeur,"TRANSMISE_RH","Déclarée et transmise à RH"));return d;}
 @Transactional public void decisionConge(Long id,Utilisateur u,boolean accepte,String commentaire){DemandeConge d=conges.findLockedById(id).orElseThrow();String role=u.getRole();if("MANAGER".equals(role)){autoriserManager(u,d.getEmploye());if(!"EN_ATTENTE_MANAGER".equals(d.getStatut()))throw new IllegalArgumentException("Cette étape est déjà traitée.");d.setStatut(accepte?"EN_ATTENTE_RH":"REFUSE_MANAGER");d.setCommentaireManager(commentaire);d.setDateDecisionManager(LocalDateTime.now());historique.save(new HistoriqueDemandeConge(d,u,"MANAGER",accepte?"VALIDEE":"REFUSEE",commentaire));}else if("RH".equals(role)){if(!"EN_ATTENTE_RH".equals(d.getStatut()))throw new IllegalArgumentException("La validation manager est requise avant la décision RH.");d.setCommentaireRh(commentaire);d.setDateDecisionRh(LocalDateTime.now());if(accepte){deduireFifo(d);d.setStatut("VALIDEE");}else d.setStatut("REFUSE_RH");historique.save(new HistoriqueDemandeConge(d,u,"RH",accepte?"VALIDEE":"REFUSEE",commentaire));}else throw new SecurityException("Accès refusé.");conges.save(d);}
 private void deduireFifo(DemandeConge d){int aDeduir=d.getJours();for(SoldeConge lot:soldes.lotsFifo(d.getEmploye().getId())){int pris=Math.min(aDeduir,lot.getJoursRestants());lot.setJoursRestants(lot.getJoursRestants()-pris);soldes.save(lot);aDeduir-=pris;if(aDeduir==0)break;}if(aDeduir>0)throw new IllegalArgumentException("Solde insuffisant au moment de la validation finale.");}
 @Transactional public void commentaireAbsence(Long id,Utilisateur u,String commentaire){DemandeAbsence d=absences.findLockedById(id).orElseThrow();autoriserManager(u,d.getEmploye());if(!"A_TRANSMETTRE_RH".equals(d.getStatut()))throw new IllegalArgumentException("Cette absence a déjà été transmise.");d.setCommentaireManager(commentaire);d.setStatut("TRANSMISE_RH");absences.save(d);historiqueAbsence.save(new HistoriqueDemandeAbsence(d,u,"TRANSMISE_RH",commentaire));}
 @Transactional public void verifierAbsence(Long id,Utilisateur u,boolean justifiee,String commentaire){if(!"RH".equals(u.getRole()))throw new SecurityException("Accès refusé.");DemandeAbsence d=absences.findLockedById(id).orElseThrow();if(!"TRANSMISE_RH".equals(d.getStatut()))throw new IllegalArgumentException("Le manager doit transmettre cette absence avant vérification.");d.setJustifiee(justifiee);d.setCommentaireRh(commentaire);d.setDateDecisionRh(LocalDateTime.now());d.setStatut(justifiee?"JUSTIFIEE":"NON_JUSTIFIEE");absences.save(d);historiqueAbsence.save(new HistoriqueDemandeAbsence(d,u,d.getStatut(),commentaire));}
 public DemandeConge conge(Long id){return conges.findById(id).orElseThrow();} public DemandeAbsence absence(Long id){return absences.findById(id).orElseThrow();}
 public boolean documentAccessible(Utilisateur u,String url){if(u==null||url==null||!Set.of("EMPLOYE","MANAGER","RH").contains(u.getRole()))return false;return absences(u).stream().anyMatch(a->url.equals(a.getJustificatifUrl()));}
 public List<HistoriqueDemandeConge> historique(Long id){return historique.findByDemande_IdOrderByDateActionAsc(id);}
 public List<HistoriqueDemandeAbsence> historiqueAbsence(Long id){return historiqueAbsence.findByDemande_IdOrderByDateActionAsc(id);}
 public void remplacer(Long id,Utilisateur u,Long remplacantId){if(!"MANAGER".equals(u.getRole()))throw new SecurityException("Accès refusé.");DemandeConge d=conge(id);autoriserManager(u,d.getEmploye());if(!"EN_ATTENTE_MANAGER".equals(d.getStatut()))throw new IllegalArgumentException("Le remplacement se définit avant la validation manager.");Employe r=remplacantId==null?null:employes.findById(remplacantId).orElseThrow();if(r!=null&&!remplacantsDisponibles(u,d).stream().anyMatch(e->e.getId().equals(r.getId())))throw new IllegalArgumentException("Ce remplaçant n'est pas disponible sur cette période.");d.setRemplacant(r);conges.save(d);}
 @Transactional public void remplacerAbsence(Long id,Utilisateur u,Long remplacantId){if(!"MANAGER".equals(u.getRole()))throw new SecurityException("Accès refusé.");DemandeAbsence d=absence(id);autoriserManager(u,d.getEmploye());if(!Set.of("A_TRANSMETTRE_RH","TRANSMISE_RH").contains(d.getStatut()))throw new IllegalArgumentException("Le remplacement se définit avant la décision RH.");Employe r=remplacantId==null?null:employes.findById(remplacantId).orElseThrow();if(r!=null&&!remplacantsDisponibles(u,d).stream().anyMatch(e->e.getId().equals(r.getId())))throw new IllegalArgumentException("Ce remplaçant n'est pas disponible sur cette période.");d.setRemplacant(r);absences.save(d);}
 public boolean autoriseManager(Utilisateur u,Employe e){return "MANAGER".equals(u.getRole())&&u.getDepartement()!=null&&employes.appartientAuDepartement(e.getId(),u.getDepartement().getIdDepartement(),Contrat.Statut.EN_COURS);}
 private void autoriserManager(Utilisateur u,Employe e){if(!autoriseManager(u,e))throw new SecurityException("Accès refusé.");}
 private static void validerDates(LocalDate d,LocalDate f){if(d==null||f==null||f.isBefore(d))throw new IllegalArgumentException("La période saisie est invalide.");if(d.isBefore(LocalDate.now()))throw new IllegalArgumentException("Les dates antérieures à aujourd’hui ne sont pas autorisées.");}
 private static int joursOuvres(LocalDate d,LocalDate f){int n=0;for(LocalDate x=d;!x.isAfter(f);x=x.plusDays(1))if(x.getDayOfWeek()!=DayOfWeek.SATURDAY&&x.getDayOfWeek()!=DayOfWeek.SUNDAY)n++;return n;}
}
