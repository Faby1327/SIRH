<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Créer l'employé et le contrat</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/embauches.css?v=20261103-creation">
</head>
<body>
<div class="emb-page">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp">
        <jsp:param name="role" value="rh"/>
        <jsp:param name="active" value="candidatures"/>
    </jsp:include>
    <main class="emb-main emb-creation">
        <div class="emb-topbar">
            <a href="${ctx}/rh/offre/${offre.idOffre}/embauches" class="emb-back">← Retour aux propositions</a>
            <span class="emb-breadcrumb">Recrutement / Création administrative</span>
        </div>
        <c:if test="${not empty error}"><div class="emb-alert emb-alert-error"><c:out value="${error}"/></div></c:if>
        <header class="emb-header emb-creation-header">
            <div>
                <span class="emb-kicker">DERNIÈRE ÉTAPE DU RECRUTEMENT</span>
                <h1>Créer l'employé et son contrat</h1>
                <p class="emb-subtitle">Les données ci-dessous proviennent de la proposition confirmée par le RH.</p>
            </div>
            <div class="emb-offer-ref">
                <span>État de la proposition</span>
                <strong>Confirmée</strong>
                <em>Prête à enregistrer</em>
            </div>
        </header>
        <div class="emb-creation-notice">
            <span class="emb-notice-icon">✓</span>
            <div><strong>Tout est déjà prérempli</strong><p>Le matricule sera généré automatiquement. Vérifiez le récapitulatif, puis confirmez la création.</p></div>
        </div>
        <div class="emb-creation-grid">
            <section class="emb-section emb-creation-card">
                <div class="emb-section-heading"><div><span class="emb-section-number">01</span><div><h2>Identité de l'employé</h2><p>Informations issues du profil candidat</p></div></div></div>
                <div class="emb-info-grid">
                    <div class="emb-info-item emb-info-main"><span>Nom complet</span><strong><c:out value="${proposition.candidature.nomComplet}"/></strong></div>
                    <div class="emb-info-item"><span>Matricule</span><strong class="emb-auto">Généré automatiquement</strong></div>
                    <div class="emb-info-item"><span>Email</span><strong><c:out value="${proposition.candidature.email}"/></strong></div>
                    <div class="emb-info-item"><span>Téléphone</span><strong><c:out value="${empty proposition.candidature.telephone ? '-' : proposition.candidature.telephone}"/></strong></div>
                </div>
            </section>
            <section class="emb-section emb-creation-card">
                <div class="emb-section-heading"><div><span class="emb-section-number emb-section-number-green">02</span><div><h2>Contrat initial</h2><p>Conditions validées dans la proposition</p></div></div></div>
                <div class="emb-contract-summary">
                    <div class="emb-contract-type"><span>Type de contrat</span><strong><c:out value="${proposition.typeContratPropose}"/></strong></div>
                    <div class="emb-contract-salary"><span>Salaire mensuel</span><strong><c:out value="${not empty proposition.salaireFinal ? proposition.salaireFinal : proposition.salairePropose}"/> <small>Ar</small></strong></div>
                </div>
                <div class="emb-info-grid emb-info-grid-contract">
                    <div class="emb-info-item"><span>Poste</span><strong><c:out value="${offre.poste.intitule}"/></strong></div>
                    <div class="emb-info-item"><span>Département</span><strong><c:out value="${offre.departement.nom}"/></strong></div>
                    <div class="emb-info-item"><span>Date de début</span><strong><c:out value="${dateMin}"/></strong></div>
                    <div class="emb-info-item"><span>Statut initial</span><strong class="emb-status-live">EN COURS</strong></div>
                </div>
            </section>
        </div>
        <section class="emb-section emb-confirm-card">
            <div class="emb-section-heading"><div><span class="emb-section-number emb-section-number-dark">03</span><div><h2>Validation finale</h2><p>Cette action enregistrera définitivement l'employé et son contrat.</p></div></div></div>
            <form method="post" action="${ctx}/rh/offre/${offre.idOffre}/embauches/${proposition.id}/creation" class="emb-confirm-form">
                <label class="emb-confirmation"><input type="checkbox" name="confirmation" value="true" required><span>Je confirme la création de cet employé et de son contrat.</span></label>
                <div class="emb-form-actions"><a href="${ctx}/rh/offre/${offre.idOffre}/embauches" class="emb-button emb-button-light">Annuler</a><button type="submit" class="emb-button emb-button-primary">Créer l'employé et le contrat <span>→</span></button></div>
            </form>
        </section>
    </main>
</div>
</body>
</html>
