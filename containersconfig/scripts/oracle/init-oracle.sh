#!/bin/bash

# Oracle DB 
echo "Esperando Oracle..."
sleep 5
while ! echo exit | sqlplus system/$ORACLE_PASSWORD@//localhost:1521/XEPDB1 > /dev/null 2>&1; do
    echo "Esperando que el Listener esté listo..."
    sleep 5
done

# Comprobar si el usuario ya existe
USER_EXISTS=$(sqlplus -s system/$ORACLE_PASSWORD@//localhost:1521/XEPDB1 <<EOF
SET HEADING OFF
SET FEEDBACK OFF
SELECT username FROM dba_users WHERE username = 'APPUSER';
EXIT;
EOF
)

if [[ -z "$USER_EXISTS" ]]; then
    echo "Usuario APPUSER no existe. Creándolo..."

    sqlplus system/$ORACLE_PASSWORD@//localhost:1521/XEPDB1 <<EOF
CREATE USER appuser IDENTIFIED BY appuser123;
GRANT CONNECT, RESOURCE TO appuser;
GRANT CREATE TABLE, CREATE VIEW, CREATE PROCEDURE TO appuser;
ALTER USER appuser QUOTA UNLIMITED ON USERS;
GRANT UNLIMITED TABLESPACE TO appuser;
EXIT;
EOF

    echo "Ejecutando init.sql script..."
    sqlplus appuser/appuser123@//localhost:1521/XEPDB1 @/oracle/init-oracle.sql
else
    echo "El usuario APPUSER ya existe. Saltando creación e inicialización."
fi

# Mantener el contenedor en ejecución
tail -f /dev/null
