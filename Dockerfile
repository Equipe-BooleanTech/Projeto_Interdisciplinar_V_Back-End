FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy the pom.xml file to download dependencies
COPY pom.xml .
# Download all dependencies. Dependencies will be cached if the pom.xml file is not changed
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn package -DskipTests

# Production image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built artifact from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Change ownership to the non-root user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose the port
EXPOSE ${SERVER_PORT}

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]