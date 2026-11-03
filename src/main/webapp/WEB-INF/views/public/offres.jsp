<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Nos offres d'emploi · Besoins en personnel</title>
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
  <section class="public-section public-section-fond" id="offres">
    <div class="public-conteneur">
      <div class="public-entete-section">
        <div>
          <h2>Nos offres d'emploi</h2>
          <p class="sous">Découvrez les postes actuellement disponibles.</p>
        </div>
        <p style="margin:0;color:var(--c-muted);font-size:14px">
          <b>${offres.size()}</b> offre<c:if test="${offres.size() != 1}">s</c:if> disponible<c:if test="${offres.size() != 1}">s</c:if>
        </p>
      </div>

      <c:if test="${not empty error}">
        <p class="b2-alerte b2-alerte-ko"><c:out value="${error}"/></p>
      </c:if>

      <c:choose>
        <c:when test="${empty offres}">
          <div class="public-encart">
            <h2>Aucune offre publiée pour le moment</h2>
            <p style="color:var(--c-muted);line-height:1.6">
              Les prochaines offres validées par la Direction apparaîtront sur cette page.
            </p>
          </div>
        </c:when>
        <c:otherwise>
          <div class="public-offres">
            <c:forEach var="offre" items="${offres}">
              <article class="public-carte">
                <div style="display:flex;gap:14px;align-items:center">
                  <span style="width:48px;height:48px;border-radius:50%;background:#1d3f86;display:flex;align-items:center;justify-content:center;flex:none">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"
                         stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <rect x="3" y="7" width="18" height="13" rx="2"/>
                      <path d="M9 7V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </span>
                  <div>
                    <h3><c:out value="${offre.titre}"/></h3>
                    <p class="meta" style="margin:2px 0 0">
                      <c:out value="${offre.departement.nom}"/>
                      · ${offre.nombrePersonnes} poste<c:if test="${offre.nombrePersonnes > 1}">s</c:if>
                    </p>
                  </div>
                </div>

                <div>
                  <span class="public-tag"><c:out value="${offre.typeRecrutementAffiche}"/></span>
                  <span class="public-tag public-tag-vert">Ouvert</span>
                </div>

                <p class="resume"><c:out value="${offre.description}"/></p>
                <p class="meta" style="margin:0">
                  Publiée le ${offre.datePublicationFormatee} · Réf. <c:out value="${offre.reference}"/>
                </p>
                <div class="pied-carte">
                  <span></span>
                  <a class="public-retour" href="${ctx}/offres/${offre.idOffre}">Voir l'offre →</a>
                </div>
              </article>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </section>
</main>

<jsp:include page="/WEB-INF/views/fragments/public-pied.jsp"/>
</body>
</html>
