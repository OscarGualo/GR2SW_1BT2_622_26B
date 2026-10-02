package com.javaweb.gr2s2_1bt2_622_26b;

import com.javaweb.gr2s2_1bt2_622_26b.model.Estado;
import com.javaweb.gr2s2_1bt2_622_26b.model.Prioridad;
import com.javaweb.gr2s2_1bt2_622_26b.model.Tarea;
import com.javaweb.gr2s2_1bt2_622_26b.service.TareaService;
import com.javaweb.gr2s2_1bt2_622_26b.util.JPAUtil;

import java.time.LocalDate;
import java.util.List;

public class PruebaServicio {

    public static void main(String[] args) {
        TareaService servicio = new TareaService();
        Tarea tareaCreada = null;

        try {
            System.out.println("=== 1) Crear tarea válida ===");
            Tarea tarea = new Tarea(
                    "   Revisar documento   ",
                    "Preparar la entrega final",
                    LocalDate.now().plusDays(3),
                    Prioridad.MEDIA
            );

            servicio.crear(tarea);
            tareaCreada = tarea;

            System.out.println("ID: " + tareaCreada.getId());
            System.out.println("Título: " + tareaCreada.getTitulo());

            System.out.println();
            System.out.println("=== 2) Validaciones que deben fallar ===");
            probarError(() -> servicio.crear(
                    new Tarea("   ", "Descripción", LocalDate.now().plusDays(2), Prioridad.ALTA)
            ));
            probarError(() -> servicio.crear(
                    new Tarea("Fecha pasada", "Descripción", LocalDate.now().minusDays(1), Prioridad.ALTA)
            ));
            probarError(() -> servicio.crear(
                    new Tarea("Sin prioridad", "Descripción", LocalDate.now().plusDays(2), null)
            ));

            System.out.println();
            System.out.println("=== 3) Cambiar el estado de la tarea creada ===");
            servicio.cambiarEstado(tareaCreada.getId());
            Tarea actual = servicio.obtener(tareaCreada.getId());
            System.out.println("Estado actual: " + actual.getEstado());

            System.out.println();
            System.out.println("=== 4) Editarla y verificar que el estado sigue siendo COMPLETADA ===");
            Tarea paraEditar = servicio.obtener(tareaCreada.getId());
            paraEditar.setTitulo("   Título editado   ");
            paraEditar.setDescripcion("Descripción actualizada");
            paraEditar.setFechaLimite(LocalDate.now().plusDays(7));
            paraEditar.setPrioridad(Prioridad.ALTA);

            servicio.editar(paraEditar);

            Tarea comprobada = servicio.obtener(tareaCreada.getId());
            System.out.println("Estado tras editar: " + comprobada.getEstado());
            System.out.println("Título tras editar: " + comprobada.getTitulo());

            System.out.println();
            System.out.println("=== 5) Probar listar con filtros y contadores ===");
            List<Tarea> porEstado = servicio.listar(Estado.COMPLETADA, null);
            List<Tarea> porPrioridad = servicio.listar(null, Prioridad.ALTA);
            List<Tarea> porAmbos = servicio.listar(Estado.COMPLETADA, Prioridad.ALTA);

            System.out.println("Filtrado por estado COMPLETADA: " + porEstado.size());
            System.out.println("Filtrado por prioridad ALTA: " + porPrioridad.size());
            System.out.println("Filtrado por estado + prioridad: " + porAmbos.size());

            System.out.println("Contador estado COMPLETADA: " + servicio.contar(Estado.COMPLETADA));
            System.out.println("Contador estado PENDIENTE: " + servicio.contar(Estado.PENDIENTE));

            System.out.println();
            System.out.println("=== 6) Mostrar la lista completa ordenada ===");
            List<Tarea> todas = servicio.listar(null, null);
            for (Tarea t : todas) {
                System.out.println(
                        "ID=" + t.getId()
                                + " | Título=" + t.getTitulo()
                                + " | Estado=" + t.getEstado()
                                + " | Prioridad=" + t.getPrioridad()
                                + " | Fecha límite=" + t.getFechaLimite()
                );
            }

            System.out.println();
            System.out.println("=== 7) Eliminar la tarea y comprobar que ya no existe ===");
            servicio.eliminar(tareaCreada.getId());
            Tarea eliminada = servicio.obtener(tareaCreada.getId());

            if (eliminada == null) {
                System.out.println("La tarea ya no existe.");
            } else {
                System.out.println("La tarea sigue existiendo: " + eliminada.getTitulo());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JPAUtil.cerrar();
            System.out.println("Finalizó la prueba y se cerró la conexión JPA.");
        }
    }

    private static void probarError(Runnable accion) {
        try {
            accion.run();
            System.out.println("✗ No lanzó IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Validación correcta: " + e.getMessage());
        }
    }
}