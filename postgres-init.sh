#!/bin/bash
set -e

# Definir a senha do PostgreSQL a partir do secret
export POSTGRES_PASSWORD=$(cat /run/secrets/postgres_password)

# Continuar com a inicialização padrão do PostgreSQL
exec docker-entrypoint.sh postgres