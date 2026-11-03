<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nouvelle demande de recrutement - SIRH</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style_form.css">

    <!-- Font Awesome -->
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>

<body>

<div class="dashboard">
    <c:set var="activeMenu" value="nouvelleDemande"/>

    <jsp:include page="/WEB-INF/views/fragments/sidebar.jsp" />

    <div class="main">

        <!-- Retour -->
        <a href="${pageContext.request.contextPath}/demande/list"
           class="back-link">

            <i class="fas fa-arrow-left"></i>
            Retour à la liste des demandes

        </a>

        <div class="form-section">

            <form action="${actionUrl}" method="post">

                <h1>

                    <i class="fas fa-file-alt product-icon"></i>

                    Nouvelle demande de recrutement

                </h1>

                <p style="color:#6b7280;margin-bottom:30px;">

                    Remplissez tous les champs pour soumettre une demande
                    de recrutement.

                </p>

                <hr style="border:none;border-top:1px solid var(--gray);margin:20px 0;">
                <br>

                <!-- Poste -->

                <div class="form-group">

                    <label for="idPoste">

                        Poste souhaité
                        <span class="required-star">*</span>

                    </label>

                    <select id="idPoste"
                            name="idPoste"
                            required>

                        <option value="">-- Sélectionner un poste --</option>

                        <c:forEach var="poste" items="${postes}">

                            <option value="${poste.idPoste}">

                                ${poste.intitule}

                            </option>

                        </c:forEach>

                    </select>

                </div>

                <!-- Nombre de personnes + Budget -->

                <div class="form-row">

                    <div class="form-group">

                        <label for="nombrePersonnes">

                            Nombre de personnes
                            <span class="required-star">*</span>

                        </label>

                        <input
                                type="number"
                                id="nombrePersonnes"
                                name="nombrePersonnes"
                                min="1"
                                value="1"
                                required>

                    </div>

                    <div class="form-group">

                        <label for="budgetDemande">

                            Budget estimé par effectif (Ar)
                            <span class="required-star">*</span>

                        </label>

                        <input
                                type="number"
                                id="budgetDemande"
                                name="budgetDemande"
                                step="0.01"
                                min="0"
                                placeholder="Ex: 350000"
                                required>

                        <small>
                            Montant unitaire en ariary (par personne).
                        </small>

                    </div>

                </div>

                <!-- Date souhaitee -->

                <div class="form-group">

                    <label for="dateSouhaitee">

                        Date de prise de poste souhaitée

                    </label>

                    <input
                            type="date"
                            id="dateSouhaitee"
                            name="dateSouhaitee">

                </div>

                <!-- Motif -->

                <div class="form-group">

                    <label for="motif">

                        Motif de la demande
                        <span class="required-star">*</span>

                    </label>

                    <textarea
                            id="motif"
                            name="motif"
                            placeholder="Ex: Renforcer l'équipe de développement..."
                            required></textarea>

                    <small>
                        Justifiez le besoin de recrutement.
                    </small>

                </div>

                <!-- Boutons -->

                <div class="form-actions">

                    <button
                            type="submit"
                            class="btn-success">

                        <i class="fas fa-paper-plane"></i>
                        Soumettre la demande

                    </button>

                    <button
                            type="reset"
                            class="btn-secondary">

                        <i class="fas fa-undo"></i>
                        Réinitialiser

                    </button>

                    <a href="${pageContext.request.contextPath}/demande/list"
                       style="margin-left:auto;
                              align-self:center;
                              color:var(--primary);
                              text-decoration:none;">

                        <i class="fas fa-times"></i>
                        Annuler

                    </a>

                </div>

            </form>

        </div>

    </div>

</div>

</body>
</html>
