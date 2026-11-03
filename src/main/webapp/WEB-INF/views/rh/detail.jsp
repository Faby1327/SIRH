<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détail RH</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/decision.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/rh.css">
</head>
<body>
<div class="c-espace"><jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="accueil"/></jsp:include>
<div class="contenu"><main class="page-espace c-detail c-detail-rh"><header class="entete"><a href="${pageContext.request.contextPath}/rh/accueil" class="retour">← Retour aux demandes</a><div><p class="surtitre">Ressources humaines</p><h1>Détail de la demande</h1></div></header>
<section class="carte" aria-labelledby="titre-detail"><div class="titre-ligne"><div><div class="reference">DP-${demande.idDemande} · demandée par <c:out value="${demande.manager.nom}"/></div><h2 id="titre-detail"><c:out value="${demande.poste.intitule}"/></h2></div><span class="puce puce-attente"><c:out value="${demande.statutActuel.libelleAffiche}"/></span></div>
<jsp:include page="/WEB-INF/views/fragments/avancement-demande.jsp"/>
<div class="bloc"><h3>Informations de la demande</h3><dl class="infos"><div class="info"><dt>Type de besoin</dt><dd><c:out value="${demande.typeRecrutementAffiche}"/></dd></div><div class="info"><dt>Poste demandé</dt><dd><c:out value="${demande.poste.intitule}"/></dd></div><div class="info"><dt>Nombre de personnes</dt><dd>${demande.nombrePersonnes}</dd></div><div class="info"><dt>Service concerné</dt><dd><c:out value="${demande.departement.nom}"/></dd></div><div class="info"><dt>Budget estimé par effectif</dt><dd class="sans-coupure">${demande.budgetDemande} Ar</dd></div><div class="info"><dt>Coût total estimé</dt><dd class="sans-coupure">${demande.budgetTotalEstime} Ar</dd></div><div class="info"><dt>Demandée par</dt><dd><c:out value="${demande.manager.nom}"/></dd></div><div class="info"><dt>Soumise le</dt><dd>${demande.dateDemandeFormatee}</dd></div><div class="info info-large"><dt>Motif du besoin</dt><dd class="texte"><c:out value="${demande.motif}"/></dd></div></dl></div>
<c:if test="${demande.statutActuel.libelle == 'SOUMISE'}"><div class="bloc"><h3>Vérification RH</h3><ul class="verifs"><li><label class="verif cochee"><input type="checkbox" checked disabled><span class="case">✓</span><span>Informations relatives au poste vérifiées</span></label></li><li><label class="verif cochee"><input type="checkbox" checked disabled><span class="case">✓</span><span>Justification du besoin vérifiée</span></label></li><li><label class="verif cochee"><input type="checkbox" checked disabled><span class="case">✓</span><span>Demande cohérente et complète</span></label></li></ul><div class="actions"><span class="note">Après validation, la demande est transmise au service Finance.</span><div class="boutons"><button type="button" class="refuser" data-open-refus="#refus-rh">Refuser la demande</button><form action="${pageContext.request.contextPath}/rh/valider/${demande.idDemande}" method="post"><button type="submit" class="valider">Valider et transmettre à Finance →</button></form></div></div><form id="refus-rh" class="refus" data-refus-form hidden action="${pageContext.request.contextPath}/rh/refuser/${demande.idDemande}" method="post"><label for="remarque" class="refus-label">Motif du refus (obligatoire)</label><p class="refus-aide">Expliquez pourquoi la demande est refusée. Ce commentaire sera transmis au Manager.</p><textarea id="remarque" name="remarque" rows="4" class="refus-champ" required></textarea><div class="refus-boutons"><button type="button" class="annuler" data-close-refus>Annuler</button><button type="submit" class="confirmer">Confirmer le refus</button></div></form></div></c:if>
<div class="bloc"><h3>Suivi des validations</h3><ol class="suivi"><c:forEach var="h" items="${historique}"><li class="validation"><div class="validation-nom"><c:out value="${h.typeValidation}"/></div><div class="validation-corps"><p>${h.dateChangement} · <c:out value="${h.utilisateur.nom}"/> · <c:out value="${h.statut.libelleAffiche}"/></p><c:if test="${not empty h.remarque}"><p><c:out value="${h.remarque}"/></p></c:if></div><span class="puce puce-valide">Traité</span></li></c:forEach><c:if test="${empty historique}"><li class="validation"><div class="validation-nom">Manager</div><div class="validation-corps"><p>Demande soumise le ${demande.dateDemandeFormatee}.</p></div><span class="puce puce-valide">Soumise</span></li></c:if></ol></div>
</section></main></div></div>
<script src="${pageContext.request.contextPath}/assets/js/refus-form.js"></script>
</body>
</html>
