<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Entretiens et tests · RH</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/rh.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/entretiens.css">
</head>
<body>
<div class="c-espace c-entretiens">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp">
        <jsp:param name="role" value="rh"/>
        <jsp:param name="active" value="entretiens"/>
    </jsp:include>

    <div class="contenu">
        <main class="page-espace c-page-liste">
            <header class="entete">
                <div>
                    <p class="surtitre">Ressources humaines · Recrutement</p>
                    <h1>Entretiens et tests</h1>
                </div>
                <a href="${pageContext.request.contextPath}/rh/entretiens/planifier" class="bouton-principal">Planifier un entretien</a>
            </header>

            <c:if test="${not empty message}">
                <div class="bandeau" role="status"><span><c:out value="${message}"/></span></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="bandeau bandeau-erreur" role="alert"><span><c:out value="${error}"/></span></div>
            </c:if>

            <div class="stats">
                <div class="stat">
                    <div class="stat-label">Planifiés</div>
                    <div class="stat-valeur">${nbEntretiens}</div>
                </div>
                <div class="stat">
                    <div class="stat-label">Terminés</div>
                    <div class="stat-valeur">${nbTermines}</div>
                </div>
                <div class="stat">
                    <div class="stat-label">Tests pratiques</div>
                    <div class="stat-valeur">${nbTests}</div>
                </div>
                <div class="stat">
                    <div class="stat-label">Annulés</div>
                    <div class="stat-valeur">${nbAnnules}</div>
                </div>
            </div>

            <section class="c-publication" aria-labelledby="titre-entretiens">
                <div class="entete">
                    <h2 id="titre-entretiens">Calendrier des entretiens</h2>
                    <p>Seuls les candidats présélectionnés ou déjà évalués peuvent être planifiés.</p>
                </div>

                <div class="offres-publiees">
                    <c:choose>
                        <c:when test="${empty entretiens}">
                            <p class="vide">Aucun entretien planifié pour le moment.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="defilement">
                                <table class="table-offres">
                                    <thead>
                                        <tr>
                                            <th scope="col">Date et heure</th>
                                            <th scope="col">Candidat</th>
                                            <th scope="col">Offre</th>
                                            <th scope="col">Type</th>
                                            <th scope="col">Lieu</th>
                                            <th scope="col">Évaluateurs</th>
                                            <th scope="col">Statut</th>
                                            <th scope="col">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="e" items="${entretiens}">
                                            <tr>
                                                <td><strong><c:out value="${e.dateHeureFormatee}"/></strong></td>
                                                <td>
                                                    <c:out value="${e.candidature.nomComplet}"/><br>
                                                    <span class="muted"><c:out value="${e.candidature.email}"/></span>
                                                </td>
                                                <td>
                                                    <c:out value="${e.candidature.offre.titre}"/><br>
                                                    <span class="muted"><c:out value="${e.candidature.offre.reference}"/></span>
                                                </td>
                                                <td><c:out value="${e.typeEvaluationAffiche}"/></td>
                                                <td><c:out value="${e.lieu}"/></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${empty e.evaluationsDesignees}">—</c:when>
                                                        <c:otherwise>
                                                            <c:forEach var="ee" items="${e.evaluationsDesignees}" varStatus="boucle">
                                                                <c:out value="${ee.evaluateur.nom}"/><c:if test="${not boucle.last}">, </c:if>
                                                            </c:forEach>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${e.statut == 'TERMINE'}"><span class="puce puce-valide">Terminé</span></c:when>
                                                        <c:when test="${e.statut == 'ANNULE'}"><span class="puce puce-refuse">Annulé</span></c:when>
                                                        <c:otherwise><span class="puce puce-attente">Planifié</span></c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="actions">
                                                        <a href="${pageContext.request.contextPath}/rh/candidature/${e.candidature.idCandidature}" class="bouton">Dossier</a>
                                                        <c:if test="${e.statut != 'ANNULE'}">
                                                            <a href="${pageContext.request.contextPath}/rh/evaluations/${e.candidature.idCandidature}" class="bouton">
                                                                <c:choose>
                                                                    <c:when test="${dejaEvalueParCandidature[e.candidature.idCandidature]}">Réévaluer</c:when>
                                                                    <c:otherwise>Évaluer</c:otherwise>
                                                                </c:choose>
                                                            </a>
                                                        </c:if>
                                                        <c:if test="${e.statut == 'PLANIFIE'}">
                                                            <form method="post" action="${pageContext.request.contextPath}/rh/entretiens/${e.id}/annuler"
                                                                  onsubmit="return confirm('Annuler cet entretien ?');">
                                                                <button type="submit" class="bouton bouton-danger">Annuler</button>
                                                            </form>
                                                        </c:if>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>
    </div>
</div>
</body>
</html>
