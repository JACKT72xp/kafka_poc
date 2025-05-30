-- Tabla de expedientes
CREATE TABLE expedientes (
 id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 fecha_contratacion DATE NOT NULL,
 titular VARCHAR(255) NOT NULL,
 numero_expediente VARCHAR(50) NOT NULL UNIQUE,
 CHGMARKER TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de fondos
CREATE TABLE fondos (
 id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 nombre VARCHAR(255) NOT NULL,
 tipo VARCHAR(50) NOT NULL,
 fecha_creacion DATE NOT NULL,
 volumen_activos DECIMAL(20, 2) NOT NULL,
 CHGMARKER TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de órdenes
CREATE TABLE ordenes (
 id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 id_expediente INTEGER NOT NULL,
 fecha_orden DATE NOT NULL,
 estado VARCHAR(50) NOT NULL,
 tipo_orden VARCHAR(50) CHECK (tipo_orden IN ('compra', 'venta')) NOT NULL,
 fecha_ejecucion DATE NOT NULL,
 importe DECIMAL(20, 2) NOT NULL,
 id_fondo INTEGER NOT NULL,
 CHGMARKER TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (id_expediente) REFERENCES expedientes(id) ON DELETE CASCADE,
 FOREIGN KEY (id_fondo) REFERENCES fondos(id) ON DELETE CASCADE
);

-- Tabla de traspasos
CREATE TABLE traspasos (
 id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 id_expediente INTEGER NOT NULL,
 id_fondo_origen INTEGER NOT NULL,
 id_fondo_destino INTEGER NOT NULL,
 fecha DATE NOT NULL,
 importe DECIMAL(20, 2) NOT NULL,
 CHGMARKER TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (id_expediente) REFERENCES expedientes(id) ON DELETE CASCADE,
 FOREIGN KEY (id_fondo_origen) REFERENCES fondos(id) ON DELETE CASCADE,
 FOREIGN KEY (id_fondo_destino) REFERENCES fondos(id) ON DELETE CASCADE
);

-- Tabla de retenciones
CREATE TABLE retenciones (
 id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 id_orden INTEGER NOT NULL,
 fecha_retencion DATE NOT NULL,
 tipo VARCHAR(50) NOT NULL,
 importe_retencion DECIMAL(20, 2) NOT NULL,
 CHGMARKER TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (id_orden) REFERENCES ordenes(id) ON DELETE CASCADE
);


-- Crear tabla de contratos
CREATE TABLE contratos (
 id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 fecha_inicio_contrato DATE NOT NULL,
 fecha_vencimiento_contrato DATE NOT NULL,
 id_parte_a VARCHAR(6) NOT NULL,
 condiciones_contrato_parte_a VARCHAR(50) NOT NULL,
 terminos_pago_parte_a VARCHAR(50) NOT NULL,
 id_parte_b VARCHAR(6) NOT NULL,
 condiciones_contrato_parte_b VARCHAR(50) NOT NULL,
 terminos_pago_parte_b VARCHAR(50) NOT NULL,
 CHGMARKER TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- Crear tabla de borrados
CREATE TABLE borrados (
 id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 pk_id INTEGER NOT NULL,
 table_name VARCHAR(50) NOT NULL,
 CHGMARKER TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insertar algunos valores de ejemplo
-- Insertar un expediente
INSERT INTO expedientes (fecha_contratacion, titular, numero_expediente)
VALUES ('2025-01-01', 'Juan Perez', 'EXP-001');

-- Insertar algunos fondos
INSERT INTO fondos (nombre, tipo, fecha_creacion, volumen_activos)
VALUES
('Fondo A', 'Equity', '2020-01-01', 5000000.00),
('Fondo B', 'Bond', '2019-01-01', 3000000.00);

-- Insertar una orden de compra
INSERT INTO ordenes (id_expediente, fecha_orden, estado, tipo_orden, fecha_ejecucion, importe, id_fondo)
VALUES (1, '2025-02-01', 'Pendiente', 'compra', '2025-02-10', 100000.00, 1);

-- Insertar un traspaso
INSERT INTO traspasos (id_expediente, id_fondo_origen, id_fondo_destino, fecha, importe)
VALUES (1, 1, 2, '2025-02-15', 50000.00);

-- Insertar una retención
INSERT INTO retenciones (id_orden, fecha_retencion, tipo, importe_retencion)
VALUES (1, '2025-03-01', 'Impuesto', 2000.00);

-- Insertar contrato
insert into contratos (fecha_inicio_contrato, fecha_vencimiento_contrato, id_parte_a, condiciones_contrato_parte_a, terminos_pago_parte_a, id_parte_b, condiciones_contrato_parte_b, terminos_pago_parte_b)
values ('2020-01-01', '2024-01-01', '10', 'CONDICIONES CONTRATO 10, 20 PARTE A', 'TERMINOS PAGO 10, 20 PARTE A', '20', 'CONDICIONES CONTRATO 10, 20 PARTE B', 'TERMINOS PAGO 10, 20 PARTE B');




-- Crear triggers
CREATE or REPLACE TRIGGER delete_contratos AFTER DELETE ON contratos
REFERENCING OLD AS old_row
FOR EACH ROW
INSERT INTO borrados (pk_id, table_name) VALUES (old_row.id, 'contratos');

CREATE or REPLACE TRIGGER delete_expedientes AFTER DELETE ON expedientes
REFERENCING OLD AS old_row
FOR EACH ROW
INSERT INTO borrados (pk_id, table_name) VALUES (old_row.id, 'expedientes');

CREATE or REPLACE TRIGGER delete_fondos AFTER DELETE ON fondos
REFERENCING OLD AS old_row
FOR EACH ROW
INSERT INTO borrados (pk_id, table_name) VALUES (old_row.id, 'fondos');



-- Crear vistas
CREATE OR REPLACE VIEW expedientes_borrados AS
SELECT pk_id AS id, CURRENT_TIMESTAMP AS fecha_contratacion, 'TITULAR' AS titular, 'EXP-000' AS numero_expediente, CHGMARKER
FROM borrados
WHERE table_name = 'expedientes';

CREATE OR REPLACE VIEW fondos_borrados AS
SELECT pk_id AS id, 'NOMBRE' AS nombre, 'TIPO' AS tipo, CURRENT_TIMESTAMP AS fecha_creacion, 0 AS volumen_activos, CHGMARKER
FROM borrados
WHERE table_name = 'fondos';

CREATE OR REPLACE VIEW contratos_borrados AS
SELECT pk_id AS id, CURRENT_DATE AS fecha_inicio_contrato, CURRENT_DATE AS fecha_vencimiento_contrato, 'ID_PARTE_A' AS id_parte_a, 'CONDICIONES_PARTE_A' AS condiciones_contrato_parte_a, 'TERMINOS_PARTE_A' AS terminos_pago_parte_a, 'ID_PARTE_B' AS id_parte_b, 'CONDICIONES_PARTE_B' AS condiciones_contrato_parte_b, 'TERMINOS_PARTE_B' AS terminos_pago_parte_b, CHGMARKER
FROM borrados
WHERE table_name = 'contratos';

CREATE OR REPLACE VIEW ordenes_full AS
SELECT e.id AS id_expediente, e.fecha_contratacion AS fecha_contratacion_expediente, e.titular AS titular_expediente, e.numero_expediente, e.CHGMARKER AS fecha_ultima_modificacion_expediente,
 f.id AS id_fondo, f.nombre AS nombre_fondo, f.tipo AS tipo_fondo, f.fecha_creacion AS fecha_creacion_fondo, f.volumen_activos AS volumen_activos_fondo, f.CHGMARKER AS fecha_ultima_modificacion_fondo,
 o.id AS id_ordenes, o.fecha_orden AS fecha_ordenes, o.estado AS estado_ordenes, o.tipo_orden AS tipo_ordenes, o.fecha_ejecucion AS fecha_ejecucion_ordenes, o.importe AS importe_ordenes, o.CHGMARKER AS fecha_ultima_modificacion_ordenes
FROM expedientes e, fondos f, ordenes o
WHERE e.id = o.id_expediente
AND f.id = o.id_fondo;

CREATE OR REPLACE VIEW VW_EXPEDIENTES
AS
SELECT
    ID, FECHA_CONTRATACION, TITULAR, NUMERO_EXPEDIENTE, CHGMARKER, ID AS CHGINC
FROM
    EXPEDIENTES;