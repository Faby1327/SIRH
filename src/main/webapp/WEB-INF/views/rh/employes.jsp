<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Employés et contrats</title>
<link rel="stylesheet" href="${ctx}/assets/css/fonts.css"><link rel="stylesheet" href="${ctx}/assets/css/base.css"><link rel="stylesheet" href="${ctx}/assets/css/besoins.css"><link rel="stylesheet" href="${ctx}/assets/css/composants.css"><link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261103-annuaire">
</head><body><div class="c-espace">
<jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="employes"/></jsp:include>
<div class="contenu"><main class="page-espace c-page-liste c-annuaire-rh">
<header class="entete"><div><p class="surtitre">Ressources humaines</p><h1>Employés et contrats</h1><p>Personnel enregistré et suivi des contrats actifs.</p></div></header>
<c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
<c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>
<section class="carte c-table">
<div class="entete"><h2>Personnel enregistré</h2><p><c:out value="${employes.size()}"/> employé(s) créé(s)</p></div>
<form class="filtres" method="get" action="${ctx}/rh/employes"><div class="recherche"><input type="search" class="champ champ-recherche" name="q" value="${q}" placeholder="Rechercher par nom, matricule ou email…" data-table-search></div><div class="filtre"><label for="employe-departement">Département</label><select id="employe-departement" class="champ" name="departementId" data-table-filter="departement"><option value="">Tous les départements</option><c:forEach var="departement" items="${departements}"><option value="${departement.idDepartement}" ${departementId == departement.idDepartement ? 'selected' : ''}><c:out value="${departement.nom}"/></option></c:forEach></select></div><div class="filtre"><label for="employe-statut">Statut</label><select id="employe-statut" class="champ" name="statut" data-table-filter="statut"><option value="">Tous les statuts</option><option value="ACTIF" ${statutFiltre == 'ACTIF' ? 'selected' : ''}>Actif</option><option value="SUSPENDU" ${statutFiltre == 'SUSPENDU' ? 'selected' : ''}>Suspendu</option><option value="SORTI" ${statutFiltre == 'SORTI' ? 'selected' : ''}>Sorti</option></select></div><div class="filtre"><label for="employe-tri">Trier par</label><select id="employe-tri" class="champ" data-table-sort><option value="">Ordre par défaut</option><option value="name-asc">Nom (A → Z)</option><option value="name-desc">Nom (Z → A)</option><option value="date-desc">Date d'embauche récente</option><option value="date-asc">Date d'embauche ancienne</option></select></div><button class="bouton" type="submit">Rechercher</button></form>
<div class="defilement"><table class="table-offres"><thead><tr><th>Matricule</th><th>Employé</th><th>Contact</th><th>Embauché le</th><th>Statut</th><th>Action</th></tr></thead><tbody>
<c:choose><c:when test="${empty employes}"><tr><td colspan="6">Aucun employé n'a encore été créé.</td></c:when><c:otherwise><c:forEach var="employe" items="${employes}">
<tr data-table-row data-search="${employe.personne.nomComplet} ${employe.matricule} ${employe.personne.email}" data-departement="${employe.departement.idDepartement}" data-statut="${employe.statut}" data-name="${employe.personne.nomComplet}" data-date="${employe.dateEmbauche}">
<td><strong><c:out value="${employe.matricule}"/></strong></td><td><c:out value="${employe.personne.nomComplet}"/></td><td><c:out value="${employe.personne.email}"/></td><td><c:out value="${employe.dateEmbauche}"/></td>
<td><span class="puce ${employe.statut == 'ACTIF' ? 'puce-valide' : 'puce-attente'}"><c:out value="${employe.statut}"/></span></td><td><a class="bouton" href="${ctx}/rh/employes/${employe.id}">Voir le dossier</a></td>
</tr></c:forEach></c:otherwise></c:choose></tbody></table></div></section>
</main></div></div><script src="${ctx}/assets/js/table-filtres.js"></script></body></html>
