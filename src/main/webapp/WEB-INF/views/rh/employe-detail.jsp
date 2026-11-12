<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dossier employé</title>
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
            <div class="dossier-retour"><a href="${ctx}/rh/employes">← Retour à la liste des employés</a></div>
            <header class="entete dossier-hero">
                <div class="dossier-identite">
                    <div class="dossier-avatar"><c:out value="${fn:substring(employe.personne.prenom, 0, 1)}"/><c:out value="${fn:substring(employe.personne.nom, 0, 1)}"/></div>
                    <div>
                        <p class="surtitre">Dossier employé</p>
                        <h1><c:out value="${employe.personne.nomComplet}"/></h1>
                        <p class="dossier-reference"><strong><c:out value="${employe.matricule}"/></strong><span>•</span><c:out value="${employe.personne.email}"/></p>
                    </div>
                </div>
                <div class="dossier-hero-actions"><span class="dossier-statut"><c:out value="${employe.statut}"/></span><a class="bouton" href="${ctx}/rh/employes/${employe.id}/documents">Documents</a><a class="bouton bouton-principal" href="${ctx}/rh/employes/${employe.id}/modifier">Modifier le dossier</a></div>
            </header>

            <c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>

            <section class="carte dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon">01</span><div><h2>Informations personnelles</h2><p>Coordonnées et statut administratif de l'employé</p></div></div></div>
                <div class="dossier-info-grid">
                    <div><span>Nom complet</span><strong><c:out value="${employe.personne.nomComplet}"/></strong></div>
                    <div><span>Email</span><strong><c:out value="${employe.personne.email}"/></strong></div>
                    <div><span>Téléphone</span><strong><c:out value="${empty employe.personne.telephone ? '-' : employe.personne.telephone}"/></strong></div>
                    <div><span>Ville</span><strong><c:out value="${empty employe.personne.ville ? '-' : employe.personne.ville}"/></strong></div>
                    <div><span>Date d'embauche</span><strong><c:out value="${employe.dateEmbauche}"/></strong></div>
                    <div><span>Matricule</span><strong><c:out value="${employe.matricule}"/></strong></div>
                </div>
                <form method="post" action="${ctx}/rh/employes/${employe.id}/photo" enctype="multipart/form-data" class="dossier-status-form dossier-photo-form"><label>Photo<input class="champ" type="file" name="photo" accept=".jpg,.jpeg,.png" required></label><button class="bouton" type="submit">Mettre à jour la photo</button></form>
                <form method="post" action="${ctx}/rh/employes/${employe.id}/statut" class="dossier-status-form">
                    <label for="statut">Modifier le statut</label>
                    <select id="statut" name="statut" class="champ">
                        <option value="ACTIF" ${employe.statut == 'ACTIF' ? 'selected' : ''}>ACTIF</option>
                        <option value="SUSPENDU" ${employe.statut == 'SUSPENDU' ? 'selected' : ''}>SUSPENDU</option>
                        <option value="SORTI" ${employe.statut == 'SORTI' ? 'selected' : ''}>SORTI</option>
                    </select>
                    <button class="bouton bouton-principal" type="submit">Enregistrer le statut</button>
                </form>
            </section>

            <section class="carte dossier-card dossier-contract-form">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-green">02</span><div><h2>Nouveau contrat</h2><p>Le contrat actuellement en cours sera terminé automatiquement.</p></div></div></div>
                <form method="post" action="${ctx}/rh/employes/${employe.id}/contrats" class="dossier-form-grid">
                    <label>Poste<select name="posteId" class="champ" required><c:forEach var="poste" items="${postes}"><option value="${poste.idPoste}"><c:out value="${poste.intitule}"/></option></c:forEach></select></label>
                    <label>Type de contrat<select name="typeContrat" class="champ" required><c:forEach var="type" items="${typesContrat}"><option value="${type}"><c:out value="${type}"/></option></c:forEach></select></label>
                    <label>Salaire mensuel (Ar)<input class="champ" name="salaireMensuel" type="number" min="0" step="0.01" required placeholder="Ex. 350000"></label>
                    <label>Date de début<input class="champ" name="dateDebut" type="date" required></label>
                    <label>Date de fin <small>(facultative)</small><input class="champ" name="dateFin" type="date"></label>
                    <div class="dossier-form-submit"><button class="bouton bouton-principal" type="submit">Créer le contrat</button></div>
                </form>
            </section>

            <section class="carte c-table dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-blue">03</span><div><h2>Historique des contrats</h2><p><c:out value="${contrats.size()}"/> contrat(s) pour cet employé</p></div></div><a class="dossier-contract-link" href="${ctx}/rh/contrats">Voir tous les contrats →</a></div>
                <div class="defilement">
                    <table class="table-offres">
                        <thead><tr><th>Poste</th><th>Type</th><th>Début</th><th>Fin</th><th>Salaire</th><th>Statut</th><th>Action</th></tr></thead>
                        <tbody>
                        <c:forEach var="contrat" items="${contrats}">
                            <tr><td><strong><c:out value="${contrat.poste.intitule}"/></strong></td><td><c:out value="${contrat.typeContrat}"/></td><td><c:out value="${contrat.dateDebut}"/></td><td><c:out value="${empty contrat.dateFin ? '-' : contrat.dateFin}"/></td><td><c:out value="${contrat.salaireMensuel}"/> Ar</td><td><span class="dossier-contract-status ${contrat.statut == 'EN_COURS' ? 'is-live' : 'is-ended'}"><c:out value="${contrat.statut}"/></span></td><td><c:if test="${contrat.statut == 'EN_COURS'}"><form method="post" action="${ctx}/rh/employes/${employe.id}/contrats/${contrat.id}/terminer"><button class="bouton bouton-danger" type="submit">Terminer</button></form></c:if></td></tr>
                        </c:forEach>
                        <c:if test="${empty contrats}"><tr><td colspan="7" class="dossier-empty">Aucun contrat enregistré.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</div>
</body>
</html>
