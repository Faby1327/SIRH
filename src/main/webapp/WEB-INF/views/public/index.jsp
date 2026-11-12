<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Accueil · Besoins en personnel</title>
  <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
  <link rel="stylesheet" href="${ctx}/assets/css/base.css">
  <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
  <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
  <link rel="stylesheet" href="${ctx}/assets/css/public.css">
</head>
<body class="public">
<jsp:include page="/WEB-INF/views/fragments/public-entete.jsp">
  <jsp:param name="active" value="accueil"/>
</jsp:include>

<main>
  <section class="public-hero public-hero-accueil">
    <div class="public-conteneur">
      <p class="public-surtitre">Recrutement</p>
      <h1>Rejoignez-nous : <span>consultez nos offres</span></h1>
      <p>Parcourez les postes ouverts et déposez votre candidature en ligne. Créez votre compte candidat pour postuler.</p>
      <div class="public-actions" style="margin-top:22px">
        <a href="${ctx}/offres" class="public-btn public-btn-jaune">Voir les offres</a>
        <c:choose>
          <c:when test="${empty utilisateurConnecte}">
            <a href="${ctx}/inscription" class="public-btn public-btn-contour">Créer un compte</a>
          </c:when>
          <c:otherwise>
            <a href="${ctx}${empty espaceUrl ? '/candidat/espace' : espaceUrl}" class="public-btn public-btn-contour">Mon espace</a>
          </c:otherwise>
        </c:choose>
      </div>

      <div class="public-contact" id="contact">
        <div class="public-contact-item">
          <span class="public-contact-label">Email</span>
          <a class="public-contact-valeur" href="mailto:recrutement@notre-entreprise.mg">recrutement@notre-entreprise.mg</a>
        </div>
        <div class="public-contact-item">
          <span class="public-contact-label">Téléphone</span>
          <a class="public-contact-valeur" href="tel:+261202200000">+261 20 22 000 00</a>
        </div>
        <div class="public-contact-item">
          <span class="public-contact-label">Horaires</span>
          <span class="public-contact-valeur">Lun. – ven., 8 h – 17 h</span>
        </div>
      </div>
    </div>
  </section>
</main>
</body>
</html>
