<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Employés et contrats</title>
<link rel="stylesheet" href="${ctx}/assets/css/fonts.css"><link rel="stylesheet" href="${ctx}/assets/css/base.css"><link rel="stylesheet" href="${ctx}/assets/css/besoins.css"><link rel="stylesheet" href="${ctx}/assets/css/composants.css"><link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261108-dossier">
</head><body><div class="c-espace">
<jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="employes"/></jsp:include>
<div class="contenu"><main class="page-espace c-page-liste c-annuaire-rh">
<header class="entete"><div><p class="surtitre">Ressources humaines</p><h1>Employés et contrats</h1><p>Dossiers du personnel et suivi des contrats.</p></div><a class="bouton bouton-principal" href="${ctx}/rh/employes/nouveau">+ Ajouter un employé</a></header>
<c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
<c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>
<section class="carte c-table">
<div class="entete"><h2>Personnel enregistré</h2><p><c:out value="${employes.size()}"/> employé(s) trouvé(s)</p></div>
<form class="filtres" method="get" action="${ctx}/rh/employes">
    <div class="recherche"><input type="search" name="q" value="<c:out value='${q}'/>" class="champ champ-recherche" placeholder="Rechercher par nom, matricule, email, téléphone ou CIN…"></div>
    <div class="filtre"><label for="employe-dep">Département</label><select id="employe-dep" name="departementId" class="champ"><option value="">Tous les départements</option><c:forEach var="d" items="${departements}"><option value="${d.idDepartement}" ${departementId == d.idDepartement ? 'selected' : ''}><c:out value="${d.nom}"/></option></c:forEach></select></div>
    <div class="filtre"><label for="employe-statut">Statut</label><select id="employe-statut" name="statut" class="champ"><option value="">Tous les statuts</option><option value="ACTIF" ${statutFiltre == 'ACTIF' ? 'selected' : ''}>Actif</option><option value="SUSPENDU" ${statutFiltre == 'SUSPENDU' ? 'selected' : ''}>Suspendu</option><option value="SORTI" ${statutFiltre == 'SORTI' ? 'selected' : ''}>Sorti</option></select></div>
    <div class="filtre"><label>&nbsp;</label><span class="dossier-actions-ligne"><button class="bouton bouton-principal" type="submit">Rechercher</button><a class="bouton" href="${ctx}/rh/employes">Réinitialiser</a></span></div>
</form>
<div class="defilement"><table class="table-offres"><thead><tr><th>Matricule</th><th>Employé</th><th>Département</th><th>Poste</th><th>Contact</th><th>Embauché le</th><th>Statut</th><th>Action</th></tr></thead><tbody>
<c:choose><c:when test="${empty employes}"><tr><td colspan="8" class="dossier-empty">Aucun employé ne correspond à la recherche.</td></tr></c:when><c:otherwise><c:forEach var="employe" items="${employes}">
<tr>
<td><strong><c:out value="${employe.matricule}"/></strong></td>
<td><c:out value="${employe.personne.nomComplet}"/></td>
<td><c:out value="${empty employe.departement ? '-' : employe.departement.nom}"/></td>
<td><c:out value="${empty employe.poste ? '-' : employe.poste.intitule}"/></td>
<td><c:out value="${employe.personne.email}"/><c:if test="${not empty employe.personne.telephone}"><span class="paie-muted"><c:out value="${employe.personne.telephone}"/></span></c:if></td>
<td><c:out value="${employe.dateEmbauche}"/></td>
<td><span class="puce ${employe.statut == 'ACTIF' ? 'puce-valide' : 'puce-attente'}"><c:out value="${employe.statut}"/></span></td>
<td class="dossier-actions-ligne"><a class="bouton" href="${ctx}/rh/employes/${employe.id}">Dossier</a><a class="bouton" href="${ctx}/rh/employes/${employe.id}/modifier">Modifier</a></td>
</tr></c:forEach></c:otherwise></c:choose></tbody></table></div></section>
</main></div></div></body></html>
