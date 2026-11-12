<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nouvelle demande</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/fonts.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/besoins.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/composants.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/manager.css">
</head>
<body>
<div class="c-espace">
    <jsp:include page="/WEB-INF/views/fragments/espace-sidebar.jsp"><jsp:param name="role" value="manager"/><jsp:param name="active" value="nouvelle"/></jsp:include>

    <div class="contenu">
        <main class="page-espace c-form">
            <header class="entete"><div><p class="surtitre"><a href="${pageContext.request.contextPath}/manager/accueil">Tableau de bord</a> / Nouvelle demande</p><h1>Nouvelle demande de personnel</h1></div><span class="puce puce-brouillon">Brouillon</span></header>

            <c:if test="${not empty error}"><div class="bandeau bandeau-erreur" role="alert"><c:out value="${error}"/></div></c:if>

            <div class="colonnes">
                <form class="formulaire" action="${pageContext.request.contextPath}/manager/nouvelle" method="post">
                    <div class="groupe">
                        <span class="etiquette">Type de besoin</span>
                        <div class="choix-liste">
                            <label class="choix choix-actif"><input type="radio" name="typeRecrutement" value="CREATION" checked><span class="choix-titre">Création de poste</span><span class="choix-aide">Un nouveau besoin en personnel</span></label>
                            <label class="choix"><input type="radio" name="typeRecrutement" value="REMPLACEMENT"><span class="choix-titre">Remplacement</span><span class="choix-aide">Remplacer une personne qui quitte son poste</span></label>
                        </div>
                    </div>
                    <div class="groupe">
                        <label for="idPoste" class="etiquette">Poste demandé</label>
                        <select id="idPoste" name="idPoste" class="champ" required>
                            <option value="">Selectionner un poste</option>
                            <c:forEach var="poste" items="${postes}">
                                <option value="${poste.idPoste}"><c:out value="${poste.intitule}"/></option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="rangee">
                        <div class="moitie"><label for="nombrePersonnes" class="etiquette">Nombre de personnes</label><input id="nombrePersonnes" name="nombrePersonnes" type="number" min="1" value="1" class="champ" required></div>
                        <div class="moitie"><label for="budgetDemande" class="etiquette">Budget estimé par effectif (Ar)</label><input id="budgetDemande" name="budgetDemande" type="number" step="0.01" min="0" class="champ" required><div class="aide">Montant unitaire par personne. Le total sera calculé automatiquement : budget × effectif.</div></div>
                    </div>

                    <div><label for="dateSouhaitee" class="etiquette">Date souhaitée</label><input id="dateSouhaitee" name="dateSouhaitee" type="date" min="${dateMin}" class="champ"><div class="aide">Choisissez aujourd’hui ou une date ultérieure.</div></div>
                    <div><label for="motif" class="etiquette">Motif du besoin</label><textarea id="motif" name="motif" rows="4" class="champ motif" required></textarea></div>

                    <div class="pied">
                        <a href="${pageContext.request.contextPath}/manager/accueil" class="annuler">Annuler</a>
                        <div class="boutons"><button type="submit" name="action" value="BROUILLON" class="bouton">Enregistrer en brouillon</button><button type="submit" name="action" value="SOUMISE" class="bouton bouton-principal">Soumettre pour vérification</button></div>
                    </div>
                </form>
                <aside class="circuit" aria-labelledby="circuit-title"><h2 id="circuit-title">Circuit de validation</h2><ol><li><span class="numero numero-vous">1</span><div><div class="circuit-nom">Manager <span class="precision">(vous)</span></div><div class="circuit-texte">Crée et soumet la demande</div></div></li><li><span class="numero">2</span><div><div class="circuit-nom">Ressources humaines</div><div class="circuit-texte">Vérifient le besoin et sa justification</div></div></li><li><span class="numero">3</span><div><div class="circuit-nom">Service Finance</div><div class="circuit-texte">Vérifie la disponibilité du budget</div></div></li><li><span class="numero">4</span><div><div class="circuit-nom">Directeur</div><div class="circuit-texte">Examine et approuve la demande</div></div></li></ol></aside>
            </div>
        </main>
    </div>
</div>
<script>document.querySelectorAll('.choix-liste input[name="typeRecrutement"]').forEach((input) => input.addEventListener('change', () => { document.querySelectorAll('.choix-liste .choix').forEach((choice) => choice.classList.toggle('choix-actif', choice.contains(input) && input.checked)); }));</script>
</body>
</html>
