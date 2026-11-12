<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Candidatures · RH</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/rh.css">
</head>
<body>
<div class="c-espace"><jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="candidatures"/></jsp:include>
<div class="contenu"><main class="page-espace c-page-liste">
    <header class="entete">
        <a href="${pageContext.request.contextPath}/rh/accueil#offres" class="retour">← Toutes les offres</a>
        <div>
            <p class="surtitre">Ressources humaines</p>
            <h1>Candidatures</h1>
        </div>
    </header>

    <section class="carte c-publication" aria-labelledby="titre-candidatures">
        <div class="entete">
            <h2 id="titre-candidatures"><c:out value="${offre.titre}"/></h2>
            <p>
                <c:out value="${offre.poste.intitule}"/>
                · <c:out value="${offre.departement.nom}"/>
                · ${candidatures.size()} candidature<c:if test="${candidatures.size() > 1}">s</c:if>
            </p>
        </div>

        <c:if test="${not empty messagePreselection}">
            <div class="bandeau" role="status"><span><c:out value="${messagePreselection}"/></span></div>
        </c:if>
        <c:if test="${not empty erreurPreselection}">
            <div class="bandeau bandeau-erreur" role="alert"><span><c:out value="${erreurPreselection}"/></span></div>
        </c:if>

        <div style="margin-bottom:16px;">
            <a href="${pageContext.request.contextPath}/rh/offre/${offre.idOffre}/embauches" class="bouton">Embaucher / Proposer</a>
        </div>

        <div class="offres-publiees">
            <c:choose>
                <c:when test="${empty candidatures}">
                    <p class="vide">Aucune candidature reçue pour cette offre pour le moment.</p>
                </c:when>
                <c:otherwise>
                    <div class="resume-preselection">
                        <span class="puce puce-valide">Présélection</span>
                        <span>
                            <strong>${nombrePreselectionnes}</strong>
                            candidat<c:if test="${nombrePreselectionnes > 1}">s</c:if>
                            présélectionné<c:if test="${nombrePreselectionnes > 1}">s</c:if>
                            sur ${candidatures.size()}
                        </span>
                    </div>

                    <form class="form-preselection"
                          action="${pageContext.request.contextPath}/rh/offre/${offre.idOffre}/preselection"
                          method="post">
                        <div class="defilement">
                            <table class="table-offres">
                                <thead>
                                    <tr>
                                        <th scope="col">Candidat</th>
                                        <th scope="col">Email</th>
                                        <th scope="col">Téléphone</th>
                                        <th scope="col">Reçue le</th>
                                        <th scope="col">Statut</th>
                                        <th scope="col">Présélection</th>
                                        <th scope="col">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="c" items="${candidatures}">
                                        <tr>
                                            <td><c:out value="${c.nomComplet}"/></td>
                                            <td><c:out value="${c.email}"/></td>
                                            <td><c:out value="${c.telephone}"/></td>
                                            <td>${c.dateCandidatureFormatee}</td>
                                            <td>
                                                <span class="puce ${c.statut == 'RETENUE' ? 'puce-valide' : (c.statut == 'REFUSEE' ? 'puce-refuse' : (c.statut == 'PRESELECTIONNEE' or c.preselectionnee) ? 'puce-valide' : (c.statut == 'EVALUEE') ? 'puce-info' : 'puce-attente')}">
                                                    ${c.statutAffiche}
                                                </span>
                                            </td>
                                            <td class="cellule-preselection">
                                                <c:if test="${c.statut == 'NOUVELLE'}">
                                                    <input type="checkbox" name="candidatureIds" value="${c.idCandidature}"
                                                           aria-label="Présélectionner ${fn:escapeXml(c.nomComplet)}">
                                                </c:if>
                                            </td>
                                            <td>
                                                <a class="bouton" href="${pageContext.request.contextPath}/rh/candidature/${c.idCandidature}">Consulter</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="actions-preselection">
                            <div class="champ-ligne">
                                <label for="note">Note de présélection <span class="facultatif">(facultatif)</span></label>
                                <input id="note" name="note" type="text" class="champ" maxlength="500"
                                       placeholder="Ex. : profil à rencontrer en priorité">
                                <p class="aide">La note est appliquée aux candidats cochés.</p>
                            </div>
                            <button type="submit" class="bouton bouton-principal">Enregistrer la présélection</button>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</main></div></div>
</body>
</html>
