<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Planifier un entretien · RH</title>
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
                <a href="${pageContext.request.contextPath}/rh/entretiens" class="retour">← Retour aux entretiens</a>
                <div>
                    <p class="surtitre">Ressources humaines · Recrutement</p>
                    <h1>Planifier un entretien ou un test</h1>
                </div>
            </header>

            <c:if test="${not empty error}">
                <div class="bandeau bandeau-erreur" role="alert"><span><c:out value="${error}"/></span></div>
            </c:if>

            <c:choose>
                <c:when test="${empty candidatures}">
                    <section class="c-publication">
                        <div class="corps">
                            <h2>Aucun candidat disponible</h2>
                            <p>Présélectionnez d'abord une candidature depuis la liste des candidatures d'une offre.</p>
                            <a href="${pageContext.request.contextPath}/rh/accueil#offres" class="bouton">Voir les offres</a>
                        </div>
                    </section>
                </c:when>
                <c:otherwise>
                    <c:if test="${not empty candidature}">
                        <section class="c-publication">
                            <div class="corps">
                                <h2>Candidat sélectionné</h2>
                                <p>
                                    <strong><c:out value="${candidature.nomComplet}"/></strong>
                                    · <c:out value="${candidature.offre.titre}"/>
                                    (<c:out value="${candidature.offre.reference}"/>)
                                    · <c:out value="${candidature.statutAffiche}"/>
                                </p>
                            </div>
                        </section>
                    </c:if>

                    <section class="c-publication">
                        <div class="corps">
                            <h2>Nouvel entretien</h2>
                            <p>Un entretien terminé dès que tous les évaluateurs désignés ont saisi leur évaluation.</p>

                            <form method="post" action="${pageContext.request.contextPath}/rh/entretiens/planifier">
                                <div class="grille">
                                    <div class="champ-ligne">
                                        <label class="etiquette" for="candidatureId">Candidat</label>
                                        <select class="champ" id="candidatureId" name="candidatureId" required>
                                            <option value="">Choisir une candidature</option>
                                            <c:forEach var="c" items="${candidatures}">
                                                <option value="${c.idCandidature}" ${not empty candidature && candidature.idCandidature == c.idCandidature ? 'selected' : ''}>
                                                    <c:out value="${c.nomComplet}"/> — <c:out value="${c.offre.titre}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="champ-ligne">
                                        <label class="etiquette" for="typeEvaluation">Type</label>
                                        <select class="champ" id="typeEvaluation" name="typeEvaluation" required>
                                            <option value="ENTRETIEN_RH">Entretien RH</option>
                                            <option value="ENTRETIEN_TECHNIQUE">Entretien technique</option>
                                            <option value="TEST_PRATIQUE">Test pratique</option>
                                        </select>
                                    </div>

                                    <div class="champ-ligne">
                                        <label class="etiquette" for="dateHeure">Date et heure</label>
                                        <input class="champ" type="datetime-local" id="dateHeure" name="dateHeure"
                                               value="${maintenant}" required>
                                    </div>

                                    <div class="champ-ligne">
                                        <label class="etiquette" for="lieu">Lieu ou lien</label>
                                        <input class="champ" type="text" id="lieu" name="lieu" maxlength="250"
                                               placeholder="Salle, adresse ou lien de visioconférence" required>
                                    </div>
                                </div>

                                <div class="champ-ligne" style="margin-top: 16px;">
                                    <label class="etiquette" for="consigne">Consignes (facultatif)</label>
                                    <textarea class="champ" id="consigne" name="consigne" maxlength="2000"
                                              placeholder="Points à préparer, matériel nécessaire..."></textarea>
                                </div>

                                <div class="champ-ligne" style="margin-top: 16px;">
                                    <span class="etiquette">Évaluateurs désignés</span>
                                    <div class="evaluateurs">
                                        <c:forEach var="u" items="${evaluateurs}">
                                            <label class="evaluateur">
                                                <input type="checkbox" name="evaluateurs" value="${u.idUtilisateur}">
                                                <span>
                                                    <c:out value="${u.nom}"/> (<c:out value="${u.role}"/>)
                                                    <c:if test="${u.role == 'MANAGER' && not empty u.departement}">
                                                        · <c:out value="${u.departement.nom}"/>
                                                    </c:if>
                                                </span>
                                            </label>
                                        </c:forEach>
                                    </div>
                                    <p class="aide">Le RH qui planifie est ajouté automatiquement s'il n'est pas coché.</p>
                                </div>

                                <div class="actions-bas">
                                    <button type="submit" class="bouton-principal">Planifier</button>
                                    <a href="${pageContext.request.contextPath}/rh/entretiens" class="bouton">Annuler</a>
                                </div>
                            </form>
                        </div>
                    </section>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</div>
</body>
</html>
