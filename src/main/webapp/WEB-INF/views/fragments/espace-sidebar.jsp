<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="barre">
    <div class="marque">
        <div class="marque-titre">Besoins en personnel</div>
        <div class="marque-sous-titre">Bloc 1 · Expression des besoins</div>
    </div>
    <nav class="menu" aria-label="Navigation principale">
        <c:choose>
            <c:when test="${param.role == 'manager'}">
                <a href="${pageContext.request.contextPath}/manager/accueil" class="lien ${param.active == 'accueil' ? 'actif' : ''}">Tableau de bord</a>
                <a href="${pageContext.request.contextPath}/manager/nouvelle" class="lien ${param.active == 'nouvelle' ? 'actif' : ''}">Nouvelle demande</a>
                <a href="${pageContext.request.contextPath}/manager/candidats" class="lien ${param.active == 'candidats' ? 'actif' : ''}">Candidats à évaluer</a>
            </c:when>
            <c:when test="${param.role == 'rh'}">
                <a href="${pageContext.request.contextPath}/rh/accueil" class="lien ${param.active == 'demandes' || param.active == 'accueil' || empty param.active ? 'actif' : ''}">Demandes</a>
                <a href="${pageContext.request.contextPath}/rh/accueil#offres" class="lien ${param.active == 'offres' || param.active == 'candidatures' ? 'actif' : ''}">Offres d'emploi</a>
                <a href="${pageContext.request.contextPath}/rh/entretiens" class="lien ${param.active == 'entretiens' ? 'actif' : ''}">Entretiens et tests</a>
                <a href="${pageContext.request.contextPath}/rh/employes" class="lien ${param.active == 'employes' ? 'actif' : ''}">Employés et contrats</a>
                <a href="${pageContext.request.contextPath}/rh/contrats" class="lien ${param.active == 'contrats' ? 'actif' : ''}">Liste des contrats</a>
                <a href="${pageContext.request.contextPath}/rh/paie" class="lien ${param.active == 'paie' ? 'actif' : ''}">Fiches de paie</a>
                <a href="${pageContext.request.contextPath}/rh/audit" class="lien ${param.active == 'audit' ? 'actif' : ''}">Journal d'audit</a>
            </c:when>
            <c:when test="${param.role == 'finance'}">
                <a href="${pageContext.request.contextPath}/finance/accueil" class="lien ${param.active == 'budgets' ? '' : 'actif'}">Demandes</a>
                <a href="${pageContext.request.contextPath}/finance/budgets" class="lien ${param.active == 'budgets' ? 'actif' : ''}">Budgets des départements</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/directeur/accueil" class="lien ${param.active == 'budgets' ? '' : 'actif'}">Demandes</a>
                <a href="${pageContext.request.contextPath}/directeur/budgets" class="lien ${param.active == 'budgets' ? 'actif' : ''}">Budgets des départements</a>
            </c:otherwise>
        </c:choose>
    </nav>
    <div class="bas">
        <div class="profil">
            <div class="avatar" aria-hidden="true"><c:out value="${empty utilisateurConnecte.nom ? 'U' : utilisateurConnecte.nom.substring(0, 1)}"/></div>
            <div>
                <div class="profil-nom"><c:out value="${utilisateurConnecte.nom}"/></div>
                <div class="profil-detail">
                    <c:choose>
                        <c:when test="${param.role == 'manager'}">Manager · <c:out value="${utilisateurConnecte.departement.nom}"/></c:when>
                        <c:when test="${param.role == 'rh'}">Ressources humaines</c:when>
                        <c:when test="${param.role == 'finance'}">Service Finance</c:when>
                        <c:otherwise>Direction</c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <form action="${pageContext.request.contextPath}/logout" method="get"><button type="submit" class="deconnexion">Se déconnecter</button></form>
    </div>
</aside>
