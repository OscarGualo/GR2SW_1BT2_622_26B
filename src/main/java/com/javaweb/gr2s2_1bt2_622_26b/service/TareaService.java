package com.javaweb.gr2s2_1bt2_622_26b.service;

import com.javaweb.gr2s2_1bt2_622_26b.dao.TareaDAO;
import com.javaweb.gr2s2_1bt2_622_26b.model.Estado;
import com.javaweb.gr2s2_1bt2_622_26b.model.Prioridad;
import com.javaweb.gr2s2_1bt2_622_26b.model.Tarea;

import java.time.LocalDate;
import java.util.List;

public class TareaService {

    private final TareaDAO dao = new TareaDAO();

    public void crear(Tarea tarea) {
        validar(tarea);
        normalizar(tarea);
        tarea.setId(null);
        tarea.setEstado(Estado.PENDIENTE);
        dao.guardar(tarea);
    }

    public void editar(Tarea datos) {
        Tarea existente = obtenerExistente(datos == null ? null : datos.getId());
        validar(datos);
        normalizar(datos);

        existente.setTitulo(datos.getTitulo());
        existente.setDescripcion(datos.getDescripcion());
        existente.setFechaLimite(datos.getFechaLimite());
        existente.setPrioridad(datos.getPrioridad());

        dao.actualizar(existente);
    }

    public void eliminar(Long id) {
        obtenerExistente(id);
        dao.eliminar(id);
    }

    public void cambiarEstado(Long id) {
        Tarea existente = obtenerExistente(id);
        existente.setEstado(existente.getEstado() == Estado.PENDIENTE
                ? Estado.COMPLETADA
                : Estado.PENDIENTE);
        dao.actualizar(existente);
    }

    public Tarea obtener(Long id) {
        return id == null ? null : dao.buscarPorId(id);
    }

    public List<Tarea> listar(Estado estado, Prioridad prioridad) {
        return dao.listar(estado, prioridad);
    }

    public long contar(Estado estado) {
        return dao.contarPorEstado(estado);
    }

    private Tarea obtenerExistente(Long id) {
        Tarea tarea = id == null ? null : dao.buscarPorId(id);
        if (tarea == null) {
            throw new IllegalArgumentException("La tarea solicitada no existe.");
        }
        return tarea;
    }

    private void validar(Tarea t) {
        if (t == null) {
            throw new IllegalArgumentException("Los datos de la tarea son obligatorios.");
        }

        String titulo = t.getTitulo();
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (titulo.trim().length() > 100) {
            throw new IllegalArgumentException("El título no puede superar los 100 caracteres.");
        }

        String descripcion = t.getDescripcion();
        if (descripcion != null && descripcion.trim().length() > 500) {
            throw new IllegalArgumentException("La descripción no puede superar los 500 caracteres.");
        }

        if (t.getPrioridad() == null) {
            throw new IllegalArgumentException("La prioridad es obligatoria.");
        }
        if (t.getFechaLimite() == null) {
            throw new IllegalArgumentException("La fecha límite es obligatoria.");
        }
        if (t.getFechaLimite().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha límite no puede ser anterior a hoy.");
        }
    }

    private void normalizar(Tarea t) {
        t.setTitulo(t.getTitulo().trim());

        String descripcion = t.getDescripcion();
        if (descripcion != null) {
            descripcion = descripcion.trim();
            t.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        }
    }
}