# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy everything from brightway_dropout folder
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
