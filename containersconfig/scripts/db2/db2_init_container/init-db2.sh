
#!/bin/bash

check_tables_exist() {
    local tables=("EXPEDIENTES" "ORDENES" "FONDOS" "TRASPASOS" "RETENCIONES" "CONTRATOS" "BORRADOS") 
    for table in "${tables[@]}"; do
        if ! su - db2inst1 -c "db2 connect to $DBNAME && db2 -x \"SELECT TABNAME FROM SYSCAT.TABLES WHERE TABNAME = '$table'\"" | grep -q "$table"; then
            return 1  
        fi
    done
    return 0  
}

if ! check_tables_exist; then
    echo "🚀 Ejecutando script de inicialización..."
    su - db2inst1 -c "db2 connect to $DBNAME && db2 -tvf /init-db2/init-db2.sql"
else
    echo "Todas las tablas requeridas ya existen. No se requiere inicialización."
fi


chown root:db2iadm1 /database/config/db2inst1/sqllib/adm/fencedid
