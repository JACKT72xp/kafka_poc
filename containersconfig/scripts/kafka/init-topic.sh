until kafka-topics.sh --bootstrap-server kafka:9092 --list > /dev/null 2>&1; do
  echo "Esperando a que Kafka esté listo..."
  sleep 2
done
 
 
 
# Crear topic
echo "Creando topic..."
kafka-topics.sh --create --if-not-exists --bootstrap-server kafka:9092 --replication-factor 1 --partitions 2 --topic test-topic
 