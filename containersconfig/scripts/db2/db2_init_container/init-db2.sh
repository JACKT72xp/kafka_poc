#!/bin/bash
 


check_schema_exists() {
su - db2inst1 -c "db2 connect to TESTDB && db2 -x \"SELECT SCHEMANAME FROM SYSCAT.SCHEMATA WHERE SCHEMANAME = 'DEMO'\"" | grep DEMO
}
 
check_table_exists() {
su - db2inst1 -c "db2 connect to TESTDB && db2 -x \"SELECT TABNAME FROM SYSCAT.TABLES WHERE TABSCHEMA = 'DEMO' AND TABNAME = 'CUSTOMERS'\"" | grep CUSTOMERS
}
 
# Ejecutar script de inicialización si el esquema o la tabla no existen
if ! check_schema_exists || ! check_table_exists; then
    echo "🚀 Ejecutando script de inicialización..."
    su - db2inst1 -c "db2 connect to TESTDB && db2 -tvf /init-db2/init-db2.sql"
else
    echo "El esquema DEMO y la tabla CUSTOMERS ya existen. No se requiere inicialización."
fi
 
chown root:db2iadm1 /database/config/db2inst1/sqllib/adm/fencedid