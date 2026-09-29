# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Java web application (WAR packaging) generated from the IntelliJ web application template. It currently contains only the starter `HelloServlet` and `index.jsp`.

- Java 24 (`maven.compiler.release` in `pom.xml`)
- Target container: **Tomcat 9.0.122** (installed at `D:\apache-tomcat-9.0.122-windows-x64\apache-tomcat-9.0.122`, run via the IntelliJ "Tomcat 9.0.122" run configuration)
- Servlet 4.0 / JSP 2.3 (`provided`) and JSTL 1.2 — all in the **`javax.*`** namespace, because Tomcat 9 does not support `jakarta.servlet`. Never import `jakarta.servlet.*`; JSTL taglib URIs are `http://java.sun.com/jsp/jstl/...`.
- Hibernate 6.6 uses `jakarta.persistence.*` — that is correct: it is bundled in the WAR and independent of Tomcat's servlet namespace.
- SQL Server via `com.microsoft.sqlserver:mssql-jdbc` (`.jre11` build)
- JUnit Jupiter 5 for tests (no `src/test` directory exists yet)

## Commands

Use the Maven wrapper (`mvnw.cmd` on Windows/PowerShell, `./mvnw` in Git Bash):

```
./mvnw clean package                      # compile, test, build target/GR2S2_1BT2_622_26B.war
./mvnw test                               # run all tests
./mvnw test -Dtest=ClassName              # run a single test class
./mvnw test -Dtest=ClassName#methodName   # run a single test method
```

## Running

There is no embedded server or server plugin in `pom.xml`. The app is deployed to the external Tomcat 9 through the IntelliJ run configuration (exploded WAR, context path `/GR2S2_1BT2_622_26B_war_exploded`).

## Architecture

- Servlets live in `src/main/java/com/javaweb/gr2s2_1bt2_622_26b/` and are registered with the `@WebServlet` annotation (e.g. `HelloServlet` → `/hello-servlet`), not in `web.xml`.
- `src/main/webapp/WEB-INF/web.xml` is an empty Java EE `web-app` 4.0 descriptor; add entries there only for configuration annotations can't express.
- JSPs and static resources go under `src/main/webapp/`; `index.jsp` is the welcome page and links to the servlet with a relative URL.
