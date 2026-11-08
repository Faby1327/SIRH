<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Documents de l'employé</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css"><link rel="stylesheet" href="${ctx}/assets/css/base.css"><link rel="stylesheet" href="${ctx}/assets/css/besoins.css"><link rel="stylesheet" href="${ctx}/assets/css/composants.css"><link rel="stylesheet" href="${ctx}/assets/css/rh.css?v=20261108-dossier">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="rh"/><jsp:param name="active" value="employes"/></jsp:include>
    <div class="contenu">
        <main class="page-espace c-page-liste c-annuaire-rh c-dossier-employe">
            <div class="dossier-retour"><a href="${ctx}/rh/employes/${employe.id}">← Retour au dossier</a></div>
            <header class="entete"><div><p class="surtitre">Dossier employé · <c:out value="${employe.matricule}"/></p><h1>Documents de <c:out value="${employe.personne.nomComplet}"/></h1><p>CV, CIN, diplômes, contrats et autres pièces. 10 Mo maximum par fichier (PDF, JPG, PNG, DOC, DOCX).</p></div></header>
            <c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error" role="alert"><c:out value="${error}"/></div></c:if>

            <section class="carte dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-green">+</span><div><h2>Ajouter un document</h2><p>Le fichier est rattaché au dossier de cet employé.</p></div></div></div>
                <form method="post" action="${ctx}/rh/employes/${employe.id}/documents" enctype="multipart/form-data" class="dossier-form-grid">
                    <label>Type de document *
                        <select class="champ" name="typeDocumentId" required>
                            <c:forEach var="t" items="${typesDocument}"><option value="${t.id}"><c:out value="${t.libelle}"/></option></c:forEach>
                        </select></label>
                    <label>Fichier *<input class="champ" type="file" name="fichier" required accept=".pdf,.jpg,.jpeg,.png,.doc,.docx"></label>
                    <label>Date d'expiration <small>(facultative)</small><input class="champ" type="date" name="dateExpiration"></label>
                    <div class="dossier-form-submit"><button class="bouton bouton-principal" type="submit">Ajouter</button></div>
                </form>
            </section>

            <section class="carte c-table dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-blue">≡</span><div><h2>Pièces du dossier</h2><p><c:out value="${documents.size()}"/> document(s)</p></div></div></div>
                <div class="defilement">
                    <table class="table-offres">
                        <thead><tr><th>Type</th><th>Fichier</th><th>Ajouté le</th><th>Par</th><th>Expiration</th><th>Actions</th></tr></thead>
                        <tbody>
                        <c:forEach var="doc" items="${documents}">
                            <tr>
                                <td><strong><c:out value="${doc.typeDocument.libelle}"/></strong></td>
                                <td><c:out value="${doc.nomFichier}"/></td>
                                <td><c:out value="${fn:replace(fn:substring(doc.dateAjout, 0, 16), 'T', ' ')}"/></td>
                                <td><c:out value="${empty doc.utilisateur ? '-' : doc.utilisateur.nom}"/></td>
                                <td><c:choose><c:when test="${empty doc.dateExpiration}">-</c:when><c:otherwise><span class="${doc.expire ? 'doc-expire' : ''}"><c:out value="${doc.dateExpiration}"/><c:if test="${doc.expire}"> (expiré)</c:if></span></c:otherwise></c:choose></td>
                                <td class="dossier-actions-ligne">
                                    <a class="bouton" href="${ctx}/employes/${employe.id}/documents/${doc.id}">Télécharger</a>
                                    <form method="post" action="${ctx}/rh/employes/${employe.id}/documents/${doc.id}/supprimer" onsubmit="return confirm('Supprimer ce document ?');"><button class="bouton bouton-danger" type="submit">Supprimer</button></form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty documents}"><tr><td colspan="6" class="dossier-empty">Aucun document dans ce dossier.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</div>
</body>
</html>
