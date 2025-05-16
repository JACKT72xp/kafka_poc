-- Table: expedientes
CREATE TABLE expedientes (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fecha_contratacion DATE NOT NULL,
    titular VARCHAR2(255) NOT NULL,
    numero_expediente VARCHAR2(50) NOT NULL UNIQUE,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE OR REPLACE TRIGGER trg_update_expedientes
BEFORE UPDATE OF fecha_contratacion, titular, numero_expediente ON expedientes
FOR EACH ROW
BEGIN
    :NEW.fecha_ultima_modificacion := CURRENT_TIMESTAMP;
END;
/

-- Table: fondos
CREATE TABLE fondos (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_fondo VARCHAR2(255) NOT NULL,
    tipo_fondo VARCHAR2(50) NOT NULL,
    fecha_creacion_fondo DATE NOT NULL,
    volumen_activos NUMBER(20,2) NOT NULL,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE OR REPLACE TRIGGER trg_update_fondos
BEFORE UPDATE OF nombre_fondo, tipo_fondo, fecha_creacion_fondo, volumen_activos ON fondos
FOR EACH ROW
BEGIN
    :NEW.fecha_ultima_modificacion := CURRENT_TIMESTAMP;
END;
/

-- Table: ordenes
CREATE TABLE ordenes (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_expediente NUMBER NOT NULL,
    fecha_orden DATE NOT NULL,
    estado VARCHAR2(50) NOT NULL,
    tipo_orden VARCHAR2(10) CHECK (tipo_orden IN ('compra', 'venta')),
    fecha_ejecucion DATE NOT NULL,
    importe NUMBER(20,2) NOT NULL,
    id_fondo NUMBER NOT NULL,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_ordenes_expediente FOREIGN KEY (id_expediente) REFERENCES expedientes(id) ON DELETE CASCADE,
    CONSTRAINT fk_ordenes_fondo FOREIGN KEY (id_fondo) REFERENCES fondos(id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER trg_update_ordenes
BEFORE UPDATE OF id_expediente, fecha_orden, estado, tipo_orden, fecha_ejecucion, importe, id_fondo ON ordenes
FOR EACH ROW
BEGIN
    :NEW.fecha_ultima_modificacion := CURRENT_TIMESTAMP;
END;
/

-- Table: traspasos
CREATE TABLE traspasos (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_expediente NUMBER NOT NULL,
    id_fondo_origen NUMBER NOT NULL,
    id_fondo_destino NUMBER NOT NULL,
    fecha DATE NOT NULL,
    importe NUMBER(20,2) NOT NULL,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_traspasos_expediente FOREIGN KEY (id_expediente) REFERENCES expedientes(id) ON DELETE CASCADE,
    CONSTRAINT fk_traspasos_origen FOREIGN KEY (id_fondo_origen) REFERENCES fondos(id) ON DELETE CASCADE,
    CONSTRAINT fk_traspasos_destino FOREIGN KEY (id_fondo_destino) REFERENCES fondos(id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER trg_update_traspasos
BEFORE UPDATE OF id_expediente, id_fondo_origen, id_fondo_destino, fecha, importe ON traspasos
FOR EACH ROW
BEGIN
    :NEW.fecha_ultima_modificacion := CURRENT_TIMESTAMP;
END;
/

-- Table: retenciones
CREATE TABLE retenciones (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_expediente NUMBER NOT NULL,
    fecha_retencion DATE NOT NULL,
    tipo VARCHAR2(50) NOT NULL,
    importe_retencion NUMBER(20,2) NOT NULL,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_retenciones_expediente FOREIGN KEY (id_expediente) REFERENCES expedientes(id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER trg_update_retenciones
BEFORE UPDATE OF id_expediente, fecha_retencion, tipo, importe_retencion ON retenciones
FOR EACH ROW
BEGIN
    :NEW.fecha_ultima_modificacion := CURRENT_TIMESTAMP;
END;
/


INSERT INTO expedientes (fecha_contratacion, titular, numero_expediente)
VALUES (TO_DATE('2025-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Juan Pérez', 'EXP-001');

INSERT INTO fondos (nombre_fondo, tipo_fondo, fecha_creacion_fondo, volumen_activos)
VALUES
('Fondo A', 'Equity', TO_DATE('2020-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 5000000.00);
--('Fondo B', 'Bond', TO_DATE('2019-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3000000.00);

INSERT INTO fondos (nombre_fondo, tipo_fondo, fecha_creacion_fondo, volumen_activos)
VALUES
('Fondo B', 'Bond', TO_DATE('2019-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 3000000.00);

INSERT INTO ordenes (id_expediente, fecha_orden, estado, tipo_orden, fecha_ejecucion, importe, id_fondo)
VALUES (1, TO_DATE('2025-02-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Pendiente', 'compra', TO_DATE('2025-02-10', 'YYYY-MM-DD'), 100000.00, 1);

INSERT INTO traspasos (id_expediente, id_fondo_origen, id_fondo_destino, fecha, importe)
VALUES (1, 1, 2, TO_DATE('2025-02-15 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 50000.00);

INSERT INTO retenciones (id_expediente, fecha_retencion, tipo, importe_retencion)
VALUES (1, TO_DATE('2025-03-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'Impuesto', 2000.00);

-- Table: contratos_parte_a
CREATE TABLE contratos_parte_a (
    id NUMBER PRIMARY KEY,
    id_parte_a NUMBER NOT NULL,
    fecha_inicio_contrato DATE NOT NULL,
    fecha_vencimiento_contrato DATE NOT NULL,
    condiciones_contrato VARCHAR2(50) NOT NULL,
    terminos_pago VARCHAR2(50) NOT NULL,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Table: contratos_parte_b
CREATE TABLE contratos_parte_b (
    id NUMBER PRIMARY KEY,
    id_parte_b NUMBER NOT NULL,
    fecha_inicio_contrato DATE NOT NULL,
    fecha_vencimiento_contrato DATE NOT NULL,
    condiciones_contrato VARCHAR2(50) NOT NULL,
    terminos_pago VARCHAR2(50) NOT NULL,
    fecha_ultima_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);


INSERT INTO contratos_parte_a (id, id_parte_a, fecha_inicio_contrato, fecha_vencimiento_contrato, condiciones_contrato, terminos_pago)
VALUES (1, 10, TO_DATE('2020-01-01', 'YYYY-MM-DD'), TO_DATE('2024-01-01', 'YYYY-MM-DD'), 'CONDICIONES CONTRATO 10, 20 PARTE A', 'TERMINOS PAGO 10, 20 PARTE A');

INSERT INTO contratos_parte_b (id, id_parte_b, fecha_inicio_contrato, fecha_vencimiento_contrato, condiciones_contrato, terminos_pago)
VALUES (1, 20, TO_DATE('2020-01-01', 'YYYY-MM-DD'), TO_DATE('2024-01-01', 'YYYY-MM-DD'), 'CONDICIONES CONTRATO 10, 20 PARTE B', 'TERMINOS PAGO 10, 20 PARTE B');
