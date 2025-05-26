#!/bin/bash

# Oracle DB 
echo "Esperando Oracle "
sleep 5
while ! echo exit | sqlplus system/$ORACLE_PASSWORD@//localhost:1521/XEPDB1 > /dev/null 2>&1; do
    echo "Esperando que el Listener esté listo..."
    sleep 5
done

echo "Creando nuevo usuario..."
sqlplus system/$ORACLE_PASSWORD@//localhost:1521/XEPDB1 <<EOF
CREATE USER appuser IDENTIFIED BY appuser123;
GRANT CONNECT, RESOURCE TO appuser;
GRANT CREATE TABLE, CREATE VIEW, CREATE PROCEDURE TO appuser;
ALTER USER appuser QUOTA UNLIMITED ON USERS;
GRANT UNLIMITED TABLESPACE TO appuser;
EXIT;
EOF

#  DDL script
echo "Ejecutando init.sql script..."
###sqlplus system/$ORACLE_PASSWORD@//localhost:1521/XEPDB1 @/oracle/init-oracle.sql

sqlplus appuser/appuser123@//localhost:1521/XEPDB1 @/oracle/init-oracle.sql

# 
tail -f /dev/null

