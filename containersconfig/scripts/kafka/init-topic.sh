until kafka-topics.sh --bootstrap-server kafka:9092 --list > /dev/null 2>&1; do
  echo "Esperando a que Kafka esté listo..."
  sleep 2
done
 
 
 
# Crear topic
echo "Creando topics..."
kafka-topics.sh --create --if-not-exists --bootstrap-server kafka:9092 --replication-factor 1 --partitions 2 --topic test-topic
kafka-topics.sh --create --if-not-exists --bootstrap-server kafka:9092 --replication-factor 1 --partitions 2 --topic test-db2-jdbc-EXPEDIENTES
kafka-topics.sh --create --if-not-exists --bootstrap-server kafka:9092 --replication-factor 1 --partitions 2 --topic test-db2-jdbc-FONDOS
kafka-topics.sh --create --if-not-exists --bootstrap-server kafka:9092 --replication-factor 1 --partitions 2 --topic test-db2-jdbc-ORDENES
kafka-topics.sh --create --if-not-exists --bootstrap-server kafka:9092 --replication-factor 1 --partitions 2 --topic test-db2-jdbc-RETENCIONES












