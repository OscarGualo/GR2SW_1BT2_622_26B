# Gestor de Tareas — GR2S2_1BT2_622_26B

Aplicación web Java (WAR) para gestionar tareas. Se despliega en Apache Tomcat 9 y guarda los datos en SQL Server mediante Hibernate (JPA).

## Funcionalidades

- **Listado de tareas** con contadores de tareas pendientes y completadas.
- **Filtros** por estado y prioridad; el filtro activo queda seleccionado y se puede limpiar.
- **Crear y editar** tareas con el mismo formulario: título, descripción, fecha límite y prioridad.
- **Completar / reabrir** una tarea. Las completadas se muestran con el texto tachado.
- **Eliminar** con confirmación previa.
- **Prioridad con colores:** ALTA en rojo, MEDIA en amarillo y BAJA en verde.
- **Etiqueta "Vencida"** para las tareas pendientes cuya fecha límite ya pasó.
- **Validaciones** en el servidor, con el mensaje de error mostrado en el formulario.
- **Mensajes flash** de éxito o error después de cada operación (patrón Post/Redirect/Get).
- **Páginas de error amigables** para los errores 404 y 500.
- **Interfaz responsive** con Bootstrap 5, accesible por teclado.

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java (JDK) | 24 | Lenguaje y compilación (`maven.compiler.release`) |
| Apache Tomcat | 9.0.122 | Servidor de aplicaciones |
| Servlet API (`javax.servlet`) | 4.0.1 | Servlets y filtros (provista por Tomcat) |
| JSP API | 2.3.3 | Vistas (provista por Tomcat) |
| JSTL | 1.2 | Etiquetas en JSP (incluida en el WAR) |
| Hibernate ORM (`jakarta.persistence`) | 6.6.58.Final | Mapeo objeto-relacional / JPA 3.1 |
| Microsoft SQL Server + `mssql-jdbc` | 13.6.0.jre11 | Base de datos y driver JDBC |
| Bootstrap + Bootstrap Icons (CDN) | 5.3.3 / 1.11.3 | Estilos e iconos de la interfaz |
| Maven (wrapper incluido) | 3.x | Construcción y dependencias |
| JUnit Jupiter | 5.14.4 | Pruebas |

> Tomcat 9 solo admite el espacio de nombres `javax.servlet`. No uses `jakarta.servlet.*` en el código.
> Por la misma razón, la URI de JSTL es `http://java.sun.com/jsp/jstl/core`. Es el equivalente en Tomcat 9 de `jakarta.tags.core`, que solo existe en JSTL 3 / Tomcat 10+.
> Hibernate 6 sí usa `jakarta.persistence.*`; es correcto porque se incluye dentro del WAR y no depende de Tomcat.

## Arquitectura

La aplicación sigue el patrón **MVC** organizado en capas:

```
Navegador ──► EncodingFilter (UTF-8)
                 │
                 ▼
          TareaServlet  (/tareas)          Controlador: GET muestra la lista o el formulario, POST guarda/completa/elimina
                 │
                 ▼
          TareaService                      Reglas de negocio y validaciones
                 │
                 ▼
          TareaDAO                          Acceso a datos con EntityManager (persist, merge, find, remove, JPQL)
                 │
                 ▼
          Hibernate / JPA ──► SQL Server   Tabla "tareas" mapeada desde la entidad Tarea
                 │
                 ▼
          JSP + JSTL (WEB-INF/views)        Vistas: lista.jsp, formulario.jsp, error.jsp
```

- `JPAListener` crea el `EntityManagerFactory` al iniciar la aplicación y lo cierra al detenerla. `JPAUtil` lo pone a disposición del resto del código.
- Las vistas están dentro de `WEB-INF/views/`, así que solo se puede llegar a ellas a través del servlet.
- `index.jsp` redirige a `/tareas` con `<c:redirect>`.

## Requisitos previos

- JDK 24 instalado y `JAVA_HOME` apuntando a él.
- Apache Tomcat 9.0.x.
- SQL Server (Express o Developer) y SQL Server Management Studio (SSMS) o Azure Data Studio.
- IntelliJ IDEA (recomendado) o cualquier IDE con soporte Maven.

## Instalación de la base de datos

1. **Habilitar TCP/IP en SQL Server**
   - Abre *SQL Server Configuration Manager* → *Configuración de red de SQL Server* → *Protocolos de MSSQLSERVER* (o `SQLEXPRESS`).
   - Habilita **TCP/IP** y, en *Direcciones IP → IPAll*, establece **Puerto TCP = 1433**.
   - Reinicia el servicio de SQL Server.

2. **Habilitar autenticación mixta** (SQL Server y Windows)
   - En SSMS: clic derecho sobre el servidor → *Propiedades* → *Seguridad* → *Modo de autenticación de SQL Server y Windows*.
   - Reinicia el servicio.

3. **Ejecutar el script** [`database/script.sql`](database/script.sql) en SSMS. El script:
   - crea la base de datos `gestion_tareas`;
   - crea el login y usuario `tareas_user`;
   - crea la tabla `tareas`;
   - inserta 10 tareas de ejemplo con fechas relativas a hoy, incluidas algunas vencidas y algunas completadas.

4. **Datos de conexión** que usa la aplicación (configurados en `src/main/resources/META-INF/persistence.xml`):

   ```
   URL:      jdbc:sqlserver://localhost:1433;databaseName=gestion_tareas;encrypt=true;trustServerCertificate=true
   Usuario:  tareas_user
   Clave:    tareas123
   ```

   Son credenciales de desarrollo local. Si las cambias en el script, cámbialas también en `persistence.xml`.

## Cómo ejecutar

### Opción A: IntelliJ IDEA (recomendada)

1. Clona el repositorio y ábrelo en IntelliJ:
   ```
   git clone https://github.com/OscarGualo/GR2SW_1BT2_622_26B.git
   ```
2. En la ventana **Maven**, pulsa **Reload All Maven Projects**.
3. **Run → Edit Configurations… → + → Tomcat Server → Local**:
   - **Server**: selecciona tu Tomcat 9 en *Application server* y tu **JDK 24** en *JRE*.
   - **Deployment**: **+ → Artifact… →** `…:war exploded`; *Application context*: `/tareas`.
4. Pulsa **▶ Run** y abre <http://localhost:8080/tareas/>; serás redirigido a la lista de tareas.

### Opción B: línea de comandos

1. Compila y genera el WAR:
   ```
   mvnw.cmd clean package        # Windows (PowerShell / CMD)
   ./mvnw clean package          # Git Bash / Linux / macOS
   ```
   Se genera `target/tareas.war`.
2. Copia `target/tareas.war` a la carpeta `webapps/` de Tomcat.
3. Inicia Tomcat con `bin\startup.bat` (Windows) o `bin/startup.sh`.
4. Abre <http://localhost:8080/tareas/>.

### Pruebas

```
./mvnw test
./mvnw test -Dtest=NombreDeClase
```

Las pruebas usan la base de datos real, así que SQL Server debe estar en ejecución con el script ya aplicado. Para compilar sin ejecutar las pruebas usa `./mvnw clean package -DskipTests`.

## Rutas de la aplicación

| Método | URL | Acción |
|---|---|---|
| GET | `/tareas` | Lista de tareas (acepta `?estado=` y `?prioridad=`) |
| GET | `/tareas?accion=nuevo` | Formulario para crear una tarea |
| GET | `/tareas?accion=editar&id=N` | Formulario para editar una tarea (404 si no existe) |
| POST | `/tareas` con `accion=guardar` | Crea o actualiza una tarea |
| POST | `/tareas` con `accion=completar` | Alterna el estado entre pendiente y completada |
| POST | `/tareas` con `accion=eliminar` | Elimina una tarea |

## Estructura del proyecto

```
src/main/java/com/javaweb/gr2s2_1bt2_622_26b/
├── controller/TareaServlet.java      Controlador (@WebServlet "/tareas")
├── service/TareaService.java         Lógica de negocio y validaciones
├── dao/TareaDAO.java                 Acceso a datos con JPA
├── model/                            Entidad Tarea y enums Estado, Prioridad
├── filter/EncodingFilter.java        Filtro UTF-8 (@WebFilter "/*")
└── util/                             JPAUtil y JPAListener (ciclo de vida del EntityManagerFactory)
src/main/resources/META-INF/persistence.xml   Unidad de persistencia "tareasPU"
src/main/webapp/
├── index.jsp                         Redirige a /tareas
├── css/estilos.css                   Estilos propios
└── WEB-INF/
    ├── web.xml                       Páginas de error 404 / 500
    └── views/
        ├── lista.jsp                 Lista, contadores y filtros
        ├── formulario.jsp            Crear / editar tarea
        ├── error.jsp                 Página de error amigable
        └── fragmentos/               header.jspf y footer.jspf
database/script.sql                   Base de datos, usuario, tabla y datos de ejemplo
pom.xml                               Dependencias y build (WAR: tareas)
```
