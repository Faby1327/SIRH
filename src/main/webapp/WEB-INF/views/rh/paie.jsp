<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fiches de paie - RH</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
    <link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261103-paie">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp">
        <jsp:param name="role" value="rh"/>
        <jsp:param name="active" value="paie"/>
    </jsp:include>
    <div class="contenu">
        <main class="page-espace c-page-liste c-annuaire-rh c-paie-page">
            <header class="entete">
                <div><p class="surtitre">Ressources humaines</p><h1>Fiches de paie</h1><p>Fiches générées automatiquement à partir des contrats actifs.</p></div>
                <span class="paie-period"><c:out value="${moisCourant}"/></span>
            </header>
            <c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>
            <section class="carte c-table dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-blue">01</span><div><h2>Paie du mois</h2><p><c:out value="${fiches.size()}"/> fiche(s) générée(s) depuis les contrats en cours</p></div></div></div>
                <div class="filtres"><div class="recherche"><input type="search" class="champ champ-recherche" placeholder="Rechercher par employé, matricule ou poste…" data-table-search></div><div class="filtre"><label for="paie-statut">Statut</label><select id="paie-statut" class="champ" data-table-filter="statut"><option value="">Tous les statuts</option><option value="BROUILLON">Brouillon</option><option value="VALIDEE">Validée</option><option value="PAYEE">Payée</option><option value="ANNULEE">Annulée</option></select></div><div class="filtre"><label for="paie-tri">Trier par</label><select id="paie-tri" class="champ" data-table-sort><option value="">Ordre par défaut</option><option value="name-asc">Employé (A → Z)</option><option value="name-desc">Employé (Z → A)</option><option value="amount-desc">Montant décroissant</option><option value="amount-asc">Montant croissant</option></select></div></div>
                <div class="defilement">
                    <table class="table-offres">
                        <thead><tr><th>Employé</th><th>Contrat</th><th>Période</th><th>Salaire net</th><th>Statut</th><th>Action</th></tr></thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty fiches}"><tr><td colspan="6" class="dossier-empty">Aucune fiche de paie à traiter pour ce mois.</td></tr></c:when>
                            <c:otherwise><c:forEach var="fiche" items="${fiches}">
                                <tr data-table-row data-search="${fiche.employe.personne.nomComplet} ${fiche.employe.matricule} ${fiche.contrat.poste.intitule} ${fiche.contrat.typeContrat}" data-statut="${fiche.statut}" data-name="${fiche.employe.personne.nomComplet}" data-amount="${fiche.netAPayer}">
                                    <td><strong><c:out value="${fiche.employe.personne.nomComplet}"/></strong><small class="paie-muted"><c:out value="${fiche.employe.matricule}"/></small></td>
                                    <td><c:out value="${fiche.contrat.typeContrat}"/> · <c:out value="${fiche.contrat.poste.intitule}"/></td>
                                    <td><c:out value="${fiche.mois}"/>/<c:out value="${fiche.annee}"/></td>
                                    <td><strong><c:out value="${fiche.netAPayer}"/> Ar</strong></td>
                                    <td><span class="paie-status paie-status-${fiche.statut}"><c:out value="${fiche.statut}"/></span></td>
                                    <td><a class="bouton" href="${ctx}/rh/paie/${fiche.id}">Consulter</a></td>
                                </tr>
                            </c:forEach></c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</div>
</body>
<script src="${ctx}/assets/js/table-filtres.js"></script>
</html>
