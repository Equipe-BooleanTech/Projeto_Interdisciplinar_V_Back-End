FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar o arquivo JAR gerado pelo Maven/Gradle
COPY target/*.jar app.jar

# Copiar o script de inicialização
COPY app-init.sh /app/app-init.sh

# Tornar o script executável
RUN chmod +x /app/app-init.sh

# Porta que a aplicação Spring Boot vai expor
EXPOSE 8080

# Usar o script de inicialização como ponto de entrada
ENTRYPOINT ["/app/app-init.sh"]
