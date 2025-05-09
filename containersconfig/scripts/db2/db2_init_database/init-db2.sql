-- Crear esquema (opcional)
CREATE SCHEMA DEMO;

-- Crear tabla de ejemplo
CREATE TABLE DEMO.CUSTOMERS (
    ID INTEGER NOT NULL PRIMARY KEY,
    NAME VARCHAR(100),
    EMAIL VARCHAR(100),
    CREATED_AT TIMESTAMP
);

-- Insertar algunos datos
INSERT INTO DEMO.CUSTOMERS (ID, NAME, EMAIL, CREATED_AT) VALUES
(1, 'Juan Pérez', 'juan@example.com', CURRENT_TIMESTAMP),
(2, 'Ana Gómez', 'ana@example.com', CURRENT_TIMESTAMP),
(3, 'Carlos Ruiz', 'carlos@example.com', CURRENT_TIMESTAMP);