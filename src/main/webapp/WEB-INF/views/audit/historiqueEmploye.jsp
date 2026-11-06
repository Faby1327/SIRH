<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Historique professionnel</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
    <link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261103-fields">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp">
        <jsp:param name="role" value="rh"/>
        <jsp:param name="active" value="employes"/>
    </jsp:include>
    <div class="contenu">
        <main class="page-espace c-page-liste c-annuaire-rh c-dossier-employe">
            <div class="dossier-retour"><a href="${ctx}/rh/employes/${employe.id}">← Retour au dossier de l'employé</a></div>
            <header class="entete">
                <div>
                    <p class="surtitre">Historique professionnel</p>
                    <h1><c:out value="${employe.personne.nomComplet}"/></h1>
                    <p><strong><c:out value="${employe.matricule}"/></strong> — poste, département, supérieur hiérarchique et salaire au fil du temps.</p>
                </div>
            </header>

            <c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>

            <section class="carte dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon">01</span><div><h2>Situation actuelle</h2><p>Dernier changement enregistré dans l'historique</p></div></div></div>
                <c:choose>
                    <c:when test="${empty courant}">
                        <p class="dossier-empty">Aucun changement enregistré pour cet employé. La première ligne est créée avec son premier contrat, ou avec le formulaire ci-dessous.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="dossier-info-grid">
                            <div><span>Poste</span><strong><c:out value="${courant.poste.intitule}"/></strong></div>
                            <div><span>Département</span><strong><c:out value="${courant.departement.nom}"/></strong></div>
                            <div><span>Supérieur hiérarchique</span><strong><c:out value="${empty courant.superieur ? 'Aucun' : courant.superieur.personne.nomComplet}"/></strong></div>
                            <div><span>Salaire mensuel</span><strong><c:out value="${courant.salaire}"/> Ar</strong></div>
                            <div><span>En vigueur depuis le</span><strong><c:out value="${courant.dateEffet}"/></strong></div>
                            <div><span>Enregistré le</span><strong><c:out value="${fn:replace(fn:substring(courant.dateEnregistrement, 0, 19), 'T', ' ')}"/></strong></div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="carte dossier-card dossier-contract-form">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-green">02</span><div><h2>Enregistrer un changement</h2><p>Une nouvelle ligne est ajoutée : l'historique existant n'est jamais modifié.</p></div></div></div>
                <form method="post" action="${ctx}/rh/employes/${employe.id}/historique" class="dossier-form-grid">
                    <label>Poste
                        <select name="posteId" class="champ" required>
                            <c:forEach var="p" items="${postes}">
                                <option value="${p.idPoste}" ${courant.poste.idPoste == p.idPoste ? 'selected' : ''}><c:out value="${p.intitule}"/></option>
                            </c:forEach>
                        </select>
                    </label>
                    <label>Salaire mensuel (Ar)
                        <input class="champ" name="salaire" type="number" min="0" step="0.01" required value="${courant.salaire}">
                    </label>
                    <label>Supérieur hiérarchique
                        <select name="superieurId" class="champ">
                            <option value="">Aucun supérieur</option>
                            <c:forEach var="s" items="${superieurs}">
                                <option value="${s.id}" ${courant.superieur.id == s.id ? 'selected' : ''}><c:out value="${s.matricule}"/> — <c:out value="${s.personne.nomComplet}"/></option>
                            </c:forEach>
                        </select>
                    </label>
                    <label>Date d'effet
                        <input class="champ" id="dateEffet" name="dateEffet" type="date" required>
                    </label>
                    <div class="dossier-form-submit"><button class="bouton bouton-principal" type="submit">Enregistrer le changement</button></div>
                </form>
            </section>

            <section class="carte c-table dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-blue">03</span><div><h2>Historique des changements</h2><p><c:out value="${lignes.size()}"/> ligne(s), du plus récent au plus ancien</p></div></div></div>
                <div class="defilement">
                    <table class="table-offres">
                        <thead>
                        <tr><th>Date d'effet</th><th>Poste</th><th>Département</th><th>Supérieur</th><th>Salaire</th><th>Enregistré le</th></tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty lignes}">
                                <tr><td colspan="6" class="dossier-empty">Aucun changement enregistré.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="l" items="${lignes}">
                                    <tr>
                                        <td><strong><c:out value="${l.dateEffet}"/></strong></td>
                                        <td><c:out value="${l.poste.intitule}"/></td>
                                        <td><c:out value="${l.departement.nom}"/></td>
                                        <td><c:out value="${empty l.superieur ? '—' : l.superieur.personne.nomComplet}"/></td>
                                        <td><c:out value="${l.salaire}"/> Ar</td>
                                        <td><c:out value="${fn:replace(fn:substring(l.dateEnregistrement, 0, 19), 'T', ' ')}"/></td>
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
<script>
    // La date d'effet est préremplie avec la date du jour.
    document.getElementById('dateEffet').valueAsDate = new Date();
</script>
</body>
</html>