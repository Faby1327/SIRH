<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirmation d'embauche</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/embauches.css">
</head>
<body>
<div class="emb-page">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp">
        <jsp:param name="role" value="rh"/>
        <jsp:param name="active" value="candidatures"/>
    </jsp:include>
    <main class="emb-main">
        <div class="emb-topbar">
            <a href="${ctx}/rh/offre/${offre.idOffre}/embauches" class="emb-back">← Retour aux propositions</a>
            <span class="emb-breadcrumb">Recrutement / Confirmation d'embauche</span>
        </div>

        <header class="emb-header">
            <div>
                <span class="emb-kicker">ESPACE RESSOURCES HUMAINES</span>
                <h1>Confirmation d'embauche</h1>
                <p class="emb-subtitle">Vérifiez les informations avant de confirmer le recrutement.</p>
            </div>
            <div class="emb-offer-ref">
                <span>Statut</span>
                <strong><c:out value="${proposition.statutAffiche}"/></strong>
            </div>
        </header>

        <section class="emb-section">
            <div class="emb-section-heading">
                <div>
                    <span class="emb-section-number">01</span>
                    <div>
                        <h2>Informations de l'offre</h2>
                        <p>Détails du poste accepté par le candidat</p>
                    </div>
                </div>
            </div>
            <div class="emb-details">
                <span><b>Intitulé</b><c:out value="${offre.titre}"/></span>
                <span><b>Référence</b><c:out value="${offre.reference}"/></span>
                <span><b>Département</b><c:out value="${offre.departement.nom}"/></span>
                <span><b>Type de recrutement</b><c:out value="${offre.typeRecrutementAffiche}"/></span>
                <span><b>Contrat proposé</b><c:out value="${proposition.typeContratPropose}"/></span>
                <span><b>Salaire proposé</b>
                    <c:choose>
                        <c:when test="${not empty proposition.salairePropose}"><c:out value="${proposition.salairePropose}"/> Ar</c:when>
                        <c:otherwise>À définir</c:otherwise>
                    </c:choose>
                </span>
                <span><b>Réponse du candidat</b><c:out value="${proposition.dateReponse}"/></span>
            </div>
        </section>

        <section class="emb-section">
            <div class="emb-section-heading">
                <div>
                    <span class="emb-section-number">02</span>
                    <div>
                        <h2>Informations du candidat</h2>
                        <p>Profil ayant accepté la proposition</p>
                    </div>
                </div>
            </div>
            <div class="emb-details">
                <span><b>Nom complet</b><c:out value="${proposition.candidature.nomComplet}"/></span>
                <span><b>Email</b><c:out value="${proposition.candidature.email}"/></span>
                <span><b>Téléphone</b><c:out value="${proposition.candidature.telephone}"/></span>
                <span><b>Date de candidature</b><c:out value="${proposition.candidature.dateCandidatureFormatee}"/></span>
            </div>
        </section>

        <section class="emb-section">
            <div class="emb-section-heading">
                <div>
                    <span class="emb-section-number">03</span>
                    <div>
                        <h2>Validation RH</h2>
                        <p>Après cette confirmation, vous renseignerez les informations de l'employé et de son contrat initial.</p>
                    </div>
                </div>
            </div>
            <form method="post" action="${ctx}/rh/offre/${offre.idOffre}/embauches/${proposition.id}/confirmation">
                <div class="champ-ligne">
                    <label for="commentaireRh">Commentaire RH (facultatif)</label>
                    <textarea id="commentaireRh" name="commentaireRh" rows="4" class="champ"
                              placeholder="Ajoutez une remarque sur la confirmation ou la suite du recrutement."></textarea>
                </div>
                <button type="submit" class="emb-button emb-button-primary">Confirmer et créer l'employé →</button>
            </form>
        </section>
    </main>
</div>
</body>
</html>
