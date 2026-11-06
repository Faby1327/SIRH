<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détail d'une action</title>
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
            <div class="dossier-retour"><a href="${ctx}/rh/audit">← Retour au journal d'audit</a></div>
            <header class="entete">
                <div>
                    <p class="surtitre">Journal d'audit</p>
                    <h1>Action n° <c:out value="${evenement.id}"/></h1>
                    <p>Détail de l'action enregistrée, avec les anciennes et les nouvelles valeurs.</p>
                </div>
            </header>

            <section class="carte dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon">01</span><div><h2>Informations</h2><p>Qui a fait quoi, et quand</p></div></div></div>
                <div class="dossier-info-grid">
                    <div><span>Date et heure</span><strong><c:out value="${fn:replace(fn:substring(evenement.dateAction, 0, 19), 'T', ' ')}"/></strong></div>
                    <div><span>Utilisateur</span><strong><c:out value="${evenement.utilisateur.nomComplet}"/></strong></div>
                    <div><span>Email</span><strong><c:out value="${evenement.utilisateur.email}"/></strong></div>
                    <div><span>Action</span><strong><c:out value="${evenement.action.libelle}"/> (<c:out value="${evenement.action.code}"/>)</strong></div>
                    <div><span>Type d'entité</span><strong><c:out value="${evenement.typeEntite.libelle}"/></strong></div>
                    <div><span>N° de l'entité</span><strong><c:out value="${evenement.entiteId}"/></strong></div>
                </div>
            </section>

            <section class="carte c-table dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-blue">02</span><div><h2>Anciennes et nouvelles valeurs</h2><p>Les lignes surlignées sont celles qui ont changé</p></div></div></div>
                <div class="defilement">
                    <table class="table-offres">
                        <thead>
                        <tr><th>Champ</th><th>Ancienne valeur</th><th>Nouvelle valeur</th><th>Changement</th></tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty lignes}">
                                <tr><td colspan="4" class="dossier-empty">Cette action n'enregistre aucune valeur (par exemple une consultation).</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="l" items="${lignes}">
                                    <tr style="${l.modifie ? 'background: var(--chip-amber-bg)' : ''}">
                                        <td><strong><c:out value="${l.champ}"/></strong></td>
                                        <td><c:out value="${empty l.ancienne ? '—' : l.ancienne}"/></td>
                                        <td><c:out value="${empty l.nouvelle ? '—' : l.nouvelle}"/></td>
                                        <td><span class="puce ${l.modifie ? 'puce-attente' : 'puce-brouillon'}"><c:out value="${l.modifie ? 'Modifié' : 'Identique'}"/></span></td>
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