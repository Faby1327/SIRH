<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détail demande</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/decision.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/manager.css">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="manager"/><jsp:param name="active" value="accueil"/></jsp:include>

    <div class="contenu">
        <main class="page-espace c-detail c-detail-manager">
            <header class="entete"><a href="${pageContext.request.contextPath}/manager/accueil" class="retour">← Retour aux demandes</a><div><p class="surtitre">Manager · <c:out value="${demande.departement.nom}"/></p><h1>Détail de la demande</h1></div></header>
            <section class="carte" aria-labelledby="titre-detail">
                <div class="titre-ligne"><div><div class="reference">DP-${demande.idDemande} · demandée par <c:out value="${demande.manager.nom}"/></div><h2 id="titre-detail"><c:out value="${demande.poste.intitule}"/></h2></div><span class="puce ${demande.statutActuel.libelle == 'BROUILLON' ? 'puce-brouillon' : 'puce-valide'}"><c:choose><c:when test="${demande.statutActuel.libelle == 'BROUILLON'}">Brouillon</c:when><c:otherwise>Validé</c:otherwise></c:choose></span></div>
                <jsp:include page="/WEB-INF/views/fragments/avancement-demande.jsp"/>
                <div class="bloc"><h3>Informations de la demande</h3><dl class="infos">
                    <div class="info"><dt>Type de besoin</dt><dd><c:out value="${demande.typeRecrutementAffiche}"/></dd></div>
                    <div class="info"><dt>Nombre de personnes</dt><dd>${demande.nombrePersonnes}</dd></div>
                    <div class="info"><dt>Service concerné</dt><dd><c:out value="${demande.departement.nom}"/></dd></div>
                    <div class="info"><dt>Budget estimé par effectif</dt><dd class="sans-coupure">${demande.budgetDemande} Ar</dd></div>
                    <div class="info"><dt>Coût total estimé</dt><dd class="sans-coupure">${demande.budgetTotalEstime} Ar</dd></div>
                    <div class="info"><dt>Soumise le</dt><dd>${demande.dateDemandeFormatee}</dd></div>
                    <div class="info"><dt>Date souhaitée</dt><dd>${demande.dateSouhaiteeFormatee}</dd></div>
                    <div class="info info-large"><dt>Motif du besoin</dt><dd class="texte"><c:out value="${demande.motif}"/></dd></div>
                </dl></div>

                <div class="bloc"><h3>Suivi de la demande</h3><c:choose><c:when test="${demande.statutActuel.libelle == 'REFUSEE' || demande.statutActuel.libelle == 'BUDGET_INSUFFISANT'}"><div class="decision decision-ko" role="status"><div><div class="decision-titre">Demande refusée</div><p>Le motif transmis par le service est indiqué dans l’historique.</p></div></div><div class="boutons"><form action="${pageContext.request.contextPath}/manager/delete/${demande.idDemande}" method="post"><button type="submit" class="refuser">Supprimer cette demande refusée</button></form></div></c:when><c:when test="${demande.statutActuel.libelle == 'VALIDEE'}"><div class="suivi suivi-valide" role="status"><div class="suivi-titre">Demande approuvée</div><p>La demande a terminé le circuit de validation.</p></div></c:when><c:otherwise><div class="suivi suivi-attente" role="status"><div class="suivi-titre">Demande en cours de validation</div><p>Suivez les étapes ci-dessus pour connaître l’avancement.</p></div></c:otherwise></c:choose></div>
                <div class="bloc"><h3>Historique des validations</h3><ul><c:forEach var="h" items="${historique}"><li>${h.dateChangement} · ${h.typeValidation} · ${h.statut.libelle} · ${h.utilisateur.nom}<c:if test="${not empty h.remarque}"> · ${h.remarque}</c:if></li></c:forEach></ul></div>
            </section>
        </main>
    </div>
</div>
</body>
</html>
