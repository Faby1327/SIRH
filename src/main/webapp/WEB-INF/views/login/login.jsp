<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion · Besoins en personnel</title>
    <link rel="stylesheet" href="${ctx}/assets/css/fonts.css">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/besoins.css">
    <link rel="stylesheet" href="${ctx}/assets/css/composants.css">
    <link rel="stylesheet" href="${ctx}/assets/css/public.css">
</head>

<body>
<div class="page c-login">
    <section class="presentation" aria-label="Présentation">
        <div class="marque">
            <div class="marque-titre">Besoins en personnel</div>
            <div class="marque-sous-titre">Blocs 1 et 2 · Du besoin au candidat retenu</div>
        </div>
        <div class="circuit">
            <h2>De la demande autorisée à la proposition d'embauche</h2>
            <ol>
                <li><span class="numero" aria-hidden="true">1</span><div><div class="etape-titre">Ressources humaines</div><div class="etape-texte">Créent et publient l'offre à partir d'une demande autorisée</div></div></li>
                <li><span class="numero" aria-hidden="true">2</span><div><div class="etape-titre">Candidat</div><div class="etape-texte">Crée son compte, consulte les offres et dépose sa candidature</div></div></li>
                <li><span class="numero" aria-hidden="true">3</span><div><div class="etape-titre">RH et Manager du service</div><div class="etape-texte">Examinent les dossiers et organisent les entretiens</div></div></li>
                <li><span class="numero" aria-hidden="true">4</span><div><div class="etape-titre">Ressources humaines</div><div class="etape-texte">Retiennent un candidat et transmettent la proposition d'embauche</div></div></li>
            </ol>
        </div>
        <p class="note">La consultation des offres est libre. Un compte est nécessaire pour déposer une candidature.</p>
    </section>

    <main class="formulaire">
        <div class="carte">
            <div>
                <h1>Connexion</h1>
                <p class="intro">Connectez-vous pour postuler ou pour accéder à votre espace de travail.</p>
            </div>

            <c:if test="${param.inscription == 'ok'}">
                <p class="b2-alerte b2-alerte-ok">Votre compte a bien été créé. Vous pouvez maintenant vous connecter.</p>
            </c:if>
            <c:if test="${not empty error}">
                <p class="b2-alerte b2-alerte-ko"><c:out value="${error}"/></p>
            </c:if>

            <form class="champs" action="${ctx}/login" method="post">
                <c:if test="${not empty next}">
                    <input type="hidden" name="next" value="<c:out value="${next}"/>">
                </c:if>
                <div>
                    <label for="email">Identifiant (adresse e-mail)</label>
                    <input id="email" name="email" type="email" placeholder="Adresse email" required autofocus>
                </div>
                <div>
                    <label for="motDePasse">Mot de passe</label>
                    <input id="motDePasse" name="motDePasse" type="password" required>
                </div>
                <button type="submit" class="bouton">Se connecter →</button>
            </form>

            <p class="intro">
                Pas encore de compte ?
                <a href="${ctx}/inscription" style="color:#1d3f86;font-weight:600">S'inscrire</a>
            </p>
            <a href="${ctx}/offres" class="public-retour">← Retour aux offres</a>

            <details class="comptes">
                <summary style="cursor:pointer;font-weight:600;color:#1d3f86">Comptes de démonstration</summary>
                <p>Utilisez ces comptes de test pour accéder aux espaces de l'application.</p>
                <table>
                    <thead>
                    <tr><th scope="col">Poste</th><th scope="col">Identifiant</th><th scope="col">Mot de passe</th></tr>
                    </thead>
                    <tbody>
                    <tr><td>Manager</td><td class="code">andry.manager@sirh.test</td><td class="code">hash_test_1</td></tr>
                    <tr><td>Ressources humaines</td><td class="code">hery.rh@sirh.test</td><td class="code">hash_test_3</td></tr>
                    <tr><td>Finance</td><td class="code">fara.finance@sirh.test</td><td class="code">hash_test_4</td></tr>
                    <tr><td>Directeur</td><td class="code">solo.direction@sirh.test</td><td class="code">hash_test_5</td></tr>
                    <tr><td>Candidat</td><td colspan="2">Créez votre compte avec le lien « S'inscrire ».</td></tr>
                    </tbody>
                </table>
            </details>
        </div>
    </main>
</div>
</body>
</html>
