<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

</head>
<div class="sidebar">

    <h2>SIRH</h2>

    <a href="${pageContext.request.contextPath}/demande/list"
    class="${activeMenu == 'demandes' ? 'active' : ''}">
        <i class="fas fa-file-alt"></i>
        Mes demandes
    </a>

    <a href="${pageContext.request.contextPath}/demande/new"
    class="${activeMenu == 'nouvelleDemande' ? 'active' : ''}">
        <i class="fas fa-plus-circle"></i>
        Nouvelle demande
    </a>

    <hr style="border-color: rgba(255,255,255,0.1); margin: 20px 0;">

    <div style="color:#9ca3af; font-size:14px; margin-bottom:10px;">
        <i class="fas fa-user-circle"></i>
        ${utilisateurConnecte.nom}
        <br>
        <span style="color:#6b7280; font-size:12px;">${utilisateurConnecte.role}</span>
    </div>

    <a href="${pageContext.request.contextPath}/logout">
        <i class="fas fa-sign-out-alt"></i>
        Déconnexion
    </a>

</div>
