
# Arquitectura Docker Compose para PoC de Integración de Datos (DB2 → Kafka → Oracle)

Este `README.md` documenta la arquitectura de integración de datos diseñada con `docker-compose` para conectar una base de datos DB2 como fuente, a través de Kafka y Kafka Connect, hacia una base de datos destino Oracle.

## Contenido del docker-compose

```yaml
version: '3.8'

services:

  zookeeper:
    image: bitnami/zookeeper:3.8
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      - ALLOW_ANONYMOUS_LOGIN=yes
    healthcheck:
      test: ["CMD", "zkServer.sh", "status"]
      interval: 10s
      timeout: 5s
      retries: 5

  kafka:
    image: bitnami/kafka:3.5
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      - KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092
      - KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE=true
    depends_on:
      zookeeper:
        condition: service_healthy

  connect:
    image: confluentinc/cp-kafka-connect:7.4.0
    container_name: connect
    ports:
      - "8083:8083"
    depends_on:
      kafka:
        condition: service_healthy
    environment:
      CONNECT_BOOTSTRAP_SERVERS: kafka:9092
      CONNECT_REST_ADVERTISED_HOST_NAME: connect
      CONNECT_GROUP_ID: connect-cluster
      CONNECT_CONFIG_STORAGE_TOPIC: connect-config
      CONNECT_OFFSET_STORAGE_TOPIC: connect-offsets
      CONNECT_STATUS_STORAGE_TOPIC: connect-status
      CONNECT_KEY_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      CONNECT_VALUE_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      CONNECT_PLUGIN_PATH: /kafka/connect
    volumes:
      - ./plugins:/kafka/connect
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8083/"]
      interval: 10s
      timeout: 5s
      retries: 5

  db2:
    image: icr.io/db2_community/db2:latest
    container_name: db2
    privileged: true
    platform: linux/amd64
    ports:
      - "50000:50000"
    environment:
      LICENSE: accept
      DB2INST1_PASSWORD: password
      DB2INST1_USER: db2inst1
      DBNAME: TESTDB
    volumes:
      - ./scripts/db2:/db2-init:ro

  oracle:
    image: gvenzl/oracle-xe:21-slim
    container_name: oracle
    ports:
      - "1521:1521"
    environment:
      ORACLE_PASSWORD: oracle

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-ui
    ports:
      - "8080:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
    depends_on:
      kafka:
        condition: service_healthy
```

## Descripción de los servicios

### zookeeper
- Coordinador necesario para Kafka en modo tradicional (no KRaft).
- Expone el puerto 2181.

### kafka
- Broker de Kafka usando Zookeeper como backend.
- Expone el puerto 9092.

### connect
- Contenedor de Kafka Connect con plugins JDBC montados desde `./plugins`.
- Requiere que los `.jar` necesarios como `db2jcc4.jar` estén disponibles.
- Expone el puerto 8083 (REST API).

### db2
- Contenedor oficial Community Edition de IBM DB2.
- Inicializa automáticamente la base `TESTDB` y un usuario.
- Monta scripts de inicialización desde `./scripts/db2`.

### oracle
- Contenedor de Oracle XE para pruebas.
- Exposición estándar del puerto 1521.

### kafka-ui
- Interfaz de usuario para monitorear topics, consumidores, y conectores.
- Usa la API REST de Kafka para conectarse al broker.

