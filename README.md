## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java (JDK) | 24 | Lenguaje y compilación (`maven.compiler.release`) |
| Apache Tomcat | 9.0.122 | Servidor de aplicaciones |
| JSP API | 2.3.3 | Vistas (provista por Tomcat) |
| JSTL | 1.2 | Etiquetas en JSP (incluida en el WAR) |
| Hibernate ORM (`jakarta.persistence`) | 6.6.58.Final | Mapeo objeto-relacional / JPA 3.1 |
| Microsoft SQL Server + `mssql-jdbc` | 13.6.0.jre11 | Base de datos y driver JDBC |
| Maven (wrapper incluido) | 3.x | Construcción y dependencias |

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



   ```
   Usuario:  tareas_user
   ```


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
pom.xml                                         Dependencias y build (WAR: tareas)
```
