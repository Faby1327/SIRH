<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Heures supplémentaires - RH</title>
<link rel="stylesheet" href="${ctx}/assets/css/fonts.css"><link rel="stylesheet" href="${ctx}/assets/css/base.css"><link rel="stylesheet" href="${ctx}/assets/css/besoins.css"><link rel="stylesheet" href="${ctx}/assets/css/composants.css"><link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261109-heures-supp">
</head><body><div class="c-espace">
<jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="heures-supplementaires"/></jsp:include>
<div class="contenu"><main class="page-espace c-page-liste c-annuaire-rh c-heures-supp-page">
<header class="entete"><div><p class="surtitre">Temps de travail</p><h1>Heures supplémentaires</h1><p>Validez les heures calculées à partir des pointages RH avant leur intégration dans la paie.</p></div></header>
<c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
<c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>
<section class="carte c-table heures-supp-card"><div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-green">01</span><div><h2>Demandes enregistrées</h2><p><c:out value="${heuresSupplementaires.size()}"/> demande(s)</p></div></div></div>
<div class="filtres"><div class="recherche"><input type="search" class="champ champ-recherche" placeholder="Rechercher un employé…" data-table-search></div><div class="filtre"><label for="heure-statut">Statut</label><select id="heure-statut" class="champ" data-table-filter="statut"><option value="">Tous les statuts</option><option value="DEMANDEE">Demandée</option><option value="VALIDEE">Validée</option><option value="REFUSEE">Refusée</option></select></div></div>
<div class="defilement"><table class="table-offres heures-supp-table"><thead><tr><th>Employé</th><th>Date</th><th>Plage</th><th>Durée</th><th>Motif</th><th>Statut</th><th>Action</th></tr></thead><tbody>
<c:choose><c:when test="${empty heuresSupplementaires}"><tr><td colspan="7" class="dossier-empty">Aucune heure supplémentaire enregistrée.</td></tr></c:when><c:otherwise><c:forEach var="heure" items="${heuresSupplementaires}"><tr data-table-row data-search="${heure.employe.personne.nomComplet} ${heure.employe.matricule}" data-statut="${heure.statut}">
<td><strong><c:out value="${heure.employe.personne.nomComplet}"/></strong><small class="heures-supp-muted"><c:out value="${heure.employe.matricule}"/></small></td><td><c:out value="${heure.dateTravail}"/></td><td><c:out value="${heure.heureDebut}"/> – <c:out value="${heure.heureFin}"/></td><td><strong><c:out value="${heure.nombreMinutes}"/> min</strong></td><td><c:out value="${heure.motif}"/></td><td><span class="heures-supp-status heures-supp-status-${heure.statut}"><c:out value="${heure.statut}"/></span></td>
<td><c:if test="${heure.statut == 'DEMANDEE'}"><form class="heures-supp-action" method="post" action="${ctx}/rh/heures-supplementaires/${heure.id}/valider"><button class="bouton heures-supp-validate" type="submit">Valider</button></form><form class="heures-supp-action" method="post" action="${ctx}/rh/heures-supplementaires/${heure.id}/refuser"><button class="bouton heures-supp-refuse" type="submit">Refuser</button></form></c:if><c:if test="${heure.statut != 'DEMANDEE'}"><span class="heures-supp-muted">Traitée</span></c:if></td>
</tr></c:forEach></c:otherwise></c:choose></tbody></table></div></section>
</main></div></div><script src="${ctx}/assets/js/table-filtres.js"></script></body></html>
