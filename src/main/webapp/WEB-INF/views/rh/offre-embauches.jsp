<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Embauches - <c:out value="${offre.titre}"/></title>
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
            <a href="${ctx}/rh/offre/${offre.idOffre}/candidatures" class="emb-back">← Retour aux candidatures</a>
            <span class="emb-breadcrumb">Recrutement / Propositions d'embauche</span>
        </div>

        <header class="emb-header">
            <div>
                <span class="emb-kicker">ESPACE RESSOURCES HUMAINES</span>
                <h1>Propositions d'embauche</h1>
                <p class="emb-subtitle">
                    <strong><c:out value="${offre.titre}"/></strong>
                    <span>·</span>
                    <c:out value="${offre.poste.intitule}"/>
                </p>
            </div>
            <div class="emb-offer-ref">
                <span>Référence de l'offre</span>
                <strong><c:out value="${offre.reference}"/></strong>
                <em><c:out value="${offre.statutAffiche}"/></em>
            </div>
        </header>

        <div class="emb-details">
            <span><b>Département</b><c:out value="${offre.departement.nom}"/></span>
            <span><b>Type de recrutement</b><c:out value="${offre.typeRecrutementAffiche}"/></span>
            <span><b>Date de publication</b><c:out value="${offre.datePublicationFormatee}"/></span>
            <span><b>Effectif recherché</b><c:out value="${placesDemandees}"/> personne<c:if test="${placesDemandees > 1}">s</c:if></span>
        </div>

        <c:if test="${not empty message}">
            <div class="emb-alert emb-alert-success"><strong>Succès</strong><c:out value="${message}"/></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="emb-alert emb-alert-error"><strong>Erreur</strong><c:out value="${error}"/></div>
        </c:if>

        <section class="emb-section emb-capacity">
            <div class="emb-section-heading">
                <div>
                    <span class="emb-section-number">01</span>
                    <div>
                        <h2>Suivi des places</h2>
                        <p>État des postes disponibles pour cette offre</p>
                    </div>
                </div>
            </div>
            <div class="emb-stats">
                <div class="emb-stat">
                    <span class="emb-stat-icon">◼</span>
                    <div><small>Places demandées</small><strong><c:out value="${placesDemandees}"/></strong></div>
                </div>
                <div class="emb-stat">
                    <span class="emb-stat-icon emb-icon-blue">✓</span>
                    <div><small>Proposées / acceptées</small><strong><c:out value="${placesPrises}"/></strong></div>
                </div>
                <div class="emb-stat emb-stat-remaining">
                    <span class="emb-stat-icon emb-icon-green">+</span>
                    <div><small>Places restantes</small><strong><c:out value="${placesDemandees - placesPrises}"/></strong></div>
                </div>
            </div>
        </section>

        <section class="emb-section">
            <div class="emb-section-heading">
                <div>
                    <span class="emb-section-number">02</span>
                    <div>
                        <h2>Candidats éligibles</h2>
                        <p>Candidats évalués n'ayant pas encore reçu de proposition</p>
                    </div>
                </div>
                <span class="emb-count"><c:out value="${eligibles.size()}"/> candidat<c:if test="${eligibles.size() > 1}">s</c:if></span>
            </div>
            <div class="emb-table-wrap">
                <table class="emb-table">
                    <thead>
                    <tr>
                        <th>Candidat</th><th>Coordonnées</th><th>Candidature reçue</th><th>Statut</th><th>Décision</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty eligibles}">
                            <tr><td colspan="5" class="emb-empty">Aucun candidat éligible pour le moment.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="candidat" items="${eligibles}">
                                <tr>
                                    <td><strong><c:out value="${candidat.nomComplet}"/></strong></td>
                                    <td><span><c:out value="${candidat.email}"/></span><span><c:out value="${candidat.telephone}"/></span></td>
                                    <td class="emb-muted"><c:out value="${candidat.dateCandidatureFormatee}"/></td>
                                    <td><span class="emb-status emb-status-blue">Évaluée</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${peutProposer}">
                                                <form class="emb-action" method="post" action="${ctx}/rh/offre/${offre.idOffre}/embauches/proposer">
                                                    <input type="hidden" name="candidatureId" value="${candidat.idCandidature}">
                                                    <a href="${ctx}/rh/candidature/${candidat.idCandidature}" class="emb-button emb-button-light">Dossier</a>
                                                    <input type="text" name="commentaireRh" placeholder="Note RH (facultatif)">
                                                    <button type="submit" class="emb-button emb-button-primary">Proposer</button>
                                                </form>
                                            </c:when>
                                            <c:otherwise><span class="emb-muted">Places atteintes</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </section>

        <section class="emb-section">
            <div class="emb-section-heading">
                <div>
                    <span class="emb-section-number">03</span>
                    <div>
                        <h2>Confirmations en attente</h2>
                        <p>Décisions RH à valider après acceptation du candidat</p>
                    </div>
                </div>
                <span class="emb-count"><c:out value="${confirmationsEnAttente.size()}"/> en attente</span>
            </div>
            <div class="emb-table-wrap">
                <table class="emb-table emb-history">
                    <thead><tr><th>Candidat</th><th>Date d'envoi</th><th>Statut</th><th>Action RH</th></tr></thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty confirmationsEnAttente}">
                            <tr><td colspan="4" class="emb-empty">Aucune confirmation en attente.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="proposition" items="${confirmationsEnAttente}">
                                <tr>
                                    <td><strong><c:out value="${proposition.candidature.nomComplet}"/></strong><small class="emb-muted">Contrat : <c:out value="${proposition.typeContratPropose}"/></small></td>
                                    <td class="emb-muted"><c:out value="${proposition.datePropositionFormatee}"/></td>
                                    <td><span class="emb-status emb-status-amber"><c:out value="${proposition.statutAffiche}"/></span></td>
                                    <td>
                                        <a class="emb-button emb-button-primary"
                                           href="${ctx}/rh/offre/${offre.idOffre}/embauches/${proposition.id}/confirmation">
                                            Ouvrir la confirmation
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </section>

        <section class="emb-section">
            <div class="emb-section-heading">
                <div>
                    <span class="emb-section-number">04</span>
                    <div>
                        <h2>Historique des propositions</h2>
                        <p>Suivi des propositions transmises aux candidats (lecture seule)</p>
                    </div>
                </div>
                <span class="emb-count"><c:out value="${propositions.size()}"/> proposition<c:if test="${propositions.size() > 1}">s</c:if></span>
            </div>
            <div class="emb-table-wrap">
                <table class="emb-table emb-history">
                    <thead><tr><th>Candidat</th><th>Date d'envoi</th><th>Statut</th><th>Proposée par</th><th>Commentaire RH</th></tr></thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty propositions}"><tr><td colspan="5" class="emb-empty">Aucune proposition n'a encore été envoyée.</td></tr></c:when>
                        <c:otherwise>
                            <c:forEach var="proposition" items="${propositions}">
                                <tr>
                                    <td><strong><c:out value="${proposition.candidature.nomComplet}"/></strong></td>
                                    <td class="emb-muted"><c:out value="${proposition.datePropositionFormatee}"/></td>
                                    <td>
                                        <span class="emb-status emb-status-amber"><c:out value="${proposition.statutAffiche}"/></span>
                                        <c:if test="${proposition.statut == 'DEMANDE_SALAIRE' or proposition.statut == 'DEMANDE_CONTRAT' or proposition.statut == 'DEMANDE_CONDITIONS'}">
                                            <c:if test="${not empty proposition.salaireDemandeCandidat}"><small class="emb-requested-salary">Salaire demandé : <c:out value="${proposition.salaireDemandeCandidat}"/> Ar</small></c:if>
                                            <c:if test="${not empty proposition.typeContratDemandeCandidat}"><small class="emb-requested-salary">Contrat demandé : <c:out value="${proposition.typeContratDemandeCandidat}"/></small></c:if>
                                        </c:if>
                                    </td>
                                    <td><c:out value="${proposition.proposePar.email}"/></td>
                                    <td class="emb-comment">
                                        <c:out value="${proposition.commentaireRh}"/>
                                        <c:if test="${proposition.statut == 'DEMANDE_SALAIRE' or proposition.statut == 'DEMANDE_CONTRAT' or proposition.statut == 'DEMANDE_CONDITIONS'}">
                                            <div class="emb-rh-decision">
                                                <form method="post" action="${ctx}/rh/offre/${offre.idOffre}/embauches/${proposition.id}/conditions">
                                                    <input type="hidden" name="accepter" value="true"/>
                                                    <button class="emb-mini emb-mini-ok" type="submit">Accepter les conditions</button>
                                                </form>
                                                <form method="post" action="${ctx}/rh/offre/${offre.idOffre}/embauches/${proposition.id}/conditions">
                                                    <input type="hidden" name="accepter" value="false"/>
                                                    <button class="emb-mini emb-mini-no" type="submit">Refuser</button>
                                                </form>
                                            </div>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>
</body>
</html>
