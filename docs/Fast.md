
# Sincronización de Datos CFO-FND basada en Eventos con Kafka y JDBC

Este repositorio documenta una Prueba de Concepto (PoC) de integración de datos entre dos sistemas empresariales —CFO (origen, DB2 Mainframe) y FND (destino, Oracle)— utilizando Kafka como intermediario, y aplicando patrones de eventos en tiempo casi real mediante conectores JDBC.

---

## 📘 Contexto

Desde la puesta en producción del sistema FND, la sincronización con CFO presenta limitaciones que causan incidencias recurrentes. Actualmente, se usan tres mecanismos:
- **Réplica Online**: Inserción/actualización inmediata de expedientes, órdenes, traspasos.
- **Mini Réplica** (Batch varias veces al día): Corrige inconsistencias de la online.
- **Batch Nocturno**: Consolida todo lo pendiente y sincroniza entidades como señales de contrato, retenciones y operaciones.

### Limitaciones del modelo actual
- Errores tras rollback o anulaciones no capturados.
- Eventos que quedan en estado "pendiente de envío".
- Necesidad de múltiples jobs batch para validar consistencia.

---

## 🎯 Objetivo del PoC

Implementar una arquitectura de eventos para sincronización CFO-FND mediante **Kafka + Kafka Connect**, usando:
- **JDBC Source Connector** para extraer desde DB2.
- **JDBC Sink Connector** para cargar en Oracle.
- **Kafka Topics** como mediadores de datos.
- Posibilidad de extender hacia Elasticsearch u otros sinks.

---

## 🛠️ Arquitectura de Referencia

```
Mainframe (DB2)      Oracle (FND)
      │                    ▲
      ▼                    │
JDBC Source         JDBC Sink
  (DB2)               (Oracle)
      │                    ▲
      └──── Kafka ────────┘
            ▲     ▲
     Kafka UI   Cloudbeaver
```

---

## 🧱 Componentes en Docker/Podman Compose

| Servicio        | Rol                                                |
|----------------|----------------------------------------------------|
| Zookeeper       | Coordinador de Kafka                              |
| Kafka           | Broker de eventos                                 |
| Kafka Connect   | Orquestador de conectores JDBC                    |
| DB2             | Base de datos de origen                           |
| Oracle XE       | Base de datos de destino                          |
| Kafka UI        | Dashboard de monitoreo Kafka                      |
| Cloudbeaver     | Herramienta DB visual, conexión DB2/Oracle        |

---

## 🚀 Flujo de Datos

1. **Store Procedure en DB2** genera nuevos registros en tablas como `ORDEN`, `TRASPASO`, etc.
2. **Kafka Connect JDBC Source** consulta periódicamente estas tablas y publica eventos.
3. **Kafka Topics** almacenan los eventos por entidad (`db2-orden`, `db2-traspaso`, etc).
4. **JDBC Sink Connector** en Oracle consume desde estos topics e inserta/actualiza datos.
5. **Kafka UI** y **Cloudbeaver** permiten monitoreo y validación del flujo.

---

## ✅ Ventajas del nuevo modelo

- Eliminación de jobs batch y validaciones redundantes.
- Near Real-Time Sync.
- Resiliencia por desacoplamiento de componentes.
- Escalabilidad para nuevos orígenes/destinos.

---

## 🧩 Posibilidades de extensión

- Notificación a sistemas externos vía Webhooks o Kafka Streams.
- Indexación en Elasticsearch para búsqueda rápida.
- Auditoría y tracking histórico con Apache Pinot o ksqlDB.

---
