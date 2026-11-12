<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Candidature · RH</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/rh.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/entretiens.css">
</head>
<body>
<div class="c-espace"><jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="candidatures"/></jsp:include>
<div class="contenu"><main class="page-espace c-page-liste">
    <header class="entete">
        <a href="${pageContext.request.contextPath}/rh/offre/${candidature.offre.idOffre}/candidatures" class="retour">← Retour aux candidatures</a>
        <div>
            <p class="surtitre">Ressources humaines</p>
            <h1>Candidature de <c:out value="${candidature.nomComplet}"/></h1>
        </div>
    </header>

    <c:if test="${not empty message}"><p style="color:#166534; font-weight:600;">${message}</p></c:if>
    <c:if test="${not empty error}"><p style="color:#b91c1c; font-weight:600;">${error}</p></c:if>

    <section class="carte c-publication" aria-labelledby="titre-candidature">
        <div class="entete">
            <h2 id="titre-candidature"><c:out value="${candidature.offre.titre}"/></h2>
            <p>
                Reçue le ${candidature.dateCandidatureFormatee}
                · <c:out value="${candidature.offre.poste.intitule}"/>
                · <c:out value="${candidature.offre.departement.nom}"/>
            </p>
        </div>

        <div class="offres-publiees">
            <h3>Coordonnées</h3>
            <dl class="fiche-candidat">
                <div class="info">
                    <dt>Nom complet</dt>
                    <dd><c:out value="${candidature.nomComplet}"/></dd>
                </div>
                <div class="info">
                    <dt>Email</dt>
                    <dd><c:out value="${candidature.email}"/></dd>
                </div>
                <div class="info">
                    <dt>Téléphone</dt>
                    <dd><c:out value="${candidature.telephone}"/></dd>
                </div>
                <div class="info">
                    <dt>Statut</dt>
                    <dd>${candidature.statutAffiche}</dd>
                </div>
            </dl>

            <h3 class="titre-reponses">Présélection</h3>
            <c:choose>
                <c:when test="${candidature.preselectionnee}">
                    <dl class="fiche-candidat">
                        <div class="info">
                            <dt>Présélectionné le</dt>
                            <dd>${candidature.datePreselectionFormatee}</dd>
                        </div>
                        <div class="info">
                            <dt>Par</dt>
                            <dd><c:out value="${candidature.preselectionnePar.nom}"/></dd>
                        </div>
                    </dl>
                    <c:if test="${not empty candidature.commentairePreselection}">
                        <p class="message-candidat"><c:out value="${candidature.commentairePreselection}"/></p>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <p class="vide">Ce candidat n'est pas présélectionné.</p>
                </c:otherwise>
            </c:choose>

            <h3 class="titre-reponses">Entretiens et évaluations</h3>
            <c:choose>
                <c:when test="${empty entretiens}">
                    <p class="vide">Aucun entretien planifié.</p>
                    <c:if test="${candidature.preselectionnee}">
                        <p>
                            <a href="${pageContext.request.contextPath}/rh/entretiens/planifier?candidatureId=${candidature.idCandidature}" class="bouton-principal">Planifier un entretien</a>
                        </p>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <div class="defilement">
                        <table class="table-offres">
                            <thead>
                                <tr>
                                    <th scope="col">Date</th>
                                    <th scope="col">Type</th>
                                    <th scope="col">Lieu</th>
                                    <th scope="col">Statut</th>
                                    <th scope="col">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="e" items="${entretiens}">
                                    <tr>
                                        <td><c:out value="${e.dateHeureFormatee}"/></td>
                                        <td><c:out value="${e.typeEvaluationAffiche}"/></td>
                                        <td><c:out value="${e.lieu}"/></td>
                                        <td><c:out value="${e.statutAffiche}"/></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/rh/evaluations/${candidature.idCandidature}" class="bouton">Évaluer</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <p>
                        <a href="${pageContext.request.contextPath}/rh/entretiens/planifier?candidatureId=${candidature.idCandidature}" class="bouton">Planifier un autre entretien</a>
                    </p>
                </c:otherwise>
            </c:choose>

            <h3 class="titre-reponses">Réponses au formulaire</h3>
            <c:choose>
                <c:when test="${empty reponses}">
                    <p class="vide">Aucune réponse complémentaire.</p>
                </c:when>
                <c:otherwise>
                    <div class="defilement">
                        <table class="table-offres">
                            <thead>
                                <tr>
                                    <th scope="col">Critère</th>
                                    <th scope="col">Réponse</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="reponse" items="${reponses}">
                                    <tr>
                                        <td><c:out value="${reponse.critere.libelle}"/></td>
                                        <td><c:out value="${reponse.valeur}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty candidature.message}">
                <h3 class="titre-reponses">Message du candidat</h3>
                <p class="message-candidat"><c:out value="${candidature.message}"/></p>
            </c:if>
        </div>
    </section>
</main></div></div>
</body>
</html>
