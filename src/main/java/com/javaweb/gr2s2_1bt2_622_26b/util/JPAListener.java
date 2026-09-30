package com.javaweb.gr2s2_1bt2_622_26b.util;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class JPAListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Fuerza la conexión al arrancar Tomcat: si la BD está mal configurada,
        // el error aparece de inmediato y no al primer clic del usuario
        JPAUtil.getEntityManager().close();
        System.out.println("JPA inicializado correctamente");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Libera las conexiones al detener o redesplegar la aplicación
        JPAUtil.cerrar();
    }
}