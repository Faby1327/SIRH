<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Détail de la demande</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bloc7.css?v=20261107-ui3">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp">
        <jsp:param name="role" value="${role == 'EMPLOYE' ? 'employe' : (role == 'MANAGER' ? 'manager' : 'rh')}"/>
        <jsp:param name="active" value="${role == 'EMPLOYE' ? 'demandes' : 'conges'}"/>
    </jsp:include>
    <div class="contenu">
        <main class="page-espace c-absences c-detail c-detail-demande">
            <header class="entete">
                <a class="retour" href="${pageContext.request.contextPath}/${role == 'EMPLOYE' ? 'mes-demandes' : 'gestion-demandes'}">← Retour aux demandes</a>
                <div><p class="surtitre">Espace de travail</p><h1><c:choose><c:when test="${not empty conge}">Détail du congé</c:when><c:otherwise>Détail de l’absence</c:otherwise></c:choose></h1></div>
            </header>
            <c:if test="${not empty message}"><p class="bandeau"><c:out value="${message}"/></p></c:if>
            <c:if test="${not empty error}"><p class="bandeau bandeau-erreur"><c:out value="${error}"/></p></c:if>
            <c:choose>
                <c:when test="${not empty conge}">
                    <section class="carte">
                        <div class="titre-ligne"><div><p class="reference">Demande de congé</p><h2><c:out value="${conge.employe.personne.nomComplet}"/></h2></div><span class="puce puce-attente"><c:out value="${conge.statut}"/></span></div>
                        <div class="bloc"><h3>Informations de la demande</h3><dl class="infos">
                            <div class="info"><dt>Période</dt><dd>${conge.dateDebut} – ${conge.dateFin}</dd></div>
                            <div class="info"><dt>Jours ouvrés</dt><dd>${conge.jours}</dd></div><div class="info"><dt>Solde disponible</dt><dd>${soldeEmploye} jour(s)</dd></div>
                            <div class="info info-large"><dt>Motif</dt><dd class="texte"><c:out value="${conge.motif}" default="Aucun motif renseigné"/></dd></div>
                            <div class="info"><dt>Commentaire manager</dt><dd class="texte"><c:out value="${conge.commentaireManager}" default="Aucun commentaire"/></dd></div>
                            <div class="info"><dt>Commentaire RH</dt><dd class="texte"><c:out value="${conge.commentaireRh}" default="Aucun commentaire"/></dd></div>
                            <div class="info"><dt>Remplaçant enregistré</dt><dd><c:out value="${conge.remplacant.personne.nomComplet}" default="Aucun"/></dd></div>
                        </dl></div>
                        <c:if test="${role == 'MANAGER' && conge.statut == 'EN_ATTENTE_MANAGER'}">
                            <div class="bloc bloc-remplacement"><div><h3>Choisir un remplaçant</h3><p class="description">Sélectionnez une personne disponible pendant toute la période du congé.</p></div>
                                <form class="form-remplacement" action="${pageContext.request.contextPath}/detailsDemande/conge/${conge.id}/remplacement" method="post">
                                    <label for="remplacantId">Remplaçant</label>
                                    <select id="remplacantId" name="remplacantId" ${empty remplacantsDisponibles ? 'disabled' : 'required'}><c:choose><c:when test="${empty remplacantsDisponibles}"><option value="" selected>Aucun remplaçant disponible</option></c:when><c:otherwise><option value="" selected disabled>Sélectionner un remplaçant</option><c:forEach var="e" items="${remplacantsDisponibles}"><option value="${e.id}" ${conge.remplacant.id == e.id ? 'selected' : ''}><c:out value="${e.personne.nomComplet}"/></option></c:forEach></c:otherwise></c:choose></select>
                                    <c:if test="${empty remplacantsDisponibles}"><p class="aucun-remplacant">Aucun remplaçant disponible sur cette période : les employés du même poste sont en congé ou en absence.</p></c:if>
                                    <button class="bouton-principal" type="submit" ${empty remplacantsDisponibles ? 'disabled' : ''}>Enregistrer le choix</button>
                                </form>
                            </div>
                        </c:if>
                        <c:if test="${role == 'MANAGER' && conge.statut == 'EN_ATTENTE_MANAGER'}">
                            <section class="carte"><div class="bloc"><h3>Décision du manager</h3><form class="formulaire-demande" action="${pageContext.request.contextPath}/gestion-demandes/conge/${conge.id}/decision" method="post"><label>Commentaire manager<textarea name="commentaire" rows="3" placeholder="Commentaire (facultatif)"></textarea></label><div class="actions"><button class="bouton-principal" type="submit" name="accepter" value="true">Valider (1re étape)</button><button class="bouton-principal" type="submit" name="accepter" value="false">Refuser</button></div></form></div></section>
                        </c:if>
                        <c:if test="${role == 'RH' && conge.statut == 'EN_ATTENTE_RH'}">
                            <section class="carte"><div class="bloc"><h3>Décision RH</h3><form class="formulaire-demande" action="${pageContext.request.contextPath}/gestion-demandes/conge/${conge.id}/decision" method="post"><label>Commentaire RH<textarea name="commentaire" rows="3" placeholder="Commentaire (facultatif)"></textarea></label><div class="actions"><button class="bouton-principal" type="submit" name="accepter" value="true">Valider définitivement</button><button class="bouton-principal" type="submit" name="accepter" value="false">Refuser</button></div></form></div></section>
                        </c:if>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="carte">
                        <div class="titre-ligne"><div><p class="reference">Déclaration d’absence</p><h2><c:out value="${absence.employe.personne.nomComplet}"/></h2></div><span class="puce puce-attente"><c:out value="${absence.statut}"/></span></div>
                        <div class="bloc"><h3>Informations de la demande</h3><dl class="infos">
                            <div class="info"><dt>Période</dt><dd>${absence.dateDebut} – ${absence.dateFin}</dd></div><div class="info"><dt>Remplaçant enregistré</dt><dd><c:out value="${absence.remplacant.personne.nomComplet}" default="Aucun"/></dd></div><div class="info"><dt>Durée absence</dt><dd>${absence.jours} jour(s) calendaires</dd></div><div class="info"><dt>Solde congés disponible</dt><dd>${soldeEmploye} jour(s)</dd></div>
                            <div class="info info-large"><dt>Motif</dt><dd class="texte"><c:out value="${absence.motif}" default="Aucun motif renseigné"/></dd></div>
                            <div class="info"><dt>Justificatif</dt><dd><c:choose><c:when test="${not empty absence.justificatifUrl}"><a class="bouton bouton-secondaire" href="${pageContext.request.contextPath}${absence.justificatifUrl}">Consulter le document</a></c:when><c:otherwise>Aucun document joint</c:otherwise></c:choose></dd></div>
                            <div class="info"><dt>Commentaire manager</dt><dd class="texte"><c:out value="${absence.commentaireManager}" default="Aucun commentaire"/></dd></div>
                            <div class="info"><dt>Commentaire RH</dt><dd class="texte"><c:out value="${absence.commentaireRh}" default="Aucun commentaire"/></dd></div>
                        </dl></div>
                    </section>
                                        <c:if test="${role == 'MANAGER' && (absence.statut == 'A_TRANSMETTRE_RH' || absence.statut == 'TRANSMISE_RH')}" >
                        <section class="carte"><div class="bloc bloc-remplacement"><div><h3>Choisir un remplaçant</h3><p class="description">Employé du même poste disponible pendant la période de l’absence.</p></div><form class="form-remplacement" action="${pageContext.request.contextPath}/detailsDemande/absence/${absence.id}/remplacement" method="post"><label for="remplacantAbsenceId">Remplaçant</label><select id="remplacantAbsenceId" name="remplacantId" ${empty remplacantsDisponibles ? 'disabled' : 'required'}><c:choose><c:when test="${empty remplacantsDisponibles}"><option value="" selected>Aucun remplaçant disponible</option></c:when><c:otherwise><option value="" selected disabled>Sélectionner un remplaçant</option><c:forEach var="e" items="${remplacantsDisponibles}"><option value="${e.id}" ${absence.remplacant.id == e.id ? 'selected' : ''}><c:out value="${e.personne.nomComplet}"/></option></c:forEach></c:otherwise></c:choose></select><c:if test="${empty remplacantsDisponibles}"><p class="aucun-remplacant">Aucun collègue disponible sur cette période.</p></c:if><button class="bouton-principal" type="submit" ${empty remplacantsDisponibles ? 'disabled' : ''}>Enregistrer le choix</button></form></div></section>
                    </c:if><c:if test="${role == 'MANAGER' && absence.statut == 'A_TRANSMETTRE_RH'}">
                        <section class="carte"><div class="bloc"><h3>Transmettre l’absence à RH</h3><form class="formulaire-demande" action="${pageContext.request.contextPath}/gestion-demandes/absence/${absence.id}/transmettre" method="post"><label>Commentaire manager<textarea name="commentaire" rows="3" placeholder="Commentaire (facultatif)"></textarea></label><button class="bouton-principal" type="submit">Transmettre à RH</button></form></div></section>
                    </c:if>
                    <c:if test="${role == 'RH' && absence.statut == 'TRANSMISE_RH'}">
                        <section class="carte"><div class="bloc"><h3>Vérifier l’absence</h3><form class="formulaire-demande" action="${pageContext.request.contextPath}/gestion-demandes/absence/${absence.id}/verifier" method="post"><label>Commentaire RH<textarea name="commentaire" rows="3" placeholder="Commentaire (facultatif)"></textarea></label><div class="actions"><button class="bouton-principal" type="submit" name="justifiee" value="true">Justifiée</button><button class="bouton-principal" type="submit" name="justifiee" value="false">Non justifiée</button></div></form></div></section>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</div>
</body>
</html>
