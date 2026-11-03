<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription · Besoins en personnel</title>
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
        <p class="note">Un compte candidat permet de postuler aux offres publiées. Aucun CV n'est demandé à cette étape.</p>
    </section>

    <main class="formulaire">
        <div class="carte">
            <div>
                <h1>Créer mon compte</h1>
                <p class="intro">Renseignez vos informations. Elles seront reprises dans vos candidatures.</p>
            </div>

            <c:if test="${not empty error}">
                <p class="b2-alerte b2-alerte-ko"><c:out value="${error}"/></p>
            </c:if>

            <form class="champs" method="post" action="${ctx}/inscription">
                <div>
                    <label for="nom">Nom *</label>
                    <input id="nom" name="nom" type="text" value="<c:out value="${param.nom}"/>"
                           required maxlength="100" autocomplete="family-name">
                </div>
                <div>
                    <label for="prenom">Prénom *</label>
                    <input id="prenom" name="prenom" type="text" value="<c:out value="${param.prenom}"/>"
                           required maxlength="100" autocomplete="given-name">
                </div>
                <div>
                    <label for="mail">Adresse e-mail *</label>
                    <input id="mail" name="email" type="email" value="<c:out value="${param.email}"/>"
                           required maxlength="150" autocomplete="email">
                </div>
                <div>
                    <label for="telephone">Téléphone *</label>
                    <input id="telephone" name="telephone" type="tel" value="<c:out value="${param.telephone}"/>"
                           required maxlength="30" autocomplete="tel">
                </div>
                <div>
                    <label for="ville">Ville <span style="font-weight:400;color:var(--c-muted)">(facultatif)</span></label>
                    <input id="ville" name="ville" type="text" value="<c:out value="${param.ville}"/>"
                           maxlength="100" autocomplete="address-level2">
                </div>
                <div>
                    <label for="mdp">Mot de passe *</label>
                    <input id="mdp" name="motDePasse" type="password" minlength="8" required autocomplete="new-password">
                </div>
                <div>
                    <label for="mdp2">Confirmer le mot de passe *</label>
                    <input id="mdp2" name="confirmation" type="password" minlength="8" required autocomplete="new-password">
                </div>
                <button type="submit" class="bouton">Créer mon compte →</button>
            </form>

            <p class="intro">
                Déjà un compte ?
                <a href="${ctx}/login" style="color:#1d3f86;font-weight:600">Se connecter</a>
            </p>
            <a href="${ctx}/offres" class="public-retour">← Retour aux offres</a>
        </div>
    </main>
</div>
</body>
</html>
