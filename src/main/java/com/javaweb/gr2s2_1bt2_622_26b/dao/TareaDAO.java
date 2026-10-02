package com.javaweb.gr2s2_1bt2_622_26b.dao;

import com.javaweb.gr2s2_1bt2_622_26b.model.Estado;
import com.javaweb.gr2s2_1bt2_622_26b.model.Prioridad;
import com.javaweb.gr2s2_1bt2_622_26b.model.Tarea;
import com.javaweb.gr2s2_1bt2_622_26b.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class TareaDAO {

    public void guardar(Tarea tarea) {
        ejecutarEnTransaccion(em -> em.persist(tarea));
    }

    public void actualizar(Tarea tarea) {
        ejecutarEnTransaccion(em -> em.merge(tarea));
    }

    public void eliminar(Long id) {
        ejecutarEnTransaccion(em -> {
            Tarea tarea = em.find(Tarea.class, id);
            if (tarea != null) {
                em.remove(tarea);
            }
        });
    }

    public Tarea buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Tarea.class, id);
        } finally {
            em.close();
        }
    }

    public List<Tarea> listar(Estado estado, Prioridad prioridad) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT t FROM Tarea t WHERE 1 = 1");
            if (estado != null) {
                jpql.append(" AND t.estado = :estado");
            }
            if (prioridad != null) {
                jpql.append(" AND t.prioridad = :prioridad");
            }

            var query = em.createQuery(jpql.toString(), Tarea.class);
            if (estado != null) {
                query.setParameter("estado", estado);
            }
            if (prioridad != null) {
                query.setParameter("prioridad", prioridad);
            }

            List<Tarea> tareas = new ArrayList<>(query.getResultList());
            // Los enums siguen su orden natural; las fechas nulas quedan al final.
            tareas.sort(Comparator.comparing(Tarea::getEstado)
                    .thenComparing(Tarea::getPrioridad)
                    .thenComparing(Tarea::getFechaLimite,
                            Comparator.nullsLast(Comparator.naturalOrder())));
            return tareas;
        } finally {
            em.close();
        }
    }

    public long contarPorEstado(Estado estado) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COUNT(t) FROM Tarea t WHERE t.estado = :estado",
                            Long.class)
                    .setParameter("estado", estado)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    // Ejecuta las escrituras en una transacción y cierra el contexto.
    private void ejecutarEnTransaccion(Consumer<EntityManager> operacion) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaccion = em.getTransaction();

        try {
            transaccion.begin();
            operacion.accept(em);
            transaccion.commit();
        } catch (RuntimeException e) {
            if (transaccion.isActive()) {
                try {
                    transaccion.rollback();
                } catch (RuntimeException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
            }
            throw e;
        } finally {
            em.close();
        }
    }
}