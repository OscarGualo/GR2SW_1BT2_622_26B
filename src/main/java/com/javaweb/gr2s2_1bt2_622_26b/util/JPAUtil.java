package com.javaweb.gr2s2_1bt2_622_26b.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {

    // Se crea una sola vez, cuando la clase se carga
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("tareasPU");

    private JPAUtil() {
        // Evita que alguien haga new JPAUtil()
    }

    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public static void cerrar() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}