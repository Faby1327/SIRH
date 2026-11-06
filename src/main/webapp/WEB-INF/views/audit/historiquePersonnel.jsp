<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:useBean id="libelles" class="java.util.HashMap"/>
<c:set target="${libelles}" property="CELIBATAIRE" value="Célibataire"/>
<c:set target="${libelles}" property="MARIE" value="Marié(e)"/>
<c:set target="${libelles}" property="UNION_LIBRE" value="Union libre"/>
<c:set target="${libelles}" property="DIVORCE" value="Divorcé(e)"/>
<c:set target="${libelles}" property="VEUF" value="Veuf / veuve"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Situation personnelle</title>
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
                    <p class="surtitre">Situation personnelle</p>
                    <h1><c:out value="${employe.personne.nomComplet}"/></h1>
                    <p><strong><c:out value="${employe.matricule}"/></strong> — téléphone, email personnel, adresse, situation matrimoniale et contact d'urgence au fil du temps.</p>
                </div>
            </header>

            <c:if test="${not empty message}"><div class="dossier-alert dossier-alert-ok"><c:out value="${message}"/></div></c:if>
            <c:if test="${not empty error}"><div class="dossier-alert dossier-alert-error"><c:out value="${error}"/></div></c:if>

            <section class="carte dossier-card">
                <div class="dossier-card-heading"><div><span class="dossier-icon">01</span><div><h2>Situation actuelle</h2><p>Dernier changement enregistré dans l'historique</p></div></div></div>
                <c:choose>
                    <c:when test="${empty courante}">
                        <p class="dossier-empty">Aucune situation personnelle enregistrée pour cet employé. Utilisez le formulaire ci-dessous.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="dossier-info-grid">
                            <div><span>Téléphone</span><strong><c:out value="${empty courante.telephone ? '—' : courante.telephone}"/></strong></div>
                            <div><span>Email personnel</span><strong><c:out value="${empty courante.emailPersonnel ? '—' : courante.emailPersonnel}"/></strong></div>
                            <div><span>Adresse</span><strong><c:out value="${empty courante.adresse ? '—' : courante.adresse}"/></strong></div>
                            <div><span>Commune</span><strong><c:out value="${empty courante.commune ? '—' : courante.commune}"/></strong></div>
                            <div><span>Région</span><strong><c:out value="${empty courante.region ? '—' : courante.region}"/></strong></div>
                            <div><span>Situation matrimoniale</span><strong><c:out value="${empty courante.situationMatrimoniale ? '—' : libelles[courante.situationMatrimoniale]}"/></strong></div>
                            <div><span>Nombre d'enfants</span><strong><c:out value="${empty courante.nombreEnfants ? '—' : courante.nombreEnfants}"/></strong></div>
                            <div><span>Contact d'urgence</span><strong><c:out value="${empty courante.contactUrgenceNom ? '—' : courante.contactUrgenceNom}"/><c:if test="${not empty courante.contactUrgenceRelation}"> (<c:out value="${courante.contactUrgenceRelation}"/>)</c:if></strong></div>
                            <div><span>Téléphone du contact</span><strong><c:out value="${empty courante.contactUrgenceTelephone ? '—' : courante.contactUrgenceTelephone}"/></strong></div>
                            <div><span>En vigueur depuis le</span><strong><c:out value="${courante.dateEffet}"/></strong></div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="carte dossier-card dossier-contract-form">
                <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-green">02</span><div><h2>Enregistrer un changement</h2><p>Les champs sont préremplis avec la situation actuelle. Une nouvelle ligne est ajoutée : l'historique n'est jamais modifié.</p></div></div></div>
                <form method="post" action="${ctx}/rh/employes/${employe.id}/situation" class="dossier-form-grid">
                    <label>Téléphone
                        <input class="champ" name="telephone" type="text" maxlength="30" value="${fn:escapeXml(courante.telephone)}">
                    </label>
                    <label>Email personnel
                        <input class="champ" name="emailPersonnel" type="text" maxlength="150" value="${fn:escapeXml(courante.emailPersonnel)}">
                    </label>
                    <label>Adresse
                        <input class="champ" name="adresse" type="text" maxlength="255" value="${fn:escapeXml(courante.adresse)}">
                    </label>
                    <label>Commune
                        <input class="champ" name="commune" type="text" maxlength="100" value="${fn:escapeXml(courante.commune)}">
                    </label>
                    <label>Région
                        <input class="champ" name="region" type="text" maxlength="100" value="${fn:escapeXml(courante.region)}">
                    </label>
                    <label>Situation matrimoniale
                        <select name="situationMatrimoniale" class="champ">
                            <option value="">Non renseignée</option>
                            <c:forEach var="s" items="${situations}">
                                <option value="${s}" ${courante.situationMatrimoniale == s ? 'selected' : ''}><c:out value="${libelles[s]}"/></option>
                            </c:forEach>
                        </select>
                    </label>
                    <label>Nombre d'enfants
                        <input class="champ" name="nombreEnfants" type="number" min="0" max="50" value="${courante.nombreEnfants}">
                    </label>
                    <label>Contact d'urgence : nom
                        <input class="champ" name="contactUrgenceNom" type="text" maxlength="150" value="${fn:escapeXml(courante.contactUrgenceNom)}">
                    </label>
                    <label>Contact d'urgence : relation
                        <input class="champ" name="contactUrgenceRelation" type="text" maxlength="50" placeholder="Ex. Épouse, frère…" value="${fn:escapeXml(courante.contactUrgenceRelation)}">
                    </label>
                    <label>Contact d'urgence : téléphone
                        <input class="champ" name="contactUrgenceTelephone" type="text" maxlength="30" value="${fn:escapeXml(courante.contactUrgenceTelephone)}">
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
                        <tr><th>Date d'effet</th><th>Téléphone</th><th>Email personnel</th><th>Adresse</th><th>Situation</th><th>Enfants</th><th>Contact d'urgence</th><th>Enregistré le</th></tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty lignes}">
                                <tr><td colspan="8" class="dossier-empty">Aucun changement enregistré.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="l" items="${lignes}">
                                    <tr>
                                        <td><strong><c:out value="${l.dateEffet}"/></strong></td>
                                        <td><c:out value="${empty l.telephone ? '—' : l.telephone}"/></td>
                                        <td><c:out value="${empty l.emailPersonnel ? '—' : l.emailPersonnel}"/></td>
                                        <td>
                                            <c:out value="${empty l.adresse ? '—' : l.adresse}"/>
                                            <c:if test="${not empty l.commune or not empty l.region}"><br><small><c:out value="${l.commune}"/><c:if test="${not empty l.commune and not empty l.region}">, </c:if><c:out value="${l.region}"/></small></c:if>
                                        </td>
                                        <td><c:out value="${empty l.situationMatrimoniale ? '—' : libelles[l.situationMatrimoniale]}"/></td>
                                        <td><c:out value="${empty l.nombreEnfants ? '—' : l.nombreEnfants}"/></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${empty l.contactUrgenceNom and empty l.contactUrgenceTelephone}">—</c:when>
                                                <c:otherwise>
                                                    <c:out value="${l.contactUrgenceNom}"/><c:if test="${not empty l.contactUrgenceRelation}"> (<c:out value="${l.contactUrgenceRelation}"/>)</c:if>
                                                    <br><small><c:out value="${l.contactUrgenceTelephone}"/></small>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
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