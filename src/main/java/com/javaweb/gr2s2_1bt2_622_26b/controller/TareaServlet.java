package com.javaweb.gr2s2_1bt2_622_26b.controller;

import com.javaweb.gr2s2_1bt2_622_26b.model.Estado;
import com.javaweb.gr2s2_1bt2_622_26b.model.Prioridad;
import com.javaweb.gr2s2_1bt2_622_26b.model.Tarea;
import com.javaweb.gr2s2_1bt2_622_26b.service.TareaService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@WebServlet(name = "TareaServlet", urlPatterns = "/tareas")
public class TareaServlet extends HttpServlet {

    // Rutas de las vistas protegidas dentro de WEB-INF.
    private static final String FORMULARIO = "/WEB-INF/views/formulario.jsp";
    private static final String LISTA = "/WEB-INF/views/lista.jsp";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;

    private TareaService service;

    @Override
    public void init() {
        // El servicio se crea una sola vez al iniciar el servlet.
        service = new TareaService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");

        // GET se utiliza para mostrar el formulario o consultar la lista.
        switch (accion == null ? "" : accion) {
            case "nuevo":
                mostrarFormulario(request, response, new Tarea());
                break;
            case "editar":
                Long id;
                try {
                    id = convertirId(request.getParameter("id"));
                } catch (IllegalArgumentException e) {
                    // Un identificador inválido se trata como recurso inexistente.
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
                }
                Tarea tarea = service.obtener(id);
                if (tarea == null) {
                    // No se expone información adicional si la tarea no existe.
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
                }
                mostrarFormulario(request, response, tarea);
                break;
            default:
                listar(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");

        // POST ejecuta las operaciones que modifican datos.
        switch (accion == null ? "" : accion) {
            case "guardar":
                guardar(request, response);
                break;
            case "eliminar":
            case "completar":
                cambiarEstadoOEliminar(request, response, accion);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Estado estado = convertirEstadoFiltro(request.getParameter("estado"));
        Prioridad prioridad = convertirPrioridadFiltro(request.getParameter("prioridad"));

        // Se envían a la vista los datos de la lista, filtros y contadores.
        request.setAttribute("tareas", service.listar(estado, prioridad));
        request.setAttribute("estados", Estado.values());
        request.setAttribute("prioridades", Prioridad.values());
        request.setAttribute("filtroEstado", estado);
        request.setAttribute("filtroPrioridad", prioridad);
        request.setAttribute("totalPendientes", service.contar(Estado.PENDIENTE));
        request.setAttribute("totalCompletadas", service.contar(Estado.COMPLETADA));
        request.getRequestDispatcher(LISTA).forward(request, response);
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Tarea tarea = new Tarea();
        try {
            // Se conservan los datos del formulario para mostrarlos si hay un error.
            tarea.setTitulo(textoONull(request.getParameter("titulo")));
            tarea.setDescripcion(textoONull(request.getParameter("descripcion")));
            tarea.setFechaLimite(convertirFecha(request.getParameter("fechaLimite")));
            tarea.setPrioridad(convertirPrioridad(request.getParameter("prioridad")));
            tarea.setId(convertirId(request.getParameter("id")));

            // Sin id se crea una tarea; con id se actualiza la existente.
            if (tarea.getId() == null) {
                service.crear(tarea);
            } else {
                service.editar(tarea);
            }

            guardarMensaje(request, "La tarea se guardó correctamente.", "success");
            // PRG evita reenviar el formulario al actualizar la página.
            response.sendRedirect(request.getContextPath() + "/tareas");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            mostrarFormulario(request, response, tarea);
        }
    }

    private void cambiarEstadoOEliminar(HttpServletRequest request, HttpServletResponse response,
                                        String accion) throws IOException {
        try {
            Long id = convertirId(request.getParameter("id"));
            // Ambas operaciones terminan redirigiendo a la lista.
            if ("eliminar".equals(accion)) {
                service.eliminar(id);
                guardarMensaje(request, "La tarea se eliminó correctamente.", "success");
            } else {
                service.cambiarEstado(id);
                guardarMensaje(request, "El estado de la tarea se actualizó correctamente.", "success");
            }
        } catch (IllegalArgumentException e) {
            guardarMensaje(request, e.getMessage(), "danger");
        }
        response.sendRedirect(request.getContextPath() + "/tareas");
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   Tarea tarea) throws ServletException, IOException {
        // La misma vista sirve para crear y editar tareas.
        request.setAttribute("tarea", tarea);
        request.setAttribute("prioridades", Prioridad.values());
        request.getRequestDispatcher(FORMULARIO).forward(request, response);
    }

    private void guardarMensaje(HttpServletRequest request, String mensaje, String tipo) {
        // El mensaje se conserva durante la redirección mediante la sesión.
        request.getSession().setAttribute("mensaje", mensaje);
        request.getSession().setAttribute("tipoMensaje", tipo);
    }

    private String textoONull(String valor) {
        // Los campos vacíos se representan como null para el servicio.
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }

    private Long convertirId(String valor) {
        String texto = textoONull(valor);
        if (texto == null) {
            return null;
        }
        try {
            return Long.valueOf(texto);
        } catch (NumberFormatException e) {
            // Se traduce el error técnico a un mensaje entendible para el usuario.
            throw new IllegalArgumentException("El identificador de la tarea no es válido.");
        }
    }

    private LocalDate convertirFecha(String valor) {
        String texto = textoONull(valor);
        if (texto == null) {
            return null;
        }
        try {
            return LocalDate.parse(texto, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            // LocalDate solo acepta fechas con el formato indicado.
            throw new IllegalArgumentException("La fecha límite debe tener el formato yyyy-MM-dd.");
        }
    }

    private Prioridad convertirPrioridad(String valor) {
        return convertirEnum(valor, Prioridad.class, "La prioridad no es válida.");
    }

    private Estado convertirEstadoFiltro(String valor) {
        try {
            return convertirEnum(valor, Estado.class, "El estado no es válido.");
        } catch (IllegalArgumentException e) {
            // Un filtro inválido equivale a no filtrar.
            return null;
        }
    }

    private Prioridad convertirPrioridadFiltro(String valor) {
        try {
            return convertirEnum(valor, Prioridad.class, "La prioridad no es válida.");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private <E extends Enum<E>> E convertirEnum(String valor, Class<E> tipo, String mensaje) {
        String texto = textoONull(valor);
        if (texto == null) {
            return null;
        }
        try {
            return Enum.valueOf(tipo, texto.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
