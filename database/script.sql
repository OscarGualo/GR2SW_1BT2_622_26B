-- 1. Base de datos
IF DB_ID('gestion_tareas') IS NULL
    CREATE DATABASE gestion_tareas;
GO

-- 2. Login y usuario exclusivo de la aplicación
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'tareas_user')
    CREATE LOGIN tareas_user WITH PASSWORD = 'tareas123', CHECK_POLICY = OFF;
GO

USE gestion_tareas;
GO

IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'tareas_user')
BEGIN
    CREATE USER tareas_user FOR LOGIN tareas_user;
    ALTER ROLE db_owner ADD MEMBER tareas_user;
END
GO

-- 3. Tabla (los nombres coinciden con la entidad Tarea)
IF OBJECT_ID('dbo.tareas', 'U') IS NULL
CREATE TABLE tareas (
                        id             BIGINT IDENTITY(1,1) PRIMARY KEY,
                        titulo         NVARCHAR(100) NOT NULL,
                        descripcion    NVARCHAR(500),
                        fecha_limite   DATE,
                        prioridad      VARCHAR(10)   NOT NULL,
                        estado         VARCHAR(15)   NOT NULL,
                        fecha_creacion DATETIME2     NOT NULL
);
GO

-- 4. Datos de ejemplo (fechas relativas a hoy)
DECLARE @hoy DATE = CAST(GETDATE() AS DATE);

INSERT INTO tareas (titulo, descripcion, fecha_limite, prioridad, estado, fecha_creacion) VALUES
                                                                                              (N'Preparar exposición de Ágiles', N'Diapositivas sobre Scrum', DATEADD(DAY, 3, @hoy), 'ALTA', 'PENDIENTE', SYSDATETIME()),
                                                                                              (N'Revisar Pull Requests', N'Revisar PR del equipo', DATEADD(DAY, 1, @hoy), 'MEDIA', 'PENDIENTE', SYSDATETIME()),
                                                                                              (N'Entregar informe de laboratorio', N'Informe de redes', DATEADD(DAY, -2, @hoy), 'ALTA', 'PENDIENTE', SYSDATETIME()),
                                                                                              (N'Comprar materiales', N'Cuadernos y marcadores', DATEADD(DAY, 7, @hoy), 'BAJA', 'COMPLETADA', SYSDATETIME()),
                                                                                              (N'Definir requisitos', N'Recopilar y documentar los requisitos del proyecto', DATEADD(DAY, 2, @hoy), 'ALTA', 'COMPLETADA', SYSDATETIME()),
                                                                                              (N'Diseñar la base de datos', N'Definir las tablas y relaciones principales', DATEADD(DAY, 4, @hoy), 'ALTA', 'PENDIENTE', SYSDATETIME()),
                                                                                              (N'Crear prototipo', N'Preparar un prototipo inicial de la interfaz', DATEADD(DAY, 5, @hoy), 'MEDIA', 'PENDIENTE', SYSDATETIME()),
                                                                                              (N'Escribir pruebas', N'Crear pruebas para las funcionalidades principales', DATEADD(DAY, 8, @hoy), 'MEDIA', 'PENDIENTE', SYSDATETIME()),
                                                                                              (N'Actualizar dependencias', N'Revisar y actualizar las dependencias del pom', DATEADD(DAY, -1, @hoy), 'BAJA', 'COMPLETADA', SYSDATETIME()),
                                                                                              (N'Leer documentación de JPA', NULL, NULL, 'BAJA', 'PENDIENTE', SYSDATETIME());
GO

SELECT * FROM tareas;