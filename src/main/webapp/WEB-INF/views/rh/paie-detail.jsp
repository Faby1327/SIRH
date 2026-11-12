<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détail fiche de paie - RH</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
    <link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261103-paie-detail">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="paie"/></jsp:include>
    <div class="contenu">
        <main class="page-espace c-page-liste c-annuaire-rh c-paie-page">
            <div class="dossier-retour"><a href="${ctx}/rh/paie">← Retour aux fiches de paie</a></div>
            <header class="entete dossier-hero">
                <div class="dossier-identite"><div class="dossier-avatar">₳</div><div><p class="surtitre">Fiche de paie</p><h1><c:out value="${fiche.employe.personne.nomComplet}"/></h1><p class="dossier-reference"><strong><c:out value="${fiche.employe.matricule}"/></strong><span>•</span><c:out value="${fiche.mois}"/>/<c:out value="${fiche.annee}"/></p></div></div>
                <span class="paie-status paie-status-${fiche.statut}"><c:out value="${fiche.statut}"/></span>
            </header>
            <c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>
            <section class="carte dossier-card paie-detail-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-blue">01</span><div><h2>Récapitulatif</h2><p>Montant calculé depuis le contrat actif</p></div></div></div>
                <div class="dossier-info-grid"><div><span>Employé</span><strong><c:out value="${fiche.employe.personne.nomComplet}"/></strong></div><div><span>Poste</span><strong><c:out value="${fiche.contrat.poste.intitule}"/></strong></div><div><span>Contrat</span><strong><c:out value="${fiche.contrat.typeContrat}"/></strong></div><div><span>Salaire de base</span><strong><c:out value="${fiche.salaireBase}"/> Ar</strong></div><div><span>Primes</span><strong><c:out value="${fiche.totalPrimes}"/> Ar</strong></div><div><span>Retenues</span><strong><c:out value="${fiche.totalRetenues}"/> Ar</strong></div></div>
                <div class="paie-total"><span>Net à payer</span><strong><c:out value="${fiche.netAPayer}"/> Ar</strong></div>
            </section>
            <section class="carte dossier-card paie-lignes-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-green">02</span><div><h2>Détail des primes</h2><p>Seules les heures supplémentaires validées sont intégrées.</p></div></div></div>
                <div class="defilement"><table class="table-offres"><thead><tr><th>Type</th><th>Libellé</th><th>Durée</th><th>Majoration</th><th>Montant</th></tr></thead><tbody>
                <c:choose><c:when test="${empty lignes}"><tr><td colspan="5" class="dossier-empty">Aucune prime ou retenue enregistrée.</td></tr></c:when><c:otherwise><c:forEach var="ligne" items="${lignes}"><tr><td><c:out value="${ligne.typeLigne}"/></td><td><c:out value="${ligne.libelle}"/></td><td><c:choose><c:when test="${not empty ligne.nombreMinutes}"><c:out value="${ligne.nombreMinutes}"/> min</c:when><c:when test="${not empty ligne.nombreJours}"><c:out value="${ligne.nombreJours}"/> jour(s)</c:when><c:otherwise>—</c:otherwise></c:choose></td><td><c:choose><c:when test="${not empty ligne.tauxMajoration}"><c:out value="${ligne.tauxMajoration}"/>×</c:when><c:otherwise>—</c:otherwise></c:choose></td><td><strong><c:out value="${ligne.montant}"/> Ar</strong></td></tr></c:forEach></c:otherwise></c:choose>
                </tbody></table></div>
            </section>
            <section class="carte dossier-card paie-actions-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-green">02</span><div><h2>Action</h2><p>Suivez les étapes de validation et de paiement</p></div></div></div>
                <div class="paie-action-content">
                    <c:choose>
                        <c:when test="${fiche.statut == 'BROUILLON'}"><p>Cette fiche doit être vérifiée avant de pouvoir être payée.</p><form method="post" action="${ctx}/rh/paie/${fiche.id}/valider"><button class="bouton bouton-principal" type="submit">Valider la fiche</button></form></c:when>
                        <c:when test="${fiche.statut == 'VALIDEE'}"><p>La fiche est validée. Vous pouvez maintenant enregistrer le paiement.</p><form method="post" action="${ctx}/rh/paie/${fiche.id}/payer"><button class="bouton bouton-principal" type="submit">Payer ${fiche.netAPayer} Ar</button></form></c:when>
                        <c:when test="${fiche.statut == 'PAYEE'}"><p class="paie-success">Cette fiche a été payée.</p><c:if test="${not empty paiement}"><div class="paie-payment"><span>Référence</span><strong><c:out value="${paiement.reference}"/></strong><span>Date</span><strong><c:out value="${paiement.datePaiement}"/></strong></div></c:if></c:when>
                        <c:otherwise><p>Cette fiche est annulée et ne peut plus être traitée.</p></c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>
    </div>
</div>
</body>
</html>
