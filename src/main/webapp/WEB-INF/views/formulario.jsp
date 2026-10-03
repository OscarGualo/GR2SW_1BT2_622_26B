<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Tomcat 9 usa JSTL 1.2 (javax): equivale a la URI jakarta.tags.core de JSTL 3. --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- El mismo formulario sirve para crear (sin id) y editar (con id). --%>
<c:set var="titulo" value="${empty tarea.id ? 'Nueva tarea' : 'Editar tarea'}"/>
<c:set var="seccion" value="${empty tarea.id ? 'nuevo' : ''}"/>

<%-- Se identifica el campo al que pertenece el error para mostrarlo junto a él. --%>
<c:choose>
    <c:when test="${fn:contains(error, 'título')}"><c:set var="campoError" value="titulo"/></c:when>
    <c:when test="${fn:contains(error, 'descripción')}"><c:set var="campoError" value="descripcion"/></c:when>
    <c:when test="${fn:contains(error, 'fecha')}"><c:set var="campoError" value="fechaLimite"/></c:when>
    <c:when test="${fn:contains(error, 'prioridad')}"><c:set var="campoError" value="prioridad"/></c:when>
</c:choose>
<%@ include file="fragmentos/header.jspf" %>

<div class="row justify-content-center">
    <div class="col-lg-8 col-xl-7">
        <nav aria-label="Ruta de navegación">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${ctx}/tareas">Tareas</a></li>
                <li class="breadcrumb-item active" aria-current="page">${titulo}</li>
            </ol>
        </nav>

        <div class="tarjeta formulario-tarea">
            <div class="encabezado-formulario">
                <span class="encabezado-icono">
                    <i class="bi ${empty tarea.id ? 'bi-plus-lg' : 'bi-pencil'}" aria-hidden="true"></i>
                </span>
                <div>
                    <h1>${titulo}</h1>
                    <p>Los campos marcados con <span class="obligatorio" aria-hidden="true">*</span> son obligatorios.</p>
                </div>
            </div>

            <c:if test="${not empty error}">
                <div id="resumen-error" class="alert alert-danger resumen-error" role="alert" tabindex="-1"
                     aria-labelledby="resumen-error-titulo">
                    <p id="resumen-error-titulo" class="fw-semibold mb-1">
                        <i class="bi bi-exclamation-triangle-fill" aria-hidden="true"></i> No se pudo guardar la tarea
                    </p>
                    <c:choose>
                        <c:when test="${not empty campoError}">
                            <a href="#${campoError}" class="alert-link"><c:out value="${error}"/></a>
                        </c:when>
                        <c:otherwise><c:out value="${error}"/></c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <form method="post" action="${ctx}/tareas" novalidate>
                <input type="hidden" name="accion" value="guardar">
                <input type="hidden" name="id" value="${tarea.id}">

                <div class="mb-4">
                    <label for="titulo" class="form-label">
                        Título <span class="obligatorio" aria-hidden="true">*</span>
                    </label>
                    <input type="text" id="titulo" name="titulo" required maxlength="100" autocomplete="off"
                           class="form-control ${campoError == 'titulo' ? 'is-invalid' : ''}"
                           aria-describedby="${campoError == 'titulo' ? 'titulo-error' : 'titulo-ayuda'}"
                           placeholder="Ej. Entregar el informe de laboratorio"
                           value="<c:out value='${tarea.titulo}'/>">
                    <c:choose>
                        <c:when test="${campoError == 'titulo'}">
                            <div id="titulo-error" class="invalid-feedback"><c:out value="${error}"/></div>
                        </c:when>
                        <c:otherwise><div id="titulo-ayuda" class="form-text">Máximo 100 caracteres.</div></c:otherwise>
                    </c:choose>
                </div>

                <div class="mb-4">
                    <label for="descripcion" class="form-label">Descripción</label>
                    <textarea id="descripcion" name="descripcion" rows="4" maxlength="500"
                              class="form-control ${campoError == 'descripcion' ? 'is-invalid' : ''}"
                              aria-describedby="${campoError == 'descripcion' ? 'descripcion-error' : 'descripcion-ayuda'}"
                              placeholder="Detalles opcionales de la tarea"><c:out value="${tarea.descripcion}"/></textarea>
                    <c:choose>
                        <c:when test="${campoError == 'descripcion'}">
                            <div id="descripcion-error" class="invalid-feedback"><c:out value="${error}"/></div>
                        </c:when>
                        <c:otherwise><div id="descripcion-ayuda" class="form-text">Opcional. Máximo 500 caracteres.</div></c:otherwise>
                    </c:choose>
                </div>

                <div class="row g-4 mb-4">
                    <div class="col-sm-6">
                        <label for="fechaLimite" class="form-label">
                            Fecha límite <span class="obligatorio" aria-hidden="true">*</span>
                        </label>
                        <%-- LocalDate.toString() produce yyyy-MM-dd, el formato del input date --%>
                        <input type="date" id="fechaLimite" name="fechaLimite" required
                               class="form-control ${campoError == 'fechaLimite' ? 'is-invalid' : ''}"
                               aria-describedby="${campoError == 'fechaLimite' ? 'fecha-error' : 'fecha-ayuda'}"
                               value="${tarea.fechaLimite}">
                        <c:choose>
                            <c:when test="${campoError == 'fechaLimite'}">
                                <div id="fecha-error" class="invalid-feedback"><c:out value="${error}"/></div>
                            </c:when>
                            <c:otherwise><div id="fecha-ayuda" class="form-text">Desde hoy en adelante.</div></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="col-sm-6">
                        <label for="prioridad" class="form-label">
                            Prioridad <span class="obligatorio" aria-hidden="true">*</span>
                        </label>
                        <select id="prioridad" name="prioridad" required
                                class="form-select ${campoError == 'prioridad' ? 'is-invalid' : ''}"
                                ${campoError == 'prioridad' ? 'aria-describedby="prioridad-error"' : ''}>
                            <option value="">Selecciona una prioridad</option>
                            <c:forEach items="${prioridades}" var="p">
                                <option value="${p}" ${tarea.prioridad == p ? 'selected' : ''}>${p.etiqueta}</option>
                            </c:forEach>
                        </select>
                        <c:if test="${campoError == 'prioridad'}">
                            <div id="prioridad-error" class="invalid-feedback"><c:out value="${error}"/></div>
                        </c:if>
                    </div>
                </div>

                <div class="acciones-formulario">
                    <a href="${ctx}/tareas" class="btn btn-outline-secondary">Cancelar</a>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check-lg" aria-hidden="true"></i> Guardar
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    // Fecha mínima = hoy (según la zona horaria del navegador), igual que la validación del servidor.
    (function () {
        var hoy = new Date();
        hoy.setMinutes(hoy.getMinutes() - hoy.getTimezoneOffset());
        document.getElementById('fechaLimite').min = hoy.toISOString().slice(0, 10);
    })();
</script>

<%@ include file="fragmentos/footer.jspf" %>
