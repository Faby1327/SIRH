<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${offre.titre}"/> · Besoins en personnel</title>
  <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
  <link rel="stylesheet" href="${ctx}/assets/css/base.css">
  <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
  <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
  <link rel="stylesheet" href="${ctx}/assets/css/public.css">
</head>
<body class="public">
<jsp:include page="/WEB-INF/views/fragments/public-entete.jsp">
  <jsp:param name="active" value="offres"/>
</jsp:include>

<main>
  <section class="public-section" style="padding-top:36px">
    <div class="public-conteneur">
      <a href="${ctx}/offres" class="public-retour">← Retour aux offres</a>

      <c:if test="${not empty messageCandidature}">
        <p class="b2-alerte b2-alerte-ok" style="margin-top:16px"><c:out value="${messageCandidature}"/></p>
      </c:if>
      <c:if test="${not empty error}">
        <p class="b2-alerte b2-alerte-ko" style="margin-top:16px"><c:out value="${error}"/></p>
      </c:if>

      <div class="public-detail" style="margin-top:16px">
        <article class="public-encart">
          <span class="public-tag"><c:out value="${offre.departement.nom}"/></span>
          <span class="public-tag public-tag-vert"><c:out value="${offre.typeRecrutementAffiche}"/></span>
          <h1 style="font-size:30px;margin:12px 0 6px"><c:out value="${offre.titre}"/></h1>
          <p class="meta">
            Réf. <c:out value="${offre.reference}"/>
            · Publiée le ${offre.datePublicationFormatee}
            · ${offre.nombrePersonnes} poste<c:if test="${offre.nombrePersonnes > 1}">s</c:if>
          </p>

          <h2 style="font-size:18px;margin-top:22px">Présentation du poste</h2>
          <p class="description"><c:out value="${offre.description}"/></p>
        </article>

        <aside class="public-encart">
          <h2 style="font-size:18px">Candidature</h2>
          <p style="font-size:14px;line-height:1.6;color:var(--c-muted)">
            Pour postuler, connectez-vous avec votre compte candidat. Si vous n'en avez pas
            encore, créez-le en quelques instants.
          </p>

          <c:choose>
            <c:when test="${dejaPostule}">
              <p class="b2-alerte b2-alerte-warn" style="margin-top:12px">
                Vous avez déjà postulé à cette offre. Le service RH consultera votre dossier.
              </p>
            </c:when>
            <c:when test="${candidatConnecte}">
              <a class="public-bouton public-bouton-jaune" style="display:block;text-align:center;margin-top:12px"
                 href="${ctx}/offres/${offre.idOffre}/postuler">Postuler à cette offre</a>
            </c:when>
            <c:when test="${empty utilisateurConnecte}">
              <a class="public-bouton public-bouton-jaune" style="display:block;text-align:center;margin-top:12px"
                 href="${ctx}/login?next=/offres/${offre.idOffre}/postuler">Se connecter pour postuler</a>
              <a class="public-retour" style="display:block;text-align:center;margin-top:12px"
                 href="${ctx}/inscription">Créer un compte candidat</a>
            </c:when>
            <c:otherwise>
              <p class="b2-alerte b2-alerte-warn" style="margin-top:12px">
                Votre compte n'est pas un compte candidat. Déconnectez-vous, puis reconnectez-vous
                avec un compte candidat pour postuler.
              </p>
              <a class="public-bouton" style="display:block;text-align:center;margin-top:12px"
                 href="${ctx}/logout">Se déconnecter</a>
            </c:otherwise>
          </c:choose>
        </aside>
      </div>
    </div>
  </section>
</main>

<jsp:include page="/WEB-INF/views/fragments/public-pied.jsp"/>
</body>
</html>
