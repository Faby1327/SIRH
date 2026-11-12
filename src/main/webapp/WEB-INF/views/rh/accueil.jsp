<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demandes RH</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/rh.css">
</head>
<body>
<div class="c-espace"><jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="accueil"/></jsp:include>
<div class="contenu"><main class="page-espace c-page-liste"><header class="entete"><div><p class="surtitre">Ressources humaines</p><h1>Vérification des demandes</h1></div></header>
<c:if test="${not empty message}"><p style="color:#166534; font-weight:600;">${message}</p></c:if><c:if test="${not empty error}"><p style="color:#b91c1c; font-weight:600;">${error}</p></c:if>
<div class="tuiles"><div class="c-tuile"><div class="tuile-label">En attente</div><div class="tuile-valeur">${aVerifier}</div></div><div class="c-tuile"><div class="tuile-label">Validé</div><div class="tuile-valeur">${validees}</div></div><div class="c-tuile"><div class="tuile-label">Refusé</div><div class="tuile-valeur">${refusees}</div></div></div>
<section class="carte c-table c-table-rh" aria-labelledby="titre-demandes"><div class="entete"><h2 id="titre-demandes">Demandes de personnel</h2><p>${demandes.size()} demandes · vérification des demandes soumises par les managers</p></div>
<div class="filtres"><div class="recherche"><input type="search" class="champ champ-recherche" aria-label="Rechercher" placeholder="Rechercher par n°, poste, service…" data-table-search></div><div class="filtre filtre-statut"><label for="rh-statut">Statut RH</label><select id="rh-statut" class="champ" data-table-filter="statut"><option value="">Tous les statuts</option><option value="EN_ATTENTE">En attente</option><option value="VALIDEE">Validé</option><option value="REFUSEE">Refusé</option></select></div><div class="filtre filtre-type"><label for="rh-type">Type de besoin</label><select id="rh-type" class="champ" data-table-filter="type"><option value="">Tous les types</option><option value="CREATION">Création</option><option value="REMPLACEMENT">Remplacement</option></select></div><div class="filtre filtre-service"><label for="rh-service">Service</label><select id="rh-service" class="champ" data-table-filter="service"><option value="">Tous les services</option><c:forEach var="d" items="${departements}"><option value="${d.nom}"><c:out value="${d.nom}"/></option></c:forEach></select></div></div>
<div class="defilement"><div class="tableau" role="table"><div class="ligne ligne-titres" role="row"><div class="titre">N°</div><div class="titre">Poste demandé</div><div class="titre">Service</div><div class="titre">Type de besoin</div><div class="titre">Pers.</div><div class="titre">Demandé par</div><div class="titre">Statut</div><div class="titre">Mise à jour</div><div class="titre">Actions</div></div><div role="rowgroup">
<c:forEach var="demande" items="${demandes}"><c:set var="statutRole" value="${statutsRole[demande.idDemande]}"/><div class="ligne" role="row" data-table-row data-search="DP-${demande.idDemande} ${demande.poste.intitule} ${demande.departement.nom} ${demande.manager.nom}" data-statut="${statutRole}" data-type="${demande.typeRecrutement}" data-service="${demande.departement.nom}"><div class="cellule numero">DP-${demande.idDemande}</div><div class="cellule poste"><c:out value="${demande.poste.intitule}"/></div><div class="cellule"><c:out value="${demande.departement.nom}"/></div><div class="cellule"><c:out value="${demande.typeRecrutementAffiche}"/></div><div class="cellule">${demande.nombrePersonnes}</div><div class="cellule"><c:out value="${demande.manager.nom}"/></div><div class="cellule"><span class="puce ${statutRole == 'REFUSEE' ? 'puce-refuse' : (statutRole == 'VALIDEE' ? 'puce-valide' : 'puce-attente')}"><c:choose><c:when test="${statutRole == 'REFUSEE'}">Refusé</c:when><c:when test="${statutRole == 'VALIDEE'}">Validé</c:when><c:otherwise>En attente</c:otherwise></c:choose></span></div><div class="cellule date">${demande.dateDemandeFormatee}</div><div class="cellule"><a href="${pageContext.request.contextPath}/rh/detail/${demande.idDemande}" class="bouton">Voir</a></div></div></c:forEach>
<c:if test="${empty demandes}"><div class="ligne"><div class="cellule" style="grid-column:1/-1">Aucune demande à afficher.</div></div></c:if>
</div></div></div><p class="legende">Les demandes soumises sont à vérifier par les RH. Après validation, elles sont transmises à la Finance. Utilisez « Voir » pour consulter les informations, valider ou refuser une demande.</p></section>
<section class="carte c-publication" id="offres" aria-labelledby="titre-offres">
    <div class="entete">
        <h2 id="titre-offres">Publication des offres d'emploi</h2>
        <p>Publiez les demandes approuvées par la Direction : les offres apparaissent aussitôt sur la page d'accueil publique, consultable sans connexion.</p>
    </div>

    <c:if test="${not empty messageOffres}"><div class="bandeau" role="status"><span><c:out value="${messageOffres}"/></span></div></c:if>
    <c:if test="${not empty erreurOffres}"><div class="bandeau bandeau-erreur" role="alert"><span><c:out value="${erreurOffres}"/></span></div></c:if>

    <div class="a-publier">
        <h3>À publier</h3>
        <c:choose>
            <c:when test="${empty demandesAPublier}">
                <p class="vide">Aucune demande approuvée en attente de publication.</p>
            </c:when>
            <c:otherwise>
                <div class="liste-a-publier">
                    <c:forEach var="demande" items="${demandesAPublier}">
                        <article class="offre-a-publier">
                            <header>
                                <div class="offre-ref">DP-${demande.idDemande} · <c:out value="${demande.poste.intitule}"/></div>
                                <div class="offre-meta">
                                    <c:out value="${demande.departement.nom}"/>
                                    · ${demande.nombrePersonnes} pers.
                                    · <c:out value="${demande.typeRecrutementAffiche}"/>
                                </div>
                            </header>
                            <form action="${pageContext.request.contextPath}/rh/publier/${demande.idDemande}" method="post">
                                <div class="champ-ligne">
                                    <label for="titre-${demande.idDemande}">Titre de l'offre</label>
                                    <input id="titre-${demande.idDemande}" name="titre" type="text" class="champ" maxlength="200"
                                           value="${fn:escapeXml(demande.offreTitreDefaut)}">
                                </div>
                                <div class="champ-ligne">
                                    <label for="description-${demande.idDemande}">Description</label>
                                    <textarea id="description-${demande.idDemande}" name="description" rows="4" class="champ"><c:out value="${demande.offreDescriptionDefaut}"/></textarea>
                                </div>
                                <button type="submit" class="bouton bouton-principal">Publier l'offre</button>
                            </form>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="offres-publiees">
        <h3>Offres d'emploi</h3>
        <c:choose>
            <c:when test="${empty offres}">
                <p class="vide">Aucune offre publiée pour le moment.</p>
            </c:when>
            <c:otherwise>
                <div class="defilement">
                    <table class="table-offres">
                        <thead>
                            <tr>
                                <th scope="col">Titre</th>
                                <th scope="col">Poste</th>
                                <th scope="col">Service</th>
                                <th scope="col">Publiée le</th>
                                <th scope="col">Statut</th>
                                <th scope="col">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="offre" items="${offres}">
                                <tr>
                                    <td><c:out value="${offre.titre}"/></td>
                                    <td><c:out value="${offre.poste.intitule}"/></td>
                                    <td><c:out value="${offre.departement.nom}"/></td>
                                    <td>${offre.datePublicationFormatee}</td>
                                    <td><span class="puce ${offre.publiee ? 'puce-valide' : 'puce-brouillon'}">${offre.statutAffiche}</span></td>
                                    <td class="actions-offre">
                                        <a class="bouton" href="${pageContext.request.contextPath}/rh/offre/${offre.idOffre}/candidatures">Candidatures (${candidaturesParOffre[offre.idOffre]})</a>
                                        <a class="bouton" href="${pageContext.request.contextPath}/rh/offre/${offre.idOffre}/embauches">Embaucher</a>
                                        <c:choose>
                                            <c:when test="${offre.publiee}">
                                                <a class="bouton" href="${pageContext.request.contextPath}/offres/${offre.idOffre}" target="_blank" rel="noopener">Voir</a>
                                                <form action="${pageContext.request.contextPath}/rh/cloturer/${offre.idOffre}" method="post">
                                                    <button type="submit" class="bouton bouton-danger">Clôturer</button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="texte">Retirée du site</span>
                                            </c:otherwise>
                                        </c:choose>
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
</main></div></div>
<script src="${pageContext.request.contextPath}/assets/js/table-filtres.js"></script>
</body>
</html>





