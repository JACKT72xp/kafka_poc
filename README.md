# 🧩 Data Integration PoC with Kafka Connect, DB2 and Oracle using Podman

Este proyecto es una prueba de concepto (PoC) para demostrar una arquitectura de integración de datos entre bases de datos DB2 y Oracle utilizando Apache Kafka, Kafka Connect y conectores JDBC. Todo se despliega utilizando **Podman** y **Podman Compose**, lo que permite una alternativa a Docker para entornos rootless o más seguros.

---

## 📦 1. Instalación de Podman y Podman Compose

### Linux (ejemplo con Ubuntu/Debian)

```bash
sudo apt update && sudo apt install -y podman podman-compose
```

### Validar instalación:

```bash
podman --version
podman-compose --version
```

---

## 📁 2. Descarga de recursos necesarios

### JARs requeridos (montados en el contenedor Kafka Connect):

- `db2jcc4.jar` – driver JDBC para IBM DB2
- `ojdbc8.jar` – driver JDBC para Oracle (si se desea replicar hacia Oracle)
- Confluent JDBC connector `.jar` (si usas `io.confluent.connect.jdbc.JdbcSourceConnector`)

#### Recomendado: organizar tu carpeta `plugins/` así:

```bash
plugins/
└── jdbc/
    ├── db2jcc4.jar
    ├── ojdbc8.jar
    ├── kafka-connect-jdbc-<version>.jar
```

> ⚠️ Asegúrate de que los `.jar` estén disponibles y se monten correctamente en Kafka Connect.

---

## 🧱 3. Estructura del docker-compose (Podman Compose)

```bash
podman-compose -f docker-compose.yaml up -d
```

### Servicios incluidos:

- **Kafka + Zookeeper** (bitnami o confluent-compatible)
- **Kafka Connect** (usando el conector JDBC y drivers montados)
- **DB2** (imagen oficial `icr.io/db2_community/db2`)
- **Oracle XE** (opcional, si se desea destino)
- **Kafka UI** (opcional, para debug visual)
- **DBeaver** (opcional, para gestionar las DB vía UI)

---

## ⚙️ 4. Scripts de creación y conexión

### 1. Conectar a DB2 y crear la base `TESTDB`

La imagen de DB2 puede tardar unos minutos en estar lista. Puedes verificar su estado con:

```bash
podman exec -it db2 bash
su - db2inst1
db2 connect to TESTDB
```

#### Script `db2-init.sql`

```sql
CREATE SCHEMA DEMO;
CREATE TABLE DEMO.CUSTOMERS (
  ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  NAME VARCHAR(100),
  EMAIL VARCHAR(100),
  CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (ID)
);
```

> Este script debe ejecutarse manualmente o montarse para ejecución post-inicio.

---

## 🔌 5. Creación y validación del conector JDBC

### 1. Verificar que el conector JDBC esté disponible:

```bash
curl -s http://localhost:8083/connector-plugins | jq
```

Busca `io.confluent.connect.jdbc.JdbcSourceConnector`.

---

### 2. Crear el conector vía POST:

```bash
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test-source-CU-db2-jdbc-autoincrement",
    "config": {
        "bootstrap.servers": "kafka:9092",
        "key.converter": "org.apache.kafka.connect.json.JsonConverter",
        "value.converter": "org.apache.kafka.connect.json.JsonConverter",
        "key.converter.schemas.enable": "true",
        "value.converter.schemas.enable": true,
        "offset.storage.file.filename": "/tmp/connect.offsets",
        "offset.flush.interval.ms": 10000,
        "plugin.path": "/kafka/connectors",
        "listeners": "http://0.0.0.0:9092",
        "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
        "tasks.max": "1",
        "connection.url": "jdbc:db2://db2:50000/TESTDB",
        "connection.user": "db2inst1",
        "connection.password": "db2inst1-pwd",
        "dialect.name": "Db2DatabaseDialect",
        "mode": "timestamp+incrementing",
        "incrementing.column.name": "ID",
        "timestamp.column.name": "CHGMARKER",
        "topic.prefix": "test-db2-jdbc-",
        "timestamp.delay.interval.ms": 500,
        "poll.interval.ms": 1000,
        "validate.non.null": false,
        "decimal.format": "NUMERIC",
        "table.types": "TABLE,VIEW",
        "transforms": "InsertKey,ExtractId",
        "transforms.InsertKey.type": "org.apache.kafka.connect.transforms.ValueToKey",
        "transforms.InsertKey.fields": "ID",
        "transforms.ExtractId.type": "org.apache.kafka.connect.transforms.ExtractField$Key",
        "transforms.ExtractId.field": "ID",
        "schema.pattern=DB2INST1": "DB2INST1",
        "table.whitelist": "DB2INST1.EXPEDIENTES,DB2INST1.FONDOS,DB2INST1.ORDENES,DB2INST1.RETENCIONES"
    }
}'
```

En el contenedor del conector db2 vale con hacer:
(Es necesario esperar a que se cree la base de datos db2)

```bash
curl -X POST -H "Content-Type: application/json" --data @/connector_config/db2_connector_config.json http://localhost:8083/connectors
```

### 3. Verificar estado del conector:

```bash
curl -s http://localhost:8083/connectors/db2-source-connector/status | jq
```

> Asegúrate de que el estado sea `"RUNNING"` y sin errores de clase JDBC ni fallos de conexión.

---

## 🧪 Tips de depuración

- Si ves `"No suitable driver"` revisa que el `.jar` esté en el `volume` y `PLUGIN_PATH`.
- Si ves `"Connection refused"` valida que el contenedor `db2` esté en red y exponga correctamente el puerto.
- Puedes usar `podman exec -it connect bash` y verificar `/kafka/connect/` para confirmar presencia de `.jar`.

---

## 🔚 Resultado esperado

Los cambios en la tabla `DEMO.CUSTOMERS` de DB2 deben publicarse en un topic Kafka con nombre `db2-DEMO.CUSTOMERS`.

---

## 🛠️ Requisitos mínimos

- Podman 4.0+
- Podman Compose
- JDK 11+ (en caso de construir plugins manualmente)
- Kafka Connect JDBC 10.7.4 (compatible con DB2)

---

## 📄 Licencia

Este proyecto es solo para fines de prueba y demostración.
