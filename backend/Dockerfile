# Multi-stage Dockerfile for Spring Boot 3 Backend
# Stage 1: Build the application with Maven
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy Maven descriptor and download dependencies
COPY pom.xml .
# Install maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
RUN mvn dependency:go-offline -B

# Copy application source code and package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create a non-root application user for container security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Cloud providers (e.g. Render) dynamically provide the PORT environment variable
ENV PORT=8080
EXPOSE ${PORT}

# Run the Spring Boot application binding to 0.0.0.0 and PORT
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]

