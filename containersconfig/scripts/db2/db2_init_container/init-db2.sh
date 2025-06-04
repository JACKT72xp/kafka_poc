#!/bin/bash

check_db_empty() {
  su - db2inst1 -c "db2 connect to TESTDB && db2 -x \"SELECT TABNAME FROM SYSCAT.TABLES WHERE TABSCHEMA = 'DB2INST1' AND TYPE = 'T'\""

}


# Evaluar si la estructura ya existía antes
if ! check_db_empty; then
  estructura_existia=false
else
  estructura_existia=true
fi

# Ejecutar siempre el script de estructura
echo "🏗️ Ejecutando script de estructura..."
su - db2inst1 -c "db2 connect to TESTDB && db2 -tvf /init-db2/init-db2.sql"

# Ejecutar el script de datos solo si la estructura no existía antes
if [ "$estructura_existia" = false ]; then
  echo "📥 Ejecutando script de carga de datos inicial..."
  su - db2inst1 -c "db2 connect to TESTDB && db2 -tvf /init-db2/init-db2-values.sql"
else
  echo "✅ La estructura ya existía. No se ejecuta carga de datos."
fi

# Cambiar permisos
chown root:db2iadm1 /database/config/db2inst1/sqllib/adm/fencedid
