<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon espace candidat</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/candidat.css">
</head>
<body>
<div class="cand-page">
    <aside class="cand-sidebar">
        <div class="cand-brand">
            <span class="cand-brand-mark">RH</span>
            <div><strong>Besoins en personnel</strong><small>Espace candidat</small></div>
        </div>
        <div class="cand-profile">
            <div class="cand-avatar">${fn:substring(candidat.prenom,0,1)}${fn:substring(candidat.nom,0,1)}</div>
            <div><strong><c:out value="${candidat.nomComplet}"/></strong><small>Candidat</small></div>
        </div>
        <nav class="cand-nav" aria-label="Navigation candidat">
            <a class="cand-nav-link cand-nav-active" href="${ctx}/candidat/espace"><span class="cand-nav-icon">⌂</span> Tableau de bord</a>
            <a class="cand-nav-link" href="${ctx}/candidat/proposition"><span class="cand-nav-icon">✉</span> Ma proposition</a>
            <a class="cand-nav-link" href="${ctx}/offres"><span class="cand-nav-icon">▣</span> Voir les offres</a>
        </nav>
        <a class="cand-logout" href="${ctx}/logout">↪ Se déconnecter</a>
    </aside>

    <main class="cand-main">
        <header class="cand-topbar">
            <div><span class="cand-eyebrow">TABLEAU DE BORD</span><h1>Bonjour, <c:out value="${candidat.prenom}"/> !</h1><p>Retrouvez ici les informations de votre candidature et votre proposition.</p></div>
            <a href="${ctx}/offres" class="cand-top-action">Consulter les offres</a>
        </header>

        <c:choose>
            <c:when test="${not empty proposition and proposition.statut == 'PROPOSEE'}">
                <section class="cand-offer-alert">
                    <div class="cand-alert-icon">!</div>
                    <div class="cand-alert-content">
                        <span class="cand-alert-label">NOUVELLE NOTIFICATION</span>
                        <h2>Une proposition d'embauche vous attend</h2>
                        <p>Le service RH vous propose le poste <strong><c:out value="${proposition.offre.titre}"/></strong>.</p>
                    </div>
                    <a href="${ctx}/candidat/proposition" class="cand-button cand-button-gold">Consulter la proposition →</a>
                </section>
            </c:when>
            <c:otherwise>
                <section class="cand-welcome">
                    <div><span class="cand-alert-label">VOTRE ESPACE</span><h2>Votre suivi de recrutement</h2><p>Aucune proposition d'embauche active pour le moment. Consultez régulièrement votre espace pour suivre votre dossier.</p></div>
                    <span class="cand-welcome-icon">✓</span>
                </section>
            </c:otherwise>
        </c:choose>

        <div class="cand-grid">
            <section class="cand-card cand-card-large">
                <div class="cand-card-heading"><div><span class="cand-card-kicker">PROPOSITION</span><h2>Mon dossier de recrutement</h2></div><span class="cand-card-number">01</span></div>
                <c:choose>
                    <c:when test="${not empty proposition}">
                        <div class="cand-job">
                            <div class="cand-job-icon">▣</div>
                            <div><span class="cand-label">Poste proposé</span><strong><c:out value="${proposition.offre.titre}"/></strong><small><c:out value="${proposition.offre.poste.intitule}"/> · <c:out value="${proposition.offre.departement.nom}"/></small></div>
                            <span class="cand-status ${proposition.statut == 'PROPOSEE' ? 'cand-status-pending' : (proposition.statut == 'ACCEPTEE' ? 'cand-status-ok' : 'cand-status-danger')}"><c:out value="${proposition.statutAffiche}"/></span>
                        </div>
                        <dl class="cand-meta-grid">
                            <div><dt>Référence</dt><dd><c:out value="${proposition.offre.reference}"/></dd></div>
                            <div><dt>Proposition reçue le</dt><dd><c:out value="${proposition.datePropositionFormatee}"/></dd></div>
                            <div><dt>Service</dt><dd><c:out value="${proposition.offre.departement.nom}"/></dd></div>
                            <div><dt>Salaire mensuel</dt><dd><c:choose><c:when test="${not empty proposition.salairePropose}"><c:out value="${proposition.salairePropose}"/> Ar</c:when><c:otherwise>À définir</c:otherwise></c:choose></dd></div>
                            <div><dt>Contrat proposé</dt><dd><c:out value="${proposition.typeContratPropose}"/></dd></div>
                        </dl>
                        <a class="cand-link" href="${ctx}/candidat/proposition">Afficher le détail de la proposition →</a>
                    </c:when>
                    <c:otherwise><div class="cand-empty"><span>□</span><p>Votre dossier ne contient pas encore de proposition d'embauche.</p></div></c:otherwise>
                </c:choose>
            </section>

            <section class="cand-card">
                <div class="cand-card-heading"><div><span class="cand-card-kicker">PROFIL</span><h2>Mes coordonnées</h2></div><span class="cand-card-number">02</span></div>
                <div class="cand-contact"><div class="cand-contact-row"><span>Nom complet</span><strong><c:out value="${candidat.nomComplet}"/></strong></div><div class="cand-contact-row"><span>Email</span><strong><c:out value="${candidat.email}"/></strong></div><div class="cand-contact-row"><span>Téléphone</span><strong><c:out value="${empty candidat.telephone ? '-' : candidat.telephone}"/></strong></div><div class="cand-contact-row"><span>Ville</span><strong><c:out value="${empty candidat.ville ? '-' : candidat.ville}"/></strong></div></div>
            </section>
        </div>

        <section class="cand-help"><span class="cand-help-icon">?</span><div><strong>Besoin d'aide ?</strong><p>Pour toute question concernant votre candidature, contactez directement le service des ressources humaines.</p></div><a href="mailto:rh@entreprise.fr">Contacter les RH</a></section>
    </main>
</div>
</body>
</html>
