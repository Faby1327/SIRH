<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Absences - RH</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
    <link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261109-absences-paie">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="absences"/></jsp:include>
    <div class="contenu">
        <main class="page-espace c-page-liste c-absences-page">
            <header class="entete"><div><p class="surtitre">Temps de travail</p><h1>Absences</h1><p>Enregistrez les périodes d'absence qui bloquent automatiquement le planning.</p></div></header>
            <c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>
            <div class="mission-detail-grid absence-grid">
                <section class="carte dossier-card">
                    <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-blue">01</span><div><h2>Enregistrer une absence</h2><p>L'absence est validée directement par le RH.</p></div></div></div>
                    <form class="absence-form" action="${ctx}/rh/absences" method="post">
                        <label>Employé<select class="champ" name="employeId" required><option value="">Sélectionner</option><c:forEach var="employe" items="${employes}"><option value="${employe.id}"><c:out value="${employe.matricule}"/> · <c:out value="${employe.personne.nomComplet}"/></option></c:forEach></select></label>
                        <label>Du<input class="champ" type="date" name="dateDebut" required></label>
                        <label>Au<input class="champ" type="date" name="dateFin" required></label>
                        <label>Type<select class="champ" name="typeAbsence" required><c:forEach var="type" items="${typesAbsence}"><option value="${type}"><c:out value="${type}"/></option></c:forEach></select></label>
                        <label class="absence-motif">Motif<textarea class="champ" name="motif" rows="3"></textarea></label>
                        <label class="absence-impact"><input type="checkbox" name="impactPaie" value="true"> Absence non payée : appliquer une retenue</label>
                        <button class="bouton-principal" type="submit">Enregistrer l'absence</button>
                    </form>
                </section>
                <section class="carte c-table dossier-card">
                    <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-green">02</span><div><h2>Absences enregistrées</h2><p><c:out value="${absences.size()}"/> absence(s)</p></div></div></div>
                    <div class="defilement"><table class="table-offres"><thead><tr><th>Employé</th><th>Période</th><th>Type</th><th>Motif</th><th>Paie</th><th>Statut</th></tr></thead><tbody>
                    <c:choose>
                        <c:when test="${empty absences}"><tr><td colspan="6" class="dossier-empty">Aucune absence enregistrée.</td></tr></c:when>
                        <c:otherwise><c:forEach var="absence" items="${absences}"><tr><td><strong><c:out value="${absence.employe.personne.nomComplet}"/></strong><small class="planning-muted"><c:out value="${absence.employe.matricule}"/></small></td><td><c:out value="${absence.dateDebut}"/> → <c:out value="${absence.dateFin}"/></td><td><c:out value="${absence.typeAbsence}"/></td><td><c:out value="${empty absence.motif ? '—' : absence.motif}"/></td><td><span class="absence-paie-badge ${absence.impactPaie ? 'absence-paie-retenue' : 'absence-paie-payee'}"><c:out value="${absence.impactPaie ? 'Retenue' : 'Payée'}"/></span></td><td><span class="puce puce-valide"><c:out value="${absence.statut}"/></span></td></tr></c:forEach></c:otherwise>
                    </c:choose>
                    </tbody></table></div>
                </section>
            </div>
        </main>
    </div>
</div>
</body>
</html>
