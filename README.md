# Tareas — GR2S2_1BT2_622_26B

Aplicación web Java (WAR) para la gestión de tareas, desplegada en Apache Tomcat 9 y con persistencia en SQL Server mediante Hibernate.

> Estado actual: proyecto base. Contiene el servlet de ejemplo `HelloServlet` (`/hello-servlet`) y la página de inicio `index.jsp`.

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java (JDK) | 24 | Lenguaje y compilación (`maven.compiler.release`) |
| Apache Tomcat | 9.0.122 | Servidor de aplicaciones |
| Servlet API (`javax.servlet`) | 4.0.1 | Servlets (provista por Tomcat) |
| JSP API | 2.3.3 | Vistas (provista por Tomcat) |
| JSTL | 1.2 | Etiquetas en JSP (incluida en el WAR) |
| Hibernate ORM (`jakarta.persistence`) | 6.6.58.Final | Mapeo objeto-relacional / JPA 3.1 |
| Microsoft SQL Server + `mssql-jdbc` | 13.6.0.jre11 | Base de datos y driver JDBC |
| Maven (wrapper incluido) | 3.x | Construcción y dependencias |
| JUnit Jupiter | 5.14.4 | Pruebas unitarias |

> Tomcat 9 solo admite el espacio de nombres `javax.servlet`. No uses `jakarta.servlet.*` en el código.

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

3. **Crear la base de datos y el usuario** (ejecutar en SSMS):

   ```sql
   CREATE DATABASE tareas;
   GO

   CREATE LOGIN tareas_user WITH PASSWORD = 'CambiaEstaClave123!';
   GO

   USE tareas;
   CREATE USER tareas_user FOR LOGIN tareas_user;
   ALTER ROLE db_owner ADD MEMBER tareas_user;
   GO
   ```

4. **Datos de conexión** que usará la aplicación:

   ```
   URL:      jdbc:sqlserver://localhost:1433;databaseName=tareas;encrypt=true;trustServerCertificate=true
   Usuario:  tareas_user
   Clave:    CambiaEstaClave123!
   ```

   Cambia la clave por una propia y no subas credenciales reales al repositorio.

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
4. Pulsa **▶ Run** y abre <http://localhost:8080/tareas/>.

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

## Estructura del proyecto

```
src/main/java/com/javaweb/gr2s2_1bt2_622_26b/   Servlets (@WebServlet)
src/main/webapp/                                JSP y recursos estáticos
src/main/webapp/WEB-INF/web.xml                 Descriptor Java EE 4.0
pom.xml                                         Dependencias y build (WAR: tareas)
```
