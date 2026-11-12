<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Candidats à évaluer · Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/rh.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/manager.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/entretiens.css">
</head>
<body>
<div class="c-espace c-entretiens">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp">
        <jsp:param name="role" value="manager"/>
        <jsp:param name="active" value="candidats"/>
    </jsp:include>

    <div class="contenu">
        <main class="page-espace c-page-liste">
            <header class="entete">
                <div>
                    <p class="surtitre">Espace manager · Recrutement</p>
                    <h1>Candidats à évaluer</h1>
                </div>
            </header>

            <c:if test="${not empty message}">
                <div class="bandeau" role="status"><span><c:out value="${message}"/></span></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="bandeau bandeau-erreur" role="alert"><span><c:out value="${error}"/></span></div>
            </c:if>

            <div class="stats">
                <div class="stat">
                    <div class="stat-label">Candidats concernés</div>
                    <div class="stat-valeur">${nbCandidats}</div>
                </div>
                <div class="stat">
                    <div class="stat-label">Restant à évaluer</div>
                    <div class="stat-valeur">${nbAEvaluer}</div>
                </div>
                <div class="stat">
                    <div class="stat-label">Évaluations saisies</div>
                    <div class="stat-valeur">${nbEvaluations}</div>
                </div>
            </div>

            <section class="c-publication">
                <div class="entete">
                    <h2>Entretiens qui vous sont assignés</h2>
                    <p>Vous ne voyez que les candidats de votre département pour lesquels le RH vous a désigné.</p>
                </div>

                <div class="offres-publiees">
                    <c:choose>
                        <c:when test="${empty candidats}">
                            <p class="vide">Aucun candidat ne vous est assigné pour le moment.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="defilement">
                                <table class="table-offres">
                                    <thead>
                                        <tr>
                                            <th scope="col">Candidat</th>
                                            <th scope="col">Offre</th>
                                            <th scope="col">Entretiens assignés</th>
                                            <th scope="col">Statut candidature</th>
                                            <th scope="col">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="c" items="${candidats}">
                                            <c:set var="mesEntretiens" value="${entretiensParCandidature[c.idCandidature]}"/>
                                            <tr>
                                                <td>
                                                    <strong><c:out value="${c.nomComplet}"/></strong><br>
                                                    <span class="muted"><c:out value="${c.email}"/></span>
                                                </td>
                                                <td>
                                                    <c:out value="${c.offre.titre}"/><br>
                                                    <span class="muted"><c:out value="${c.offre.reference}"/></span>
                                                </td>
                                                <td>
                                                    <c:forEach var="e" items="${mesEntretiens}">
                                                        <div>
                                                            <c:out value="${e.typeEvaluationAffiche}"/> ·
                                                            <c:out value="${e.dateHeureFormatee}"/>
                                                            <c:choose>
                                                                <c:when test="${e.statut == 'TERMINE'}"> · Terminé</c:when>
                                                                <c:when test="${e.statut == 'ANNULE'}"> · Annulé</c:when>
                                                                <c:otherwise> · Planifié</c:otherwise>
                                                            </c:choose>
                                                        </div>
                                                    </c:forEach>
                                                </td>
                                                <td><c:out value="${c.statutAffiche}"/></td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/manager/candidats/${c.idCandidature}" class="bouton">
                                                        <c:choose>
                                                            <c:when test="${dejaEvalueParCandidature[c.idCandidature]}">Réévaluer</c:when>
                                                            <c:otherwise>Évaluer</c:otherwise>
                                                        </c:choose>
                                                    </a>
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
