<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de bord Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/decision.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/manager.css">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="manager"/><jsp:param name="active" value="accueil"/></jsp:include>

    <div class="contenu">
        <main class="page-espace c-page-liste">
            <header class="entete"><div><p class="surtitre">Manager · <c:out value="${utilisateurConnecte.departement.nom}"/></p><h1>Tableau de bord</h1></div><a href="${pageContext.request.contextPath}/manager/nouvelle" class="bouton-principal">＋ Nouvelle demande</a></header>

            <div class="tuiles" aria-label="Statistiques de mes demandes">
                <div class="c-tuile"><span class="tuile-label">Demandes</span><span class="tuile-valeur">${nbDemandes}</span></div>
                <div class="c-tuile"><span class="tuile-label">Brouillon</span><span class="tuile-valeur">${nbBrouillons}</span></div>
                <div class="c-tuile"><span class="tuile-label">Validé</span><span class="tuile-valeur">${nbValidees}</span></div>
            </div>

            <c:if test="${not empty message}"><div class="bandeau" role="status"><span><c:out value="${message}"/></span></div></c:if>
            <c:if test="${not empty error}"><div class="bandeau bandeau-erreur" role="alert"><span><c:out value="${error}"/></span></div></c:if>

            <section class="carte c-table c-table-manager" aria-labelledby="titre-demandes">
                <div class="entete"><h2 id="titre-demandes">Mes demandes de personnel</h2><p>${demandes.size()} demandes · suivi de l’avancement dans le circuit de validation</p></div>
                <div class="filtres"><div class="recherche"><input type="search" class="champ champ-recherche" aria-label="Rechercher" placeholder="Rechercher par n°, poste, statut, date…" data-table-search></div><div class="filtre filtre-statut"><label for="filtre-statut">Statut Manager</label><select id="filtre-statut" class="champ" data-table-filter="statut"><option value="">Tous les statuts</option><option value="BROUILLON">Brouillon</option><option value="VALIDE_MANAGER">Validé</option></select></div><div class="filtre filtre-type"><label for="filtre-type">Type de besoin</label><select id="filtre-type" class="champ" data-table-filter="type"><option value="">Tous les types</option><option value="CREATION">Création de poste</option><option value="REMPLACEMENT">Remplacement</option></select></div></div>
                <div class="defilement"><div class="tableau" role="table" aria-labelledby="titre-demandes">
                    <div class="ligne ligne-titres" role="row">
                        <div class="titre" role="columnheader">N°</div>
                        <div class="titre" role="columnheader">Poste</div>
                        <div class="titre" role="columnheader">Type de besoin</div>
                        <div class="titre" role="columnheader">Pers.</div>
                        <div class="titre" role="columnheader">Statut</div>
                        <div class="titre" role="columnheader">Avancement</div>
                        <div class="titre" role="columnheader">Date</div>
                        <div class="titre" role="columnheader">Actions</div>
                    </div>
                    <div role="rowgroup">
                        <c:forEach var="demande" items="${demandes}">
                            <c:set var="statut" value="${demande.statutActuel.libelle}"/>
                            <c:set var="etapeRefus" value="${etapesRefus[demande.idDemande]}"/>
                            <div class="ligne" role="row" data-table-row data-search="DP-${demande.idDemande} ${demande.poste.intitule} ${demande.statutActuel.libelle} ${demande.dateDemandeFormatee}" data-statut="${statut == 'BROUILLON' ? 'BROUILLON' : 'VALIDE_MANAGER'}" data-type="${demande.typeRecrutement}">
                                <div class="cellule numero" role="cell">DP-${demande.idDemande}</div>
                                <div class="cellule poste" role="cell"><c:out value="${demande.poste.intitule}"/></div>
                                <div class="cellule" role="cell"><c:choose><c:when test="${demande.typeRecrutement == 'REMPLACEMENT'}">Remplacement</c:when><c:otherwise>Création</c:otherwise></c:choose></div>
                                <div class="cellule" role="cell">${demande.nombrePersonnes}</div>
                                <div class="cellule" role="cell"><span class="puce ${statut == 'BROUILLON' ? 'puce-brouillon' : 'puce-valide'}"><c:choose><c:when test="${statut == 'BROUILLON'}">Brouillon</c:when><c:otherwise>Validé</c:otherwise></c:choose></span></div>
                                <div class="cellule" role="cell">
                                    <div class="c-stepbar">
                                        <c:choose>
                                            <c:when test="${statut == 'BROUILLON'}"><span class="libelle">Non soumise</span><div class="barre"><span class="segment a-faire"></span><span class="segment a-faire"></span><span class="segment a-faire"></span><span class="segment a-faire"></span></div></c:when>
                                            <c:when test="${statut == 'SOUMISE'}"><span class="libelle">En attente RH</span><div class="barre"><span class="segment faite"></span><span class="segment en-cours"></span><span class="segment a-faire"></span><span class="segment a-faire"></span></div></c:when>
                                            <c:when test="${statut == 'EN_VERIFICATION_FINANCIERE'}"><span class="libelle">En attente Finance</span><div class="barre"><span class="segment faite"></span><span class="segment faite"></span><span class="segment en-cours"></span><span class="segment a-faire"></span></div></c:when>
                                            <c:when test="${statut == 'BUDGET_INSUFFISANT'}"><span class="libelle">Budget insuffisant</span><div class="barre"><span class="segment faite"></span><span class="segment faite"></span><span class="segment refusee"></span><span class="segment a-faire"></span></div></c:when>
                                            <c:when test="${statut == 'EN_ATTENTE_VALIDATION'}"><span class="libelle">En attente Direction</span><div class="barre"><span class="segment faite"></span><span class="segment faite"></span><span class="segment faite"></span><span class="segment en-cours"></span></div></c:when>
                                            <c:when test="${statut == 'VALIDEE'}"><span class="libelle">Approuvée</span><div class="barre"><span class="segment approuvee"></span><span class="segment approuvee"></span><span class="segment approuvee"></span><span class="segment approuvee"></span></div></c:when>
                                            <c:when test="${statut == 'REFUSEE'}"><span class="libelle"><c:choose><c:when test="${etapeRefus == 2}">Refusée par RH</c:when><c:when test="${etapeRefus == 3}">Refusée par Finance</c:when><c:otherwise>Refusée par le Directeur</c:otherwise></c:choose></span><div class="barre"><span class="segment faite"></span><span class="segment ${etapeRefus == 2 ? 'refusee' : (etapeRefus > 2 ? 'faite' : 'a-faire')}"></span><span class="segment ${etapeRefus == 3 ? 'refusee' : (etapeRefus > 3 ? 'faite' : 'a-faire')}"></span><span class="segment ${etapeRefus == 4 ? 'refusee' : 'a-faire'}"></span></div></c:when>
                                            <c:otherwise><span class="libelle">Annulée</span><div class="barre"><span class="segment faite"></span><span class="segment a-faire"></span><span class="segment a-faire"></span><span class="segment a-faire"></span></div></c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div class="cellule date" role="cell">${demande.dateDemandeFormatee}</div>
                                <div class="cellule actions" role="cell">
                                    <a href="${pageContext.request.contextPath}/manager/detail/${demande.idDemande}" class="bouton">Voir</a>
                                    <form action="${pageContext.request.contextPath}/manager/delete/${demande.idDemande}" method="post" style="display:inline;">
                                        <c:choose><c:when test="${statut == 'REFUSEE' || statut == 'BUDGET_INSUFFISANT'}"><button type="submit" class="corbeille" aria-label="Supprimer">×</button></c:when><c:otherwise><button type="button" class="corbeille" disabled aria-label="Suppression possible uniquement après refus">×</button></c:otherwise></c:choose>
                                    </form>
                                </div>
                            </div>
                        </c:forEach>
                        <c:if test="${empty demandes}"><div class="ligne"><div class="cellule" style="grid-column:1/-1">Aucune demande pour le moment. <a href="${pageContext.request.contextPath}/manager/nouvelle">Créer votre première demande</a>.</div></div></c:if>
                    </div>
                </div></div>
                <p class="legende">Avancement : 1 Manager · 2 Vérification RH · 3 Vérification financière · 4 Direction. Une demande approuvée affiche les quatre segments verts. Brouillon : non soumise. Les demandes refusées peuvent être supprimées.</p>
            </section>
        </main>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/table-filtres.js"></script>
</body>
</html>
