<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<header class="public-entete">
  <div class="public-conteneur public-barre">
    <a href="${ctx}/" class="public-marque" aria-label="Besoins en personnel, accueil">
      <svg width="46" height="34" viewBox="0 0 46 34" aria-hidden="true" style="flex:none">
        <circle cx="13" cy="10" r="7" fill="#ffc233"/>
        <circle cx="33" cy="10" r="7" fill="#ffc233"/>
        <path d="M2 33c0-8 5-12 11-12s11 4 11 12z" fill="#ffc233"/>
        <path d="M22 33c0-8 5-12 11-12s11 4 11 12z" fill="#ffc233"/>
      </svg>
      <span><b>Besoins en personnel</b><small>Recrutement</small></span>
    </a>

    <nav class="public-nav" aria-label="Navigation publique">
      <a href="${ctx}/" class="${param.active == 'accueil' ? 'actif' : ''}">Accueil</a>
      <a href="${ctx}/offres" class="${param.active == 'offres' ? 'actif' : ''}">Nos offres</a>
    </nav>

    <div class="public-actions">
      <c:choose>
        <c:when test="${not empty utilisateurConnecte}">
          <c:if test="${utilisateurConnecte.role != 'CANDIDAT'}">
            <a href="${ctx}${espaceUrl}" class="public-btn public-btn-contour">Mon espace</a>
          </c:if>
          <a href="${ctx}/logout" class="public-btn public-btn-jaune">Se déconnecter</a>
        </c:when>
        <c:otherwise>
          <a href="${ctx}/login" class="public-btn public-btn-contour">Connexion</a>
          <a href="${ctx}/inscription" class="public-btn public-btn-jaune">S'inscrire</a>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</header>
