<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Champs communs aux formulaires d'ajout et de modification d'un employé.
     Attributs attendus : form (EmployeForm), departements, postes, superieurs.
     Paramètre : mode = ajout | modification --%>
<section class="carte dossier-card">
    <div class="dossier-card-heading"><div><span class="dossier-icon">01</span><div><h2>Identité</h2><p>État civil et pièce d'identité</p></div></div></div>
    <div class="dossier-form-grid">
        <label>Nom *<input class="champ" name="nom" required maxlength="100" value="<c:out value='${form.nom}'/>"></label>
        <label>Prénom *<input class="champ" name="prenom" required maxlength="100" value="<c:out value='${form.prenom}'/>"></label>
        <label>Sexe
            <select class="champ" name="sexe">
                <option value="">—</option>
                <option value="M" ${form.sexe == 'M' ? 'selected' : ''}>Masculin</option>
                <option value="F" ${form.sexe == 'F' ? 'selected' : ''}>Féminin</option>
            </select></label>
        <label>Date de naissance<input class="champ" type="date" name="dateNaissance" value="<c:out value='${form.dateNaissance}'/>"></label>
        <label>Lieu de naissance<input class="champ" name="lieuNaissance" maxlength="100" value="<c:out value='${form.lieuNaissance}'/>"></label>
        <label>Nationalité<input class="champ" name="nationalite" maxlength="60" placeholder="Malagasy" value="<c:out value='${form.nationalite}'/>"></label>
        <label>N° de CIN<input class="champ" name="cinNumero" maxlength="30" value="<c:out value='${form.cinNumero}'/>"></label>
        <label>CIN délivrée le<input class="champ" type="date" name="cinDateDelivrance" value="<c:out value='${form.cinDateDelivrance}'/>"></label>
        <label>CIN délivrée à<input class="champ" name="cinLieuDelivrance" maxlength="100" value="<c:out value='${form.cinLieuDelivrance}'/>"></label>
    </div>
</section>

<section class="carte dossier-card">
    <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-green">02</span><div><h2>Coordonnées et situation personnelle</h2><p>Contact, adresse et personne à prévenir</p></div></div></div>
    <div class="dossier-form-grid">
        <label>Téléphone<input class="champ" name="telephone" maxlength="30" value="<c:out value='${form.telephone}'/>"></label>
        <label>Email personnel<input class="champ" type="email" name="emailPersonnel" maxlength="150" value="<c:out value='${form.emailPersonnel}'/>"></label>
        <label>Situation matrimoniale
            <select class="champ" name="situationMatrimoniale">
                <option value="">—</option>
                <option value="CELIBATAIRE" ${form.situationMatrimoniale == 'CELIBATAIRE' ? 'selected' : ''}>Célibataire</option>
                <option value="MARIE" ${form.situationMatrimoniale == 'MARIE' ? 'selected' : ''}>Marié(e)</option>
                <option value="UNION_LIBRE" ${form.situationMatrimoniale == 'UNION_LIBRE' ? 'selected' : ''}>Union libre</option>
                <option value="DIVORCE" ${form.situationMatrimoniale == 'DIVORCE' ? 'selected' : ''}>Divorcé(e)</option>
                <option value="VEUF" ${form.situationMatrimoniale == 'VEUF' ? 'selected' : ''}>Veuf / veuve</option>
            </select></label>
        <label class="pleine">Adresse<input class="champ" name="adresse" maxlength="255" value="<c:out value='${form.adresse}'/>"></label>
        <label>Commune<input class="champ" name="commune" maxlength="100" value="<c:out value='${form.commune}'/>"></label>
        <label>Région<input class="champ" name="region" maxlength="100" value="<c:out value='${form.region}'/>"></label>
        <label>Nombre d'enfants<input class="champ" type="number" min="0" name="nombreEnfants" value="<c:out value='${empty form.nombreEnfants ? 0 : form.nombreEnfants}'/>"></label>
        <label>Contact d'urgence : nom<input class="champ" name="contactUrgenceNom" maxlength="150" value="<c:out value='${form.contactUrgenceNom}'/>"></label>
        <label>Lien avec l'employé<input class="champ" name="contactUrgenceRelation" maxlength="60" placeholder="Conjoint, parent…" value="<c:out value='${form.contactUrgenceRelation}'/>"></label>
        <label>Contact d'urgence : téléphone<input class="champ" name="contactUrgenceTelephone" maxlength="30" value="<c:out value='${form.contactUrgenceTelephone}'/>"></label>
    </div>
</section>

<section class="carte dossier-card">
    <div class="dossier-card-heading"><div><span class="dossier-icon dossier-icon-blue">03</span><div><h2>Situation professionnelle</h2><p>Affectation, hiérarchie et statut</p></div></div></div>
    <div class="dossier-form-grid">
        <label>Département
            <select class="champ" name="departementId">
                <option value="">—</option>
                <c:forEach var="d" items="${departements}"><option value="${d.idDepartement}" ${form.departementId == d.idDepartement ? 'selected' : ''}><c:out value="${d.nom}"/></option></c:forEach>
            </select></label>
        <label>Poste
            <select class="champ" name="posteId">
                <option value="">—</option>
                <c:forEach var="p" items="${postes}"><option value="${p.idPoste}" ${form.posteId == p.idPoste ? 'selected' : ''}><c:out value="${p.intitule}"/> (<c:out value="${p.departement.nom}"/>)</option></c:forEach>
            </select></label>
        <label>Supérieur hiérarchique
            <select class="champ" name="superieurId">
                <option value="">Aucun</option>
                <c:forEach var="s" items="${superieurs}"><option value="${s.id}" ${form.superieurId == s.id ? 'selected' : ''}><c:out value="${s.matricule}"/> · <c:out value="${s.personne.nomComplet}"/></option></c:forEach>
            </select></label>
        <label>Date d'embauche *<input class="champ" type="date" name="dateEmbauche" required value="<c:out value='${form.dateEmbauche}'/>"></label>
        <label>Salaire actuel (Ar)<input class="champ" type="number" min="0" step="0.01" name="salaireActuel" value="<c:out value='${form.salaireActuel}'/>"></label>
        <label>Statut
            <select class="champ" name="statut">
                <option value="ACTIF" ${form.statut == 'ACTIF' || empty form.statut ? 'selected' : ''}>Actif</option>
                <option value="SUSPENDU" ${form.statut == 'SUSPENDU' ? 'selected' : ''}>Suspendu</option>
                <option value="SORTI" ${form.statut == 'SORTI' ? 'selected' : ''}>Sorti</option>
            </select></label>
        <label>Date de départ <small>(si sorti)</small><input class="champ" type="date" name="dateDepart" value="<c:out value='${form.dateDepart}'/>"></label>
        <label>Photo <small>(JPG ou PNG)</small><input class="champ" type="file" name="photoFichier" accept=".jpg,.jpeg,.png,image/jpeg,image/png"></label>
    </div>
</section>

<c:if test="${param.mode == 'ajout'}">
<section class="carte dossier-card">
    <div class="dossier-card-heading"><div><span class="dossier-icon">04</span><div><h2>Accès à l'application</h2><p>Facultatif : permet à l'employé de consulter son profil</p></div></div></div>
    <div class="dossier-form-grid">
        <label class="pleine case"><span><input type="checkbox" name="creerCompte" value="true" ${form.creerCompte ? 'checked' : ''}> Créer un compte de connexion (rôle EMPLOYÉ) avec l'email personnel</span></label>
        <label>Mot de passe initial <small>(8 caractères minimum)</small><input class="champ" type="password" name="motDePasse" minlength="8" autocomplete="new-password"></label>
    </div>
</section>
</c:if>
