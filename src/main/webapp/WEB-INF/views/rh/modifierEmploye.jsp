<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modifier un dossier employé</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css"><link rel="stylesheet" href="${ctx}/assets/css/base.css"><link rel="stylesheet" href="${ctx}/assets/css/besoins.css"><link rel="stylesheet" href="${ctx}/assets/css/composants.css"><link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261108-dossier">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="employes"/></jsp:include>
    <div class="contenu">
        <main class="page-espace c-page-liste c-annuaire-rh c-dossier-employe">
            <div class="dossier-retour"><a href="${ctx}/rh/employes/${employe.id}">← Retour au dossier</a></div>
            <header class="entete"><div><p class="surtitre">Dossier employé · <c:out value="${employe.matricule}"/></p><h1>Modifier <c:out value="${employe.personne.nomComplet}"/></h1></div></header>
            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error" role="alert"><c:out value="${error}"/></div></c:if>
            <form method="post" action="${ctx}/rh/employes/${employe.id}/modifier" enctype="multipart/form-data" class="dossier-formulaire">
                <jsp:include page="/WEB-INF/views/fragments/employe-form-champs.jsp"><jsp:param name="mode" value="modification"/></jsp:include>
                <div class="dossier-actions"><a class="bouton" href="${ctx}/rh/employes/${employe.id}">Annuler</a><button class="bouton bouton-principal" type="submit">Enregistrer les modifications</button></div>
            </form>
        </main>
    </div>
</div>
</body>
</html>
