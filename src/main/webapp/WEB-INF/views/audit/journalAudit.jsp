<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Journal d'audit</title>
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
        <jsp:param name="active" value="audit"/>
    </jsp:include>
    <div class="contenu">
        <main class="page-espace c-page-liste c-annuaire-rh">
            <header class="entete">
                <div>
                    <p class="surtitre">Ressources humaines</p>
                    <h1>Journal d'audit</h1>
                    <p>Actions importantes effectuées dans l'application : création, modification, suppression et consultation.</p>
                </div>
            </header>

            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>

            <section class="carte c-table">
                <div class="entete">
                    <h2>Événements</h2>
                    <p><c:out value="${evenements.size()}"/> événement(s) affiché(s)<c:if test="${evenements.size() >= limite}"> — limité aux <c:out value="${limite}"/> plus récents, affinez la recherche</c:if></p>
                </div>

                <form method="get" action="${ctx}/rh/audit">
                    <div class="filtres">
                        <div class="filtre" style="flex:1 1 160px">
                            <label for="f-action">Action</label>
                            <select id="f-action" name="action" class="champ">
                                <option value="">Toutes les actions</option>
                                <c:forEach var="t" items="${typesAction}">
                                    <option value="${t.code}" ${fAction == t.code ? 'selected' : ''}><c:out value="${t.libelle}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filtre" style="flex:1 1 160px">
                            <label for="f-entite">Type d'entité</label>
                            <select id="f-entite" name="entite" class="champ">
                                <option value="">Toutes les entités</option>
                                <c:forEach var="t" items="${typesEntite}">
                                    <option value="${t.code}" ${fEntite == t.code ? 'selected' : ''}><c:out value="${t.libelle}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filtre" style="flex:0 1 130px">
                            <label for="f-entite-id">N° d'entité</label>
                            <input id="f-entite-id" name="entiteId" type="number" min="1" class="champ" value="${fEntiteId}">
                        </div>
                        <div class="filtre" style="flex:1 1 200px">
                            <label for="f-utilisateur">Utilisateur</label>
                            <input id="f-utilisateur" name="utilisateur" type="text" class="champ" placeholder="Nom ou email" value="${fn:escapeXml(fUtilisateur)}">
                        </div>
                        <div class="filtre" style="flex:1 1 150px">
                            <label for="f-du">Du</label>
                            <input id="f-du" name="du" type="date" class="champ" value="${fDu}">
                        </div>
                        <div class="filtre" style="flex:1 1 150px">
                            <label for="f-au">Au</label>
                            <input id="f-au" name="au" type="date" class="champ" value="${fAu}">
                        </div>
                        <div class="filtre" style="flex:2 1 220px">
                            <label for="f-texte">Texte dans les valeurs</label>
                            <input id="f-texte" name="texte" type="text" class="champ" placeholder="Ex. SUSPENDU" value="${fn:escapeXml(fTexte)}">
                        </div>
                        <button class="bouton bouton-principal" type="submit">Rechercher</button>
                        <a class="bouton" href="${ctx}/rh/audit">Réinitialiser</a>
                    </div>
                </form>

                <div class="defilement">
                    <table class="table-offres">
                        <thead>
                        <tr><th>Date et heure</th><th>Utilisateur</th><th>Action</th><th>Entité</th><th>Détail</th></tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty evenements}">
                                <tr><td colspan="5">Aucun événement ne correspond à la recherche.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="e" items="${evenements}">
                                    <tr>
                                        <td><c:out value="${fn:replace(fn:substring(e.dateAction, 0, 19), 'T', ' ')}"/></td>
                                        <td><c:out value="${e.utilisateur.nomComplet}"/></td>
                                        <td><span class="puce ${e.action.code == 'INSERT' ? 'puce-valide' : (e.action.code == 'UPDATE' ? 'puce-attente' : (e.action.code == 'DELETE' ? 'puce-refuse' : 'puce-info'))}"><c:out value="${e.action.libelle}"/></span></td>
                                        <td><c:out value="${e.typeEntite.libelle}"/> n° <c:out value="${e.entiteId}"/></td>
                                        <td><a class="bouton" href="${ctx}/rh/audit/${e.id}">Voir le détail</a></td>
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
</html>