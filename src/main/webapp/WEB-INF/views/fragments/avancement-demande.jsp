<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="statutAvancement" value="${demande.statutActuel.libelle}"/>
<div class="c-stepbar c-stepbar-detail" aria-label="Avancement de la demande">
    <c:choose>
        <c:when test="${statutAvancement == 'BROUILLON'}"><span class="libelle">Non soumise</span><div class="barre"><span class="segment a-faire"></span><span class="segment a-faire"></span><span class="segment a-faire"></span><span class="segment a-faire"></span></div></c:when>
        <c:when test="${statutAvancement == 'SOUMISE'}"><span class="libelle">En attente RH</span><div class="barre"><span class="segment faite"></span><span class="segment en-cours"></span><span class="segment a-faire"></span><span class="segment a-faire"></span></div></c:when>
        <c:when test="${statutAvancement == 'EN_VERIFICATION_FINANCIERE'}"><span class="libelle">En attente Finance</span><div class="barre"><span class="segment faite"></span><span class="segment faite"></span><span class="segment en-cours"></span><span class="segment a-faire"></span></div></c:when>
        <c:when test="${statutAvancement == 'BUDGET_INSUFFISANT'}"><span class="libelle">Budget insuffisant</span><div class="barre"><span class="segment faite"></span><span class="segment faite"></span><span class="segment refusee"></span><span class="segment a-faire"></span></div></c:when>
        <c:when test="${statutAvancement == 'EN_ATTENTE_VALIDATION'}"><span class="libelle">En attente Direction</span><div class="barre"><span class="segment faite"></span><span class="segment faite"></span><span class="segment faite"></span><span class="segment en-cours"></span></div></c:when>
        <c:when test="${statutAvancement == 'VALIDEE'}"><span class="libelle">Approuvée</span><div class="barre"><span class="segment approuvee"></span><span class="segment approuvee"></span><span class="segment approuvee"></span><span class="segment approuvee"></span></div></c:when>
        <c:when test="${statutAvancement == 'REFUSEE'}"><span class="libelle"><c:choose><c:when test="${etapeRefus == 2}">Refusée par RH</c:when><c:when test="${etapeRefus == 3}">Refusée par Finance</c:when><c:otherwise>Refusée par le Directeur</c:otherwise></c:choose></span><div class="barre"><span class="segment faite"></span><span class="segment ${etapeRefus == 2 ? 'refusee' : (etapeRefus > 2 ? 'faite' : 'a-faire')}"></span><span class="segment ${etapeRefus == 3 ? 'refusee' : (etapeRefus > 3 ? 'faite' : 'a-faire')}"></span><span class="segment ${etapeRefus == 4 ? 'refusee' : 'a-faire'}"></span></div></c:when>
        <c:otherwise><span class="libelle">Annulée</span><div class="barre"><span class="segment faite"></span><span class="segment a-faire"></span><span class="segment a-faire"></span><span class="segment a-faire"></span></div></c:otherwise>
    </c:choose>
</div>
