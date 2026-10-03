<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%-- Tomcat 9 usa JSTL 1.2 (javax): equivale a la URI jakarta.tags.core de JSTL 3. --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="codigo" value="${requestScope['javax.servlet.error.status_code']}"/>
<c:set var="titulo" value="${codigo == 404 ? 'Página no encontrada' : 'Error'}"/>
<%@ include file="fragmentos/header.jspf" %>

<div class="row justify-content-center">
    <div class="col-md-8 col-lg-6">
        <div class="tarjeta pagina-error">
            <c:choose>
                <c:when test="${codigo == 404}">
                    <span class="pagina-error-icono"><i class="bi bi-signpost-split" aria-hidden="true"></i></span>
                    <p class="pagina-error-codigo">404</p>
                    <h1>Página no encontrada</h1>
                    <p>La tarea o la página que buscas no existe o fue eliminada.</p>
                </c:when>
                <c:otherwise>
                    <span class="pagina-error-icono"><i class="bi bi-exclamation-octagon" aria-hidden="true"></i></span>
                    <p class="pagina-error-codigo">${empty codigo ? 'Error' : codigo}</p>
                    <h1>Ocurrió un error inesperado</h1>
                    <p>Algo salió mal al procesar tu solicitud. Vuelve a intentarlo en unos minutos.</p>
                </c:otherwise>
            </c:choose>
            <a href="${ctx}/tareas" class="btn btn-primary">
                <i class="bi bi-arrow-left" aria-hidden="true"></i> Volver a la lista
            </a>
        </div>
    </div>
</div>

<%@ include file="fragmentos/footer.jspf" %>
