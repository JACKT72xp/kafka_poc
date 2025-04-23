#!/bin/bash
set -e

echo "⏳ Esperando a que DB2 esté completamente levantado (verificando proceso y puerto)..."

# Esperar a que el proceso db2sysc esté en ejecución
until pgrep -f db2sysc > /dev/null; do
  echo "⌛ Proceso db2sysc aún no está activo..."
  sleep 5
done

# Esperar a que el puerto de DB2 esté escuchando
until nc -z localhost 50000; do
  echo "⌛ Puerto 50000 aún no está disponible..."
  sleep 5
done

echo "✅ DB2 está completamente listo (proceso y puerto verificados). Ejecutando script..."

# Ejecutar el script una sola vez si no se hizo antes
MARKER="/home/db2inst1/.db2_sql_done"
if [ ! -f "$MARKER" ]; then
  su - db2inst1 -c "db2 connect to TESTDB && db2 -tvf /db2-init/db2-init.sql"
  touch "$MARKER"
  echo "✅ Script ejecutado y marcado."
else
  echo "⚠️ Script ya fue ejecutado anteriormente. Saltando."
fi

tail -f /dev/null