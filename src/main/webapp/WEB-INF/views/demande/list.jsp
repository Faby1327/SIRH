<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mes demandes de recrutement - SIRH</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style_form.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style_list.css">

    <!-- Font Awesome -->
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>

<body>

<div class="dashboard">
    <c:set var="activeMenu" value="demandes"/>

    <jsp:include page="/WEB-INF/views/fragments/sidebar.jsp" />

    <div class="main">

        <div class="table-container">

            <!-- En-tête -->

            <div class="table-header">

                <h1>

                    <i class="fas fa-file-alt"
                       style="color:var(--primary);margin-right:10px;"></i>

                    Mes demandes de recrutement

                </h1>

                <a href="${pageContext.request.contextPath}/demande/new"
                   class="btn-add">

                    Nouvelle demande

                </a>

            </div>

            <!-- Message de confirmation -->

            <c:if test="${not empty message}">

                <div class="alert-success">

                    <i class="fas fa-check-circle"></i>

                    ${message}

                </div>

            </c:if>

            <!-- Tableau -->

            <table>

                <thead>

                <tr>

                    <th><i class="fas fa-hashtag"></i> ID</th>

                    <th><i class="fas fa-briefcase"></i> Poste</th>

                    <th><i class="fas fa-users"></i> Nb personnes</th>

                    <th><i class="fas fa-money-bill"></i> Budget (Ar)</th>

                    <th><i class="fas fa-comment"></i> Motif</th>

                    <th><i class="fas fa-calendar"></i> Date demande</th>

                    <th><i class="fas fa-clock"></i> Statut</th>

                </tr>

                </thead>

                <tbody>

                <!-- Aucune demande -->

                <c:if test="${empty demandes}">

                    <tr>

                        <td colspan="7">

                            <div class="empty-state">

                                <i class="fas fa-inbox"
                                   style="font-size:48px;
                                          color:#d1d5db;
                                          margin-bottom:15px;
                                          display:block;"></i>

                                <p>
                                    Aucune demande de recrutement pour le moment.
                                </p>

                                <a href="${pageContext.request.contextPath}/demande/new"
                                   class="btn-add">

                                    <i class="fas fa-plus"></i>

                                    Créer la première demande

                                </a>

                            </div>

                        </td>

                    </tr>

                </c:if>

                <!-- Liste -->

                <c:forEach var="demande" items="${demandes}">

                    <tr>

                        <td>

                            <span class="product-id">

                                ${demande.idDemande}

                            </span>

                        </td>

                        <td>

                            <strong>

                                ${demande.poste.intitule}

                            </strong>

                        </td>

                        <td>

                            ${demande.nombrePersonnes}

                        </td>

                        <td>

                            <span class="price-tag">

                                ${demande.budgetDemande}

                            </span>

                        </td>

                        <td>

                            <span style="max-width:250px; display:inline-block;">

                                ${demande.motif}

                            </span>

                        </td>

                        <td>

                            ${demande.dateDemande}

                        </td>

                        <td>

                            <c:choose>

                                <c:when test="${demande.statutActuel.libelle == 'VALIDEE'}">

                                    <span class="badge badge-success">

                                        <i class="fas fa-check-circle"></i>

                                        ${demande.statutActuel.libelle}

                                    </span>

                                </c:when>

                                <c:when test="${empty demande.statutActuel}">

                                    <span class="badge badge-warning">

                                        SOUMISE

                                    </span>

                                </c:when>

                                <c:otherwise>

                                    <span class="badge badge-warning">

                                        <i class="fas fa-hourglass-half"></i>

                                        ${demande.statutActuel.libelle}

                                    </span>

                                </c:otherwise>

                            </c:choose>

                        </td>

                    </tr>

                </c:forEach>

                </tbody>

            </table>

        </div>

    </div>

</div>

</body>
</html>
