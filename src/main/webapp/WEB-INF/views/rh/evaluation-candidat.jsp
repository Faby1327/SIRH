<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Évaluation du candidat · RH</title>
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
                    <h1>Évaluation du candidat</h1>
                </div>
            </header>

            <c:if test="${not empty message}">
                <div class="bandeau" role="status"><span><c:out value="${message}"/></span></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="bandeau bandeau-erreur" role="alert"><span><c:out value="${error}"/></span></div>
            </c:if>

            <section class="c-publication">
                <div class="corps">
                    <h2><c:out value="${candidature.nomComplet}"/></h2>
                    <p>
                        <c:out value="${candidature.offre.titre}"/>
                        (<c:out value="${candidature.offre.reference}"/>)
                        · <c:out value="${candidature.email}"/>
                        · <c:out value="${candidature.statutAffiche}"/>
                    </p>
                </div>
            </section>

            <c:choose>
                <c:when test="${empty entretiens}">
                    <section class="c-publication">
                        <div class="corps">
                            <h2>Aucun entretien</h2>
                            <p>Planifiez d'abord un entretien ou un test pour ce candidat.</p>
                            <a href="${pageContext.request.contextPath}/rh/entretiens/planifier?candidatureId=${candidature.idCandidature}" class="bouton-principal">Planifier</a>
                        </div>
                    </section>
                </c:when>
                <c:otherwise>
                    <c:forEach var="e" items="${entretiens}">
                        <section class="c-publication">
                            <div class="entete">
                                <h2><c:out value="${e.typeEvaluationAffiche}"/> · <c:out value="${e.dateHeureFormatee}"/></h2>
                                <p>
                                    Lieu : <c:out value="${e.lieu}"/>
                                    · Statut : <c:out value="${e.statutAffiche}"/>
                                </p>
                            </div>
                            <div class="offres-publiees">
                                <c:if test="${not empty e.consigne}">
                                    <p class="vide"><strong>Consignes :</strong> <c:out value="${e.consigne}"/></p>
                                </c:if>

                                <div class="corps">
                                    <h3>Évaluateurs</h3>
                                    <p class="muted">
                                        <c:forEach var="ee" items="${e.evaluationsDesignees}" varStatus="boucle">
                                            <c:out value="${ee.evaluateur.nom}"/><c:if test="${not boucle.last}">, </c:if>
                                        </c:forEach>
                                    </p>

                                    <c:forEach var="ev" items="${evaluations}">
                                        <c:if test="${ev.entretien.id == e.id}">
                                            <div class="sous-carte">
                                                <h3>Évaluation de <c:out value="${ev.evaluateur.nom}"/></h3>
                                                <p class="muted">
                                                    <c:out value="${ev.recommandationAffichee}"/>
                                                    · <c:out value="${ev.dateEvaluationFormatee}"/>
                                                    <c:if test="${not empty ev.moyenne}">· Moyenne : <c:out value="${ev.moyenne}"/>/5</c:if>
                                                </p>
                                                <p><c:out value="${ev.commentaire}"/></p>
                                                <c:if test="${not empty ev.notes}">
                                                    <p class="muted">
                                                        <c:forEach var="crit" items="${criteres}" varStatus="boucleNote">
                                                            <c:out value="${crit.libelle}"/> :
                                                            <c:out value="${ev.notesParCritere[crit.id]}"/>/5<c:if test="${not boucleNote.last}"> · </c:if>
                                                        </c:forEach>
                                                    </p>
                                                </c:if>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>

                                <div class="corps">
                                    <c:choose>
                                        <c:when test="${e.statut == 'ANNULE'}">
                                            <p class="note">Cet entretien est annulé, aucune évaluation n'est possible.</p>
                                        </c:when>
                                        <c:when test="${!affectations[e.id]}">
                                            <p class="note">Vous n'êtes pas désigné comme évaluateur pour cet entretien.</p>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="deja" value="${mesEvaluations[e.id]}"/>
                                            <h3>${empty deja ? 'Votre évaluation' : 'Modifier votre évaluation'}</h3>
                                            <form method="post" action="${pageContext.request.contextPath}/rh/evaluations/enregistrer">
                                                <input type="hidden" name="candidatureId" value="${candidature.idCandidature}">
                                                <input type="hidden" name="entretienId" value="${e.id}">

                                                <div class="grille-3">
                                                    <c:forEach var="crit" items="${criteres}">
                                                        <div class="champ-ligne">
                                                            <label class="etiquette" for="note_${e.id}_${crit.id}"><c:out value="${crit.libelle}"/></label>
                                                            <select class="champ" id="note_${e.id}_${crit.id}" name="note_${crit.id}" required>
                                                                <option value="">—</option>
                                                                <c:forEach var="n" begin="1" end="5">
                                                                    <option value="${n}" ${not empty deja && deja.notesParCritere[crit.id] == n ? 'selected' : ''}>${n}/5</option>
                                                                </c:forEach>
                                                            </select>
                                                        </div>
                                                    </c:forEach>
                                                </div>

                                                <div class="grille" style="margin-top: 16px;">
                                                    <div class="champ-ligne">
                                                        <label class="etiquette" for="recommandation_${e.id}">Recommandation</label>
                                                        <select class="champ" id="recommandation_${e.id}" name="recommandation" required>
                                                            <option value="FAVORABLE" ${not empty deja && deja.recommandation == 'FAVORABLE' ? 'selected' : ''}>Favorable</option>
                                                            <option value="RESERVE" ${not empty deja && deja.recommandation == 'RESERVE' ? 'selected' : ''}>Réservé</option>
                                                            <option value="DEFAVORABLE" ${not empty deja && deja.recommandation == 'DEFAVORABLE' ? 'selected' : ''}>Défavorable</option>
                                                        </select>
                                                    </div>
                                                    <div class="champ-ligne">
                                                        <label class="etiquette" for="commentaire_${e.id}">Commentaire</label>
                                                        <textarea class="champ" id="commentaire_${e.id}" name="commentaire" required><c:out value="${deja.commentaire}"/></textarea>
                                                    </div>
                                                </div>

                                                <div class="actions-bas">
                                                    <button type="submit" class="bouton-principal">Enregistrer l'évaluation</button>
                                                </div>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </section>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</div>
</body>
</html>
