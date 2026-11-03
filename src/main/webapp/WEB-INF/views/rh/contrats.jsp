<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contrats - Ressources humaines</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
    <link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261103-dossier">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp">
        <jsp:param name="role" value="rh"/>
        <jsp:param name="active" value="contrats"/>
    </jsp:include>
    <div class="contenu">
        <main class="page-espace c-page-liste c-annuaire-rh">
            <header class="entete">
                <div>
                    <p class="surtitre">Ressources humaines</p>
                    <h1>Contrats</h1>
                    <p>Vue globale de tous les contrats enregistrés, en cours ou terminés.</p>
                </div>
            </header>
            <section class="carte c-table">
                <div class="entete">
                    <h2>Liste des contrats</h2>
                    <p><c:out value="${contrats.size()}"/> contrat(s) enregistré(s)</p>
                </div>
                <div class="filtres"><div class="recherche"><input type="search" class="champ champ-recherche" placeholder="Rechercher par employé, matricule ou poste…" data-table-search></div><div class="filtre"><label for="contrat-statut">Statut</label><select id="contrat-statut" class="champ" data-table-filter="statut"><option value="">Tous les statuts</option><option value="EN_COURS">En cours</option><option value="TERMINE">Terminé</option><option value="ANNULE">Annulé</option></select></div><div class="filtre"><label for="contrat-tri">Trier par</label><select id="contrat-tri" class="champ" data-table-sort><option value="">Ordre par défaut</option><option value="name-asc">Employé (A → Z)</option><option value="name-desc">Employé (Z → A)</option><option value="date-desc">Début récent</option><option value="date-asc">Début ancien</option></select></div></div>
                <div class="defilement">
                    <table class="table-offres">
                        <thead><tr><th>Employé</th><th>Matricule</th><th>Poste</th><th>Type</th><th>Début</th><th>Fin</th><th>Salaire</th><th>Statut</th><th>Action</th></tr></thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty contrats}"><tr><td colspan="9">Aucun contrat enregistré.</td></tr></c:when>
                            <c:otherwise>
                                <c:forEach var="contrat" items="${contrats}">
                                    <tr data-table-row data-search="${contrat.employe.personne.nomComplet} ${contrat.employe.matricule} ${contrat.poste.intitule} ${contrat.typeContrat}" data-statut="${contrat.statut}" data-name="${contrat.employe.personne.nomComplet}" data-date="${contrat.dateDebut}">
                                        <td><c:out value="${contrat.employe.personne.nomComplet}"/></td>
                                        <td><strong><c:out value="${contrat.employe.matricule}"/></strong></td>
                                        <td><c:out value="${contrat.poste.intitule}"/></td>
                                        <td><c:out value="${contrat.typeContrat}"/></td>
                                        <td><c:out value="${contrat.dateDebut}"/></td>
                                        <td><c:out value="${empty contrat.dateFin ? '-' : contrat.dateFin}"/></td>
                                        <td><c:out value="${contrat.salaireMensuel}"/> Ar</td>
                                        <td><c:out value="${contrat.statut}"/></td>
                                        <td><a class="bouton" href="${ctx}/rh/employes/${contrat.employe.id}">Voir l'employé</a></td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
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
