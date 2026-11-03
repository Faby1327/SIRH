<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Proposition d'embauche</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
    <link rel="stylesheet" href="${ctx}/assets/css/espace.css">
    <link rel="stylesheet" href="${ctx}/assets/css/public.css">
    <link rel="stylesheet" href="${ctx}/assets/css/candidat.css">
</head>
<body class="espace">
<jsp:include page="/WEB-INF/views/fragments/public-entete.jsp"/>
<div class="c-espace">
    <aside class="espace-sidebar">
        <div class="espace-sidebar-en-tete">
            <div class="espace-avatar">${fn:substring(candidat.prenom,0,1)}${fn:substring(candidat.nom,0,1)}</div>
            <div>
                <div class="espace-nom"><c:out value="${candidat.nomComplet}"/></div>
                <div class="espace-role">Candidat</div>
            </div>
        </div>
        <nav class="espace-menu">
            <a class="espace-lien" href="${ctx}/candidat/espace"><span class="espace-icone">🏠</span> Mon espace</a>
            <a class="espace-lien actif" href="${ctx}/candidat/proposition"><span class="espace-icone">✉️</span> Proposition d'embauche</a>
            <a class="espace-lien" href="${ctx}/offres"><span class="espace-icone">💼</span> Voir les offres</a>
        </nav>
    </aside>
    <main class="espace-contenu">
        <header class="espace-entete-page">
            <div>
                <p class="espace-sous-titre">Espace candidat</p>
                <h1>Proposition d'embauche</h1>
            </div>
            <a href="${ctx}/candidat/espace" class="bouton">Retour à mon espace</a>
        </header>

        <c:choose>
            <c:when test="${empty proposition}">
                <section class="carte">
                    <div class="carte-corps">
                        <p class="vide">Aucune proposition d'embauche pour le moment.</p>
                    </div>
                </section>
            </c:when>
            <c:otherwise>
                <section class="carte">
                    <div class="carte-en-tete">
                        <div>
                            <h2 style="margin:0;"><c:out value="${proposition.offre.titre}"/></h2>
                            <p class="carte-sous-titre">Référence : <c:out value="${proposition.offre.reference}"/></p>
                        </div>
                        <span class="puce puce-attente"><c:out value="${proposition.statutAffiche}"/></span>
                    </div>
                    <div class="carte-corps" style="display:grid; gap:12px;">
                        <div><strong>Proposée le :</strong> <c:out value="${proposition.datePropositionFormatee}"/></div>
                        <div><strong>Salaire mensuel proposé :</strong>
                            <c:choose><c:when test="${not empty proposition.salairePropose}"><c:out value="${proposition.salairePropose}"/> Ar</c:when><c:otherwise>À définir avec les RH</c:otherwise></c:choose>
                        </div>
                        <div><strong>Contrat proposé :</strong> <c:out value="${proposition.typeContratPropose}"/></div>
                        <div><strong>Proposée par :</strong> <c:out value="${proposition.proposePar.nomComplet}"/></div>
                        <c:if test="${not empty proposition.commentaireRh}">
                            <div>
                                <strong>Message du RH</strong>
                                <p style="margin-top:8px; white-space:pre-wrap;"><c:out value="${proposition.commentaireRh}"/></p>
                            </div>
                        </c:if>
                        <c:if test="${proposition.statut == 'PROPOSEE'}">
                            <div class="proposition-reponse">
                                <p>Cette proposition vous convient-elle ? Vous pouvez l'accepter, la refuser ou demander un autre salaire et/ou un autre type de contrat.</p>
                                <div class="proposition-actions">
                                    <form method="post" action="${ctx}/candidat/proposition/accepter">
                                        <input type="hidden" name="propositionId" value="${proposition.id}"/>
                                        <button type="submit" class="bouton bouton-accepter">Accepter la proposition</button>
                                    </form>
                                    <form method="post" action="${ctx}/candidat/proposition/refuser">
                                        <input type="hidden" name="propositionId" value="${proposition.id}"/>
                                        <input type="text" name="commentaire" placeholder="Motif du refus (facultatif)"/>
                                        <button type="submit" class="bouton bouton-refuser">Refuser</button>
                                    </form>
                                </div>
                                <form class="demande-salaire" method="post" action="${ctx}/candidat/proposition/demander-salaire">
                                    <input type="hidden" name="propositionId" value="${proposition.id}"/>
                                    <label for="salaireDemande">Vous souhaitez modifier les conditions ? Indiquez uniquement les éléments à négocier.</label>
                                    <div><input id="salaireDemande" name="salaireDemande" type="number" min="0" step="0.01" placeholder="Salaire mensuel en Ar"/>
                                    <select name="typeContratDemande">
                                        <option value="">Conserver le contrat <c:out value="${proposition.typeContratPropose}"/></option>
                                        <option value="CDD">Demander un CDD</option>
                                        <option value="CDI">Demander un CDI</option>
                                    </select>
                                    <input name="commentaire" type="text" placeholder="Message pour les RH (facultatif)"/>
                                    <button type="submit" class="bouton">Demander ces conditions</button></div>
                                </form>
                            </div>
                        </c:if>
                        <c:if test="${proposition.statut == 'DEMANDE_SALAIRE' or proposition.statut == 'DEMANDE_CONTRAT' or proposition.statut == 'DEMANDE_CONDITIONS'}">
                            <div class="proposition-reponse proposition-attente">
                                <strong>Votre demande de modification est en cours d'examen par les RH.</strong>
                                <c:if test="${not empty proposition.salaireDemandeCandidat}"><p>Salaire demandé : <c:out value="${proposition.salaireDemandeCandidat}"/> Ar mensuels.</p></c:if>
                                <c:if test="${not empty proposition.typeContratDemandeCandidat}"><p>Contrat demandé : <c:out value="${proposition.typeContratDemandeCandidat}"/></p></c:if>
                            </div>
                        </c:if>
                    </div>
                </section>
            </c:otherwise>
        </c:choose>
    </main>
</div>
</body>
</html>
