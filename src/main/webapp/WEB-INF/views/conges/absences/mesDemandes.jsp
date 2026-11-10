<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mes demandes · SIRH</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bloc7.css?v=20261107-ui3">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="employe"/><jsp:param name="active" value="demandes"/></jsp:include>
    <div class="contenu"><main class="page-espace c-absences">
        <header class="entete"><div><p class="surtitre">Espace employé</p><h1>Mes demandes</h1><p class="description">Solde disponible : <strong>${solde} jour(s)</strong></p></div></header>
        <c:if test="${not empty message}"><p class="bandeau"><c:out value="${message}"/></p></c:if>
        <c:if test="${not empty error}"><p class="bandeau bandeau-erreur"><c:out value="${error}"/></p></c:if>
        <section class="carte">
            <input class="tab-radio" type="radio" name="onglet" id="tab-conge" checked><label for="tab-conge">Congés</label>
            <input class="tab-radio" type="radio" name="onglet" id="tab-absence"><label for="tab-absence">Absences</label>
            <div class="panneau contenu-conge">
                <h2>Demander un congé</h2>
                <form class="formulaire-demande" action="${pageContext.request.contextPath}/mes-demandes/conge" method="post">
                    <label>Du<input type="date" name="dateDebut" min="${today}" required></label><label>Au<input type="date" name="dateFin" min="${today}" required></label>
                    <label class="large">Motif<textarea name="motif" rows="3"></textarea></label><button class="bouton-principal">Envoyer la demande</button>
                </form>
                <h2>Mes congés</h2><div class="defilement"><table><thead><tr><th>Période</th><th>Jours ouvrés</th><th>Statut</th><th>Détail</th></tr></thead><tbody>
                    <c:forEach var="d" items="${demandesConge}"><tr><td>${d.dateDebut} – ${d.dateFin}</td><td>${d.jours}</td><td><c:out value="${d.statut}"/></td><td><a href="${pageContext.request.contextPath}/detailsDemande?type=conge&id=${d.id}">Voir</a></td></tr></c:forEach>
                    <c:if test="${empty demandesConge}"><tr><td colspan="4" class="vide">Aucune demande de congé.</td></tr></c:if>
                </tbody></table></div>
            </div>
            <div class="panneau contenu-absence">
                <h2>Déclarer une absence</h2>
                <form class="formulaire-demande" action="${pageContext.request.contextPath}/mes-demandes/absence" method="post" enctype="multipart/form-data">
                    <label>Du<input type="date" name="dateDebut" min="${today}" required></label><label>Au<input type="date" name="dateFin" min="${today}" required></label>
                    <label class="large">Motif<textarea name="motif" required rows="3"></textarea></label>
                    <label class="large">Justificatif<input type="file" name="justificatif" accept="application/pdf,image/png,image/jpeg"></label><button class="bouton-principal">Déclarer l’absence</button>
                </form>
                <h2>Mes absences</h2><div class="defilement"><table><thead><tr><th>Période</th><th>Jours calendaires</th><th>Statut</th><th>Détail</th></tr></thead><tbody>
                    <c:forEach var="d" items="${demandesAbsence}"><tr><td>${d.dateDebut} – ${d.dateFin}</td><td>${d.jours}</td><td><c:out value="${d.statut}"/></td><td><a href="${pageContext.request.contextPath}/detailsDemande?type=absence&id=${d.id}">Voir</a></td></tr></c:forEach>
                    <c:if test="${empty demandesAbsence}"><tr><td colspan="4" class="vide">Aucune absence enregistrée.</td></tr></c:if>
                </tbody></table></div>
            </div>
        </section>
    </main></div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/validation-periode.js?v=20261107-ui3"></script></body>
</html>
