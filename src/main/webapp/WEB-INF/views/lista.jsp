<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Tomcat 9 usa JSTL 1.2 (javax): equivale a la URI jakarta.tags.core de JSTL 3. --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="titulo" value="Mis tareas"/>
<c:set var="seccion" value="lista"/>
<%@ include file="fragmentos/header.jspf" %>

<div class="encabezado-pagina">
    <div>
        <h1>Mis tareas</h1>
        <p>Organiza tus pendientes y revisa lo que ya completaste.</p>
    </div>
    <a class="btn btn-primary" href="${ctx}/tareas?accion=nuevo">
        <i class="bi bi-plus-lg" aria-hidden="true"></i> Nueva tarea
    </a>
</div>

<%-- Contadores --%>
<section class="row g-3 mb-4" aria-label="Resumen de tareas">
    <div class="col-6">
        <div class="tarjeta contador contador-pendientes">
            <span class="contador-icono"><i class="bi bi-hourglass-split" aria-hidden="true"></i></span>
            <div>
                <p class="contador-numero">${totalPendientes}</p>
                <p class="contador-etiqueta">Pendientes</p>
            </div>
        </div>
    </div>
    <div class="col-6">
        <div class="tarjeta contador contador-completadas">
            <span class="contador-icono"><i class="bi bi-check2-circle" aria-hidden="true"></i></span>
            <div>
                <p class="contador-numero">${totalCompletadas}</p>
                <p class="contador-etiqueta">Completadas</p>
            </div>
        </div>
    </div>
</section>

<%-- Barra de filtros: conserva seleccionado el filtro activo --%>
<form method="get" action="${ctx}/tareas" class="tarjeta filtros mb-4" role="search" aria-label="Filtrar tareas">
    <div class="row g-3 align-items-end">
        <div class="col-sm-6 col-lg-4">
            <label for="estado" class="form-label">Estado</label>
            <select id="estado" name="estado" class="form-select">
                <option value="">Todos</option>
                <c:forEach items="${estados}" var="e">
                    <option value="${e}" ${filtroEstado == e ? 'selected' : ''}>${e.etiqueta}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-sm-6 col-lg-4">
            <label for="prioridad" class="form-label">Prioridad</label>
            <select id="prioridad" name="prioridad" class="form-select">
                <option value="">Todas</option>
                <c:forEach items="${prioridades}" var="p">
                    <option value="${p}" ${filtroPrioridad == p ? 'selected' : ''}>${p.etiqueta}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-lg-4 d-flex gap-2">
            <button type="submit" class="btn btn-outline-primary flex-fill">
                <i class="bi bi-funnel" aria-hidden="true"></i> Filtrar
            </button>
            <a href="${ctx}/tareas" class="btn btn-link flex-fill">
                <i class="bi bi-x-circle" aria-hidden="true"></i> Limpiar
            </a>
        </div>
    </div>
    <c:if test="${not empty filtroEstado or not empty filtroPrioridad}">
        <p class="filtros-activos">
            <i class="bi bi-info-circle" aria-hidden="true"></i>
            Mostrando
            <c:out value="${empty filtroEstado ? 'todos los estados' : filtroEstado.etiqueta}"/>
            ·
            <c:out value="${empty filtroPrioridad ? 'todas las prioridades' : 'prioridad '.concat(filtroPrioridad.etiqueta)}"/>
        </p>
    </c:if>
</form>

<%-- Lista vacía --%>
<c:if test="${empty tareas}">
    <div class="tarjeta estado-vacio">
        <span class="estado-vacio-icono"><i class="bi bi-clipboard-check" aria-hidden="true"></i></span>
        <h2>No hay tareas</h2>
        <p>No se encontraron tareas con los filtros actuales. Crea una nueva o limpia los filtros.</p>
        <div class="d-flex flex-wrap justify-content-center gap-2">
            <a class="btn btn-primary" href="${ctx}/tareas?accion=nuevo">
                <i class="bi bi-plus-lg" aria-hidden="true"></i> Crear una tarea
            </a>
            <c:if test="${not empty filtroEstado or not empty filtroPrioridad}">
                <a class="btn btn-outline-secondary" href="${ctx}/tareas">Limpiar filtros</a>
            </c:if>
        </div>
    </div>
</c:if>

<%-- Tabla de tareas (en móvil cada fila se muestra como tarjeta) --%>
<c:if test="${not empty tareas}">
    <div class="tarjeta tabla-tareas">
        <table class="table align-middle mb-0">
            <caption class="visually-hidden">Lista de tareas</caption>
            <thead>
            <tr>
                <th scope="col">Título</th>
                <th scope="col">Descripción</th>
                <th scope="col">Fecha límite</th>
                <th scope="col">Prioridad</th>
                <th scope="col">Estado</th>
                <th scope="col" class="text-lg-end">Acciones</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${tareas}" var="t">
                <c:set var="completada" value="${t.estado == 'COMPLETADA'}"/>
                <tr class="${completada ? 'tarea-completada' : ''}">
                    <td class="texto titulo" data-label="Título"><c:out value="${t.titulo}"/></td>
                    <td class="texto descripcion" data-label="Descripción">
                        <c:out value="${t.descripcion}" default="—"/>
                    </td>
                    <td class="fecha" data-label="Fecha límite">
                        <c:choose>
                            <c:when test="${empty t.fechaLimite}"><span class="text-muted">—</span></c:when>
                            <c:otherwise>
                                <i class="bi bi-calendar3" aria-hidden="true"></i> ${t.fechaLimite}
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${t.vencida}">
                            <span class="etiqueta etiqueta-vencida">
                                <i class="bi bi-exclamation-triangle-fill" aria-hidden="true"></i> Vencida
                            </span>
                        </c:if>
                    </td>
                    <td data-label="Prioridad">
                        <c:choose>
                            <c:when test="${t.prioridad == 'ALTA'}"><c:set var="clasePrioridad" value="prioridad-alta"/></c:when>
                            <c:when test="${t.prioridad == 'MEDIA'}"><c:set var="clasePrioridad" value="prioridad-media"/></c:when>
                            <c:otherwise><c:set var="clasePrioridad" value="prioridad-baja"/></c:otherwise>
                        </c:choose>
                        <span class="etiqueta ${clasePrioridad}">
                            <span class="punto" aria-hidden="true"></span>${t.prioridad.etiqueta}
                        </span>
                    </td>
                    <td data-label="Estado">
                        <span class="etiqueta ${completada ? 'estado-completada' : 'estado-pendiente'}">
                            <i class="bi ${completada ? 'bi-check2' : 'bi-clock'}" aria-hidden="true"></i>
                            ${t.estado.etiqueta}
                        </span>
                    </td>
                    <td class="acciones" data-label="Acciones">
                        <a class="btn btn-accion" href="${ctx}/tareas?accion=editar&amp;id=${t.id}"
                           aria-label="Editar: ${fn:escapeXml(t.titulo)}">
                            <i class="bi bi-pencil" aria-hidden="true"></i><span>Editar</span>
                        </a>
                        <form method="post" action="${ctx}/tareas">
                            <input type="hidden" name="accion" value="completar">
                            <input type="hidden" name="id" value="${t.id}">
                            <c:choose>
                                <c:when test="${completada}">
                                    <button type="submit" class="btn btn-accion" aria-label="Reabrir: ${fn:escapeXml(t.titulo)}">
                                        <i class="bi bi-arrow-counterclockwise" aria-hidden="true"></i><span>Reabrir</span>
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button type="submit" class="btn btn-accion btn-accion-exito"
                                            aria-label="Completar: ${fn:escapeXml(t.titulo)}">
                                        <i class="bi bi-check-lg" aria-hidden="true"></i><span>Completar</span>
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </form>
                        <form method="post" action="${ctx}/tareas" onsubmit="return confirm('¿Eliminar tarea?')">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="id" value="${t.id}">
                            <button type="submit" class="btn btn-accion btn-accion-peligro"
                                    aria-label="Eliminar: ${fn:escapeXml(t.titulo)}">
                                <i class="bi bi-trash3" aria-hidden="true"></i><span>Eliminar</span>
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<%@ include file="fragmentos/footer.jspf" %>
