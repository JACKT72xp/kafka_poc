-- Table: expedientes
CREATE TABLE expedientes (
    id NUMBER PRIMARY KEY,
    fecha_contratacion DATE NOT NULL,
    titular VARCHAR2(255) NOT NULL,
    numero_expediente VARCHAR2(50) NOT NULL UNIQUE,
    CHGMARKER TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CHGINC NUMBER NOT NULL
);

-- Table: fondos
CREATE TABLE fondos (
    id NUMBER PRIMARY KEY,
    nombre VARCHAR2(255) NOT NULL,
    tipo VARCHAR2(50) NOT NULL,
    fecha_creacion DATE NOT NULL,
    volumen_activos NUMBER(20,2) NOT NULL,
    CHGMARKER TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CHGINC NUMBER NOT NULL
);

-- Table: ordenes
CREATE TABLE ordenes (
    id_orden NUMBER PRIMARY KEY,
    id_expediente NUMBER NOT NULL,
    fecha_orden DATE NOT NULL,
    estado_orden VARCHAR2(50) NOT NULL,
    tipo_orden VARCHAR2(10) CHECK (tipo_orden IN ('compra', 'venta')),
    fecha_ejecucion_orden DATE NOT NULL,
    importe_orden NUMBER(20,2) NOT NULL,
    id_fondo NUMBER NOT NULL,
    CHGMARKER TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CHGINC NUMBER NOT NULL,
    CONSTRAINT fk_ordenes_expediente FOREIGN KEY (id_expediente) REFERENCES expedientes(id) ON DELETE CASCADE,
    CONSTRAINT fk_ordenes_fondo FOREIGN KEY (id_fondo) REFERENCES fondos(id) ON DELETE CASCADE
);

-- Table: traspasos
CREATE TABLE traspasos (
    id NUMBER PRIMARY KEY,
    id_expediente NUMBER NOT NULL,
    id_fondo_origen NUMBER NOT NULL,
    id_fondo_destino NUMBER NOT NULL,
    fecha DATE NOT NULL,
    importe NUMBER(20,2) NOT NULL,
    CHGMARKER TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CHGINC NUMBER NOT NULL,
    CONSTRAINT fk_traspasos_expediente FOREIGN KEY (id_expediente) REFERENCES expedientes(id) ON DELETE CASCADE,
    CONSTRAINT fk_traspasos_origen FOREIGN KEY (id_fondo_origen) REFERENCES fondos(id) ON DELETE CASCADE,
    CONSTRAINT fk_traspasos_destino FOREIGN KEY (id_fondo_destino) REFERENCES fondos(id) ON DELETE CASCADE
);

-- Table: retenciones
CREATE TABLE retenciones_1 (
    id NUMBER PRIMARY KEY,
    id_orden NUMBER NOT NULL,
    fecha_retencion DATE NOT NULL,
    tipo VARCHAR2(50) NOT NULL,
    importe_retencion NUMBER(20,2) NOT NULL,
    CHGMARKER TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CHGINC NUMBER NOT NULL,
    CONSTRAINT fk_retenciones_ordenes FOREIGN KEY (id_orden) REFERENCES ordenes(id_orden) ON DELETE CASCADE
);

-- Table: retenciones
CREATE TABLE retenciones_2 (
    id NUMBER PRIMARY KEY,
    id_traspaso NUMBER NOT NULL,
    fecha_retencion DATE NOT NULL,
    tipo VARCHAR2(50) NOT NULL,
    importe_retencion NUMBER(20,2) NOT NULL,
    CHGMARKER TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CHGINC NUMBER NOT NULL,
    CONSTRAINT fk_retenciones_traspasos FOREIGN KEY (id_traspaso) REFERENCES traspasos(id) ON DELETE CASCADE
);

-- Table: contratos_parte_a
CREATE TABLE contratos_parte_a (
    id NUMBER PRIMARY KEY,
    id_parte_a NUMBER NOT NULL,
    fecha_inicio_contrato DATE NOT NULL,
    fecha_vencimiento_contrato DATE NOT NULL,
    condiciones_contrato VARCHAR2(50) NOT NULL,
    terminos_pago VARCHAR2(50) NOT NULL,
    CHGMARKER TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CHGINC NUMBER NOT NULL
);

-- Table: contratos_parte_b
CREATE TABLE contratos_parte_b (
    id NUMBER PRIMARY KEY,
    id_parte_b NUMBER NOT NULL,
    fecha_inicio_contrato DATE NOT NULL,
    fecha_vencimiento_contrato DATE NOT NULL,
    condiciones_contrato VARCHAR2(50) NOT NULL,
    terminos_pago VARCHAR2(50) NOT NULL,
    CHGMARKER TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CHGINC NUMBER NOT NULL    
);



CREATE TABLE PROCESO_BANCARIO (
    ID_ESTADO VARCHAR2(20) PRIMARY KEY,
    ID NUMBER,
    ESTADO VARCHAR2(20) NOT NULL, 
    NOMBRE VARCHAR2(50) NOT NULL,
    FECHA DATE NOT NULL, 
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CHGINC NUMBER
);






CREATE OR REPLACE TRIGGER trg_update_expedientes
BEFORE UPDATE OF fecha_contratacion, titular, numero_expediente ON expedientes
FOR EACH ROW
BEGIN
    :NEW.CHGMARKER := CURRENT_TIMESTAMP;
END;
/


CREATE OR REPLACE TRIGGER trg_update_fondos
BEFORE UPDATE OF nombre, tipo, fecha_creacion, volumen_activos ON fondos
FOR EACH ROW
BEGIN
    :NEW.CHGMARKER := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_update_ordenes
BEFORE UPDATE OF id_expediente, fecha_orden, estado, tipo_orden, fecha_ejecucion, importe_orden, id_fondo ON ordenes
FOR EACH ROW
BEGIN
    :NEW.CHGMARKER := CURRENT_TIMESTAMP;
END;
/


CREATE OR REPLACE TRIGGER trg_update_traspasos
BEFORE UPDATE OF id_expediente, id_fondo_origen, id_fondo_destino, fecha, importe ON traspasos
FOR EACH ROW
BEGIN
    :NEW.CHGMARKER := CURRENT_TIMESTAMP;
END;
/
