<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Tomcat 9 usa JSTL 1.2 (javax): equivale a la URI jakarta.tags.core de JSTL 3. --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- La página de inicio redirige directamente a la lista de tareas. --%>
<c:redirect url="/tareas"/>
