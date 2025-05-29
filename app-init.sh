#!/bin/bash
set -e

# Obter a senha do secret e configurar como variável de ambiente
export SPRING_DATASOURCE_PASSWORD=$(cat /run/secrets/app_password)

# Iniciar a aplicação Spring Boot
exec java -jar /app/app.jar