<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon profil</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css"><link rel="stylesheet" href="${ctx}/assets/css/base.css"><link rel="stylesheet" href="${ctx}/assets/css/besoins.css"><link rel="stylesheet" href="${ctx}/assets/css/composants.css"><link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261108-dossier">
</head>
<body>
<div class="c-espace">
    <aside class="barre">
        <div class="marque"><div class="marque-titre">Espace employé</div><div class="marque-sous-titre">Mon dossier</div></div>
        <nav class="menu" aria-label="Navigation principale"><a href="${ctx}/employe/profil" class="lien actif">Mon profil</a></nav>
        <div class="bas">
            <div class="profil"><div class="avatar" aria-hidden="true"><c:out value="${fn:substring(employe.personne.prenom, 0, 1)}"/></div><div><div class="profil-nom"><c:out value="${employe.personne.nomComplet}"/></div><div class="profil-detail"><c:out value="${employe.matricule}"/></div></div></div>
            <form action="${ctx}/logout" method="get"><button type="submit" class="deconnexion">Se déconnecter</button></form>
        </div>
    </aside>
    <div class="contenu">
        <main class="page-espace c-page-liste c-annuaire-rh c-dossier-employe">
            <header class="entete dossier-hero">
                <div class="dossier-identite">
                    <c:choose><c:when test="${not empty employe.photo}"><img class="dossier-photo" src="${ctx}/employes/${employe.id}/photo" alt="Ma photo"></c:when><c:otherwise><div class="dossier-avatar"><c:out value="${fn:substring(employe.personne.prenom, 0, 1)}"/><c:out value="${fn:substring(employe.personne.nom, 0, 1)}"/></div></c:otherwise></c:choose>
                    <div><p class="surtitre">Mon profil</p><h1><c:out value="${employe.personne.nomComplet}"/></h1><p class="dossier-reference"><strong><c:out value="${employe.matricule}"/></strong><span>•</span><c:out value="${empty employe.poste ? 'Poste non renseigné' : employe.poste.intitule}"/></p></div>
                </div>
                <span class="dossier-statut"><c:out value="${employe.statut}"/></span>
            </header>

            <section class="carte dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon">01</span><div><h2>Mes informations</h2><p>Lecture seule : pour toute correction, contactez les Ressources humaines.</p></div></div></div>
                <div class="dossier-info-grid">
                    <div><span>Email</span><strong><c:out value="${empty employe.personne.email ? '-' : employe.personne.email}"/></strong></div>
                    <div><span>Téléphone</span><strong><c:out value="${empty employe.personne.telephone ? '-' : employe.personne.telephone}"/></strong></div>
                    <div><span>Date de naissance</span><strong><c:out value="${empty employe.personne.dateNaissance ? '-' : employe.personne.dateNaissance}"/></strong></div>
                    <div><span>Adresse</span><strong><c:out value="${empty employe.adresse ? '-' : employe.adresse}"/></strong><small><c:out value="${employe.commune}"/> <c:out value="${employe.region}"/></small></div>
                    <div><span>Situation matrimoniale</span><strong><c:out value="${empty employe.situationMatrimoniale ? '-' : employe.situationMatrimoniale}"/></strong><small><c:out value="${employe.nombreEnfants}"/> enfant(s)</small></div>
                    <div><span>Contact d'urgence</span><strong><c:out value="${empty employe.contactUrgenceNom ? '-' : employe.contactUrgenceNom}"/></strong><small><c:out value="${employe.contactUrgenceRelation}"/> <c:out value="${employe.contactUrgenceTelephone}"/></small></div>
                    <div><span>Département</span><strong><c:out value="${empty employe.departement ? '-' : employe.departement.nom}"/></strong></div>
                    <div><span>Supérieur hiérarchique</span><strong><c:out value="${empty employe.superieur ? '-' : employe.superieur.personne.nomComplet}"/></strong></div>
                    <div><span>Date d'embauche</span><strong><c:out value="${employe.dateEmbauche}"/></strong></div>
                </div>
            </section>

            <section class="carte c-table dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-blue">02</span><div><h2>Mes documents</h2><p><c:out value="${documents.size()}"/> document(s) dans mon dossier</p></div></div></div>
                <div class="defilement">
                    <table class="table-offres">
                        <thead><tr><th>Type</th><th>Fichier</th><th>Ajouté le</th><th>Expiration</th><th>Action</th></tr></thead>
                        <tbody>
                        <c:forEach var="doc" items="${documents}">
                            <tr><td><strong><c:out value="${doc.typeDocument.libelle}"/></strong></td><td><c:out value="${doc.nomFichier}"/></td><td><c:out value="${fn:substring(doc.dateAjout, 0, 10)}"/></td><td><c:out value="${empty doc.dateExpiration ? '-' : doc.dateExpiration}"/></td><td><a class="bouton" href="${ctx}/employes/${employe.id}/documents/${doc.id}">Télécharger</a></td></tr>
                        </c:forEach>
                        <c:if test="${empty documents}"><tr><td colspan="5" class="dossier-empty">Aucun document dans votre dossier.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</div>
</body>
</html>
