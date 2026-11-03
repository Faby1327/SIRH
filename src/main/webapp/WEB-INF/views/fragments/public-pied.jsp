<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<footer class="public-pied">
  <div class="public-conteneur public-pied-grille">
    <div>
      <div style="display:flex;gap:10px;align-items:center;color:#fff">
        <svg width="46" height="34" viewBox="0 0 46 34" aria-hidden="true" style="flex:none">
          <circle cx="13" cy="10" r="7" fill="#ffc233"/>
          <circle cx="33" cy="10" r="7" fill="#ffc233"/>
          <path d="M2 33c0-8 5-12 11-12s11 4 11 12z" fill="#ffc233"/>
          <path d="M22 33c0-8 5-12 11-12s11 4 11 12z" fill="#ffc233"/>
        </svg>
        <span><b style="display:block;font-size:16px">Besoins en personnel</b><small style="color:#b9c6e2">Recrutement</small></span>
      </div>
      <p style="margin:12px 0 0;line-height:1.6">Consultez les offres d'emploi, créez votre compte candidat et déposez votre candidature en ligne.</p>
    </div>
    <div>
      <p style="color:#fff;font-weight:600;margin:0 0 8px">Contact</p>
      <p style="margin:0;line-height:1.8">Service RH : recrutement@notre-entreprise.mg<br>Téléphone : +261 20 22 000 00<br>Lun. – ven., 8 h – 17 h</p>
    </div>
    <div>
      <p style="color:#fff;font-weight:600;margin:0 0 8px">Navigation</p>
      <p style="margin:0;line-height:1.9">
        <a href="${ctx}/" style="margin:0">Accueil</a><br>
        <a href="${ctx}/offres" style="margin:0">Nos offres</a><br>
        <a href="${ctx}/login" style="margin:0">Connexion</a><br>
        <a href="${ctx}/inscription" style="margin:0">Inscription</a>
      </p>
    </div>
  </div>
  <div class="public-conteneur public-pied-bas" style="margin-top:22px;padding-top:16px;border-top:1px solid rgba(255,255,255,.15)">
    <span>© 2026 Besoins en personnel. Tous droits réservés.</span>
    <span>Bloc 2 · Recrutement</span>
  </div>
</footer>
