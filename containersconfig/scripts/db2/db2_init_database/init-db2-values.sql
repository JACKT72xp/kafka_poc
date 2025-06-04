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
INSERT INTO retenciones_1 (id_orden, fecha_retencion, tipo, importe_retencion)
VALUES (1, '2025-03-01', 'Impuesto', 2000.00);

INSERT INTO retenciones_2 (id_traspaso, fecha_retencion, tipo, importe_retencion)
VALUES (1, '2025-03-01', 'Impuesto', 2000.00);

-- Insertar contrato
insert into contratos (fecha_inicio_contrato, fecha_vencimiento_contrato, id_parte_a, condiciones_contrato_parte_a, terminos_pago_parte_a, id_parte_b, condiciones_contrato_parte_b, terminos_pago_parte_b)
values ('2020-01-01', '2024-01-01', '10', 'CONDICIONES CONTRATO 10, 20 PARTE A', 'TERMINOS PAGO 10, 20 PARTE A', '20', 'CONDICIONES CONTRATO 10, 20 PARTE B', 'TERMINOS PAGO 10, 20 PARTE B');

