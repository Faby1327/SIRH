<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Affectations horaires - RH</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
    <link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261106-planning-affectations">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp">
        <jsp:param name="role" value="rh"/>
        <jsp:param name="active" value="planning"/>
    </jsp:include>
    <div class="contenu">
        <main class="page-espace c-page-liste c-annuaire-rh c-planning-page">
            <header class="entete planning-header">
                <div>
                    <p class="surtitre">Organisation du temps de travail</p>
                    <h1>Affectations horaires</h1>
                    <p>Associez un régime et un horaire aux employés pendant une période donnée.</p>
                </div>
                <div class="planning-header-badge"><span>01</span><strong>Planning</strong></div>
            </header>

            <c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>

            <section class="carte c-table planning-card">
                <div class="dossier-card-heading">
                    <div><span class="dossier-icon dossier-icon-blue">+</span><div><h2>Nouvelle affectation</h2><p>Définissez l'horaire habituel d'un employé.</p></div></div>
                </div>
                <form class="planning-form" action="${ctx}/rh/planning/affectations" method="post">
                    <label><span>Employé</span>
                        <select name="employeId" class="champ" required>
                            <option value="">Sélectionner un employé</option>
                            <c:forEach var="employe" items="${employes}">
                                <option value="${employe.id}"><c:out value="${employe.matricule}"/> · <c:out value="${employe.personne.nomComplet}"/></option>
                            </c:forEach>
                        </select>
                    </label>
                    <label><span>Régime de travail</span>
                        <select name="regimeId" class="champ" required>
                            <option value="">Sélectionner un régime</option>
                            <c:forEach var="regime" items="${regimes}">
                                <option value="${regime.id}"><c:out value="${regime.nom}"/> · <c:out value="${regime.heuresHebdomadaires}"/> h/semaine</option>
                            </c:forEach>
                        </select>
                    </label>
                    <label><span>Modèle horaire</span>
                        <select name="modeleId" class="champ" required>
                            <option value="">Sélectionner un horaire</option>
                            <c:forEach var="modele" items="${modeles}">
                                <option value="${modele.id}"><c:out value="${modele.nom}"/> · <c:out value="${modele.heureDebut}"/>–<c:out value="${modele.heureFin}"/></option>
                            </c:forEach>
                        </select>
                    </label>
                    <label><span>Date de début</span><input class="champ" type="date" name="dateDebut" required></label>
                    <label><span>Date de fin <small>(facultative)</small></span><input class="champ" type="date" name="dateFin"></label>
                    <fieldset class="jours-fieldset">
                        <legend>Jours travaillés</legend>
                        <div class="jours-options">
                            <c:forEach var="jour" items="${['Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi','Dimanche']}" varStatus="status">
                                <label class="jour-option"><input type="checkbox" name="jours" value="${status.index + 1}" <c:if test="${status.index < 5}">checked</c:if>><span><c:out value="${jour}"/></span></label>
                            </c:forEach>
                        </div>
                    </fieldset>
                    <div class="planning-submit"><button class="bouton-principal" type="submit">Créer l'affectation</button></div>
                </form>
            </section>

            <section class="carte c-table planning-card">
                <div class="dossier-card-heading">
                    <div><span class="dossier-icon dossier-icon-green">02</span><div><h2>Affectations enregistrées</h2><p><c:out value="${affectations.size()}"/> affectation(s) définie(s)</p></div></div>
                </div>
                <div class="filtres"><div class="recherche"><input type="search" class="champ champ-recherche" placeholder="Rechercher un employé ou un horaire…" data-table-search></div></div>
                <div class="defilement">
                    <table class="table-offres planning-table">
                        <thead><tr><th>Employé</th><th>Régime</th><th>Horaire</th><th>Jours</th><th>Période</th><th>Statut</th></tr></thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty affectations}"><tr><td colspan="6" class="dossier-empty">Aucune affectation horaire enregistrée.</td></tr></c:when>
                            <c:otherwise><c:forEach var="affectation" items="${affectations}">
                                <tr data-table-row data-search="${affectation.employe.matricule} ${affectation.employe.personne.nomComplet} ${affectation.regimeTravail.nom} ${affectation.modeleHoraire.nom}">
                                    <td><strong><c:out value="${affectation.employe.personne.nomComplet}"/></strong><small class="planning-muted"><c:out value="${affectation.employe.matricule}"/></small></td>
                                    <td><c:out value="${affectation.regimeTravail.nom}"/></td>
                                    <td><strong><c:out value="${affectation.modeleHoraire.heureDebut}"/>–<c:out value="${affectation.modeleHoraire.heureFin}"/></strong><small class="planning-muted">Pause : <c:out value="${affectation.modeleHoraire.pausePrevueMinutes}"/> min</small></td>
                                    <td><span class="planning-days"><c:if test="${affectation.joursSemaine.contains(1)}">L</c:if><c:if test="${affectation.joursSemaine.contains(2)}">M</c:if><c:if test="${affectation.joursSemaine.contains(3)}">M</c:if><c:if test="${affectation.joursSemaine.contains(4)}">J</c:if><c:if test="${affectation.joursSemaine.contains(5)}">V</c:if><c:if test="${affectation.joursSemaine.contains(6)}">S</c:if><c:if test="${affectation.joursSemaine.contains(7)}">D</c:if></span></td>
                                    <td><c:out value="${affectation.dateDebut}"/> <c:if test="${not empty affectation.dateFin}">→ <c:out value="${affectation.dateFin}"/></c:if><c:if test="${empty affectation.dateFin}">→ indéterminée</c:if></td>
                                    <td><span class="puce ${affectation.actif ? 'puce-valide' : 'puce-brouillon'}"><c:out value="${affectation.actif ? 'ACTIVE' : 'INACTIVE'}"/></span></td>
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
<script src="${ctx}/assets/js/table-filtres.js"></script>
</body>
</html>
