
import com.javaweb.gr2s2_1bt2_622_26b.model.Prioridad;
import com.javaweb.gr2s2_1bt2_622_26b.model.Tarea;
import com.javaweb.gr2s2_1bt2_622_26b.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class PruebaJPA {

    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // INSERT: guardamos un objeto, Hibernate genera el SQL
            em.getTransaction().begin();
            Tarea nueva = new Tarea("Prueba desde JPA", "Creada con Hibernate",
                    LocalDate.now().plusDays(3), Prioridad.MEDIA);
            em.persist(nueva);
            em.getTransaction().commit();
            System.out.println(">>> Guardada con id: " + nueva.getId());

            // SELECT con JPQL: se consulta la ENTIDAD (Tarea), no la tabla
            List<Tarea> tareas = em.createQuery("SELECT t FROM Tarea t", Tarea.class)
                    .getResultList();
            for (Tarea t : tareas) {
                System.out.println(t.getId() + " | " + t.getTitulo() + " | "
                        + t.getPrioridad() + " | " + t.getEstado()
                        + (t.isVencida() ? " | VENCIDA" : ""));
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            JPAUtil.cerrar();
        }
    }
}