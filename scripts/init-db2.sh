#!/bin/bash

# Inicializar instancia
/var/db2_setup/lib/setup_db2_instance.sh

# Esperar que el demonio DB2 esté activo
su - db2inst1 -c 'db2start'

# Esperar que TESTDB esté disponible
until su - db2inst1 -c 'db2 connect to TESTDB' >/dev/null 2>&1; do
  echo "⏳ Esperando que TESTDB esté disponible..."
  sleep 5
done

# Ejecutar script de inicialización
echo "🚀 Ejecutando script de inicialización..."
su - db2inst1 -c 'db2 -tvf /db2-init/db2-init.sql'

# Dejar el contenedor vivo
tail -f /dev/null