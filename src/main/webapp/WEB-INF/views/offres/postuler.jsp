<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Postuler · <c:out value="${offre.titre}"/></title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
    <link rel="stylesheet" href="${ctx}/assets/css/public.css">
    <link rel="stylesheet" href="${ctx}/assets/css/offres.css">
</head>
<body class="public">
<jsp:include page="/WEB-INF/views/fragments/public-entete.jsp">
    <jsp:param name="active" value="offres"/>
</jsp:include>

<main class="public-section" style="padding-top:36px">
    <div class="public-conteneur">
        <a class="public-retour" href="${ctx}/offres/${offre.idOffre}">← Retour à l'offre</a>

        <div class="c-offres embed" style="margin-top:16px">
            <article class="carte-offre">
                <div class="offre-entete">
                    <span class="puce puce-valide"><c:out value="${offre.typeRecrutementAffiche}"/></span>
                    <span class="offre-date">Réf. <c:out value="${offre.reference}"/></span>
                </div>

                <h1><c:out value="${offre.titre}"/></h1>
                <p class="offre-meta">
                    Service <c:out value="${offre.departement.nom}"/>
                    · ${offre.nombrePersonnes} poste<c:if test="${offre.nombrePersonnes > 1}">s</c:if>
                </p>

                <c:if test="${not empty error}">
                    <div class="bandeau-erreur" role="alert"><c:out value="${error}"/></div>
                </c:if>

                <form class="form-candidature" action="${ctx}/offres/${offre.idOffre}/postuler" method="post">
                    <fieldset>
                        <legend>Vos informations</legend>
                        <p class="note-profil">
                            Ces informations proviennent de votre compte candidat et ne peuvent pas
                            être modifiées depuis ce formulaire.
                        </p>

                        <div class="grille-champs">
                            <div class="champ-ligne">
                                <label for="prenom">Prénom</label>
                                <input class="champ" id="prenom" name="prenom" type="text" readonly
                                       value="${fn:escapeXml(candidat.prenom)}">
                            </div>
                            <div class="champ-ligne">
                                <label for="nom">Nom</label>
                                <input class="champ" id="nom" name="nom" type="text" readonly
                                       value="${fn:escapeXml(candidat.nom)}">
                            </div>
                            <div class="champ-ligne">
                                <label for="email">Email</label>
                                <input class="champ" id="email" name="email" type="email" readonly
                                       value="${fn:escapeXml(candidat.email)}">
                            </div>
                            <div class="champ-ligne">
                                <label for="telephone">Téléphone</label>
                                <input class="champ" id="telephone" name="telephone" type="tel" readonly
                                       value="${fn:escapeXml(candidat.telephone)}">
                            </div>
                        </div>

                        <div class="champ-ligne">
                            <label for="message">Message <span class="facultatif">(facultatif)</span></label>
                            <textarea class="champ" id="message" name="message" rows="4"
                                      maxlength="2000">${fn:escapeXml(message)}</textarea>
                        </div>
                    </fieldset>

                    <fieldset>
                        <legend>Votre profil pour ce poste</legend>

                        <c:forEach var="critere" items="${criteres}">
                            <c:set var="valeur" value="${valeurs[critere.idCritere]}"/>
                            <div class="champ-ligne">
                                <span class="etiquette">
                                    <c:out value="${critere.libelle}"/>
                                    <c:choose>
                                        <c:when test="${critere.obligatoire}"><span class="obligatoire">*</span></c:when>
                                        <c:otherwise><span class="facultatif">(facultatif)</span></c:otherwise>
                                    </c:choose>
                                </span>

                                <c:choose>
                                    <c:when test="${critere.liste}">
                                        <select class="champ" name="critere_${critere.idCritere}"
                                                ${critere.obligatoire ? 'required' : ''}>
                                            <option value="">— Choisir —</option>
                                            <c:forEach var="option" items="${critere.optionsListe}">
                                                <option value="${fn:escapeXml(option)}"
                                                    ${critere.estSelectionnee(valeur, option) ? 'selected' : ''}>
                                                    <c:out value="${option}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </c:when>

                                    <c:when test="${critere.cases}">
                                        <div class="choix-groupe">
                                            <c:forEach var="option" items="${critere.optionsListe}">
                                                <label class="choix">
                                                    <input type="checkbox" name="critere_${critere.idCritere}"
                                                           value="${fn:escapeXml(option)}"
                                                        ${critere.estSelectionnee(valeur, option) ? 'checked' : ''}>
                                                    <c:out value="${option}"/>
                                                </label>
                                            </c:forEach>
                                        </div>
                                    </c:when>

                                    <c:when test="${critere.ouiNon}">
                                        <div class="choix-groupe">
                                            <label class="choix">
                                                <input type="radio" name="critere_${critere.idCritere}" value="OUI"
                                                    ${critere.estSelectionnee(valeur, 'OUI') ? 'checked' : ''}
                                                    ${critere.obligatoire ? 'required' : ''}>
                                                Oui
                                            </label>
                                            <label class="choix">
                                                <input type="radio" name="critere_${critere.idCritere}" value="NON"
                                                    ${critere.estSelectionnee(valeur, 'NON') ? 'checked' : ''}
                                                    ${critere.obligatoire ? 'required' : ''}>
                                                Non
                                            </label>
                                        </div>
                                    </c:when>

                                    <c:when test="${critere.nombre}">
                                        <input class="champ champ-court" type="number" min="0"
                                               name="critere_${critere.idCritere}"
                                               value="${fn:escapeXml(valeur)}"
                                            ${critere.obligatoire ? 'required' : ''}>
                                    </c:when>

                                    <c:otherwise>
                                        <input class="champ" type="text" maxlength="200"
                                               name="critere_${critere.idCritere}"
                                               value="${fn:escapeXml(valeur)}"
                                            ${critere.obligatoire ? 'required' : ''}>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </fieldset>

                    <div class="actions-formulaire">
                        <button type="submit" class="bouton-offre">Envoyer ma candidature</button>
                        <a class="retour-lien" href="${ctx}/offres/${offre.idOffre}">Annuler</a>
                    </div>
                </form>
            </article>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/fragments/public-pied.jsp"/>
</body>
</html>
