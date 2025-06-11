# ---------- Build Stage ----------
FROM maven:3.9.3-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom and install dependencies first for better Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the rest of the project files
COPY . .

# Package the application (skipping tests for faster build)
RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM openjdk:21-jdk-slim AS runtime
WORKDIR /app

# Copy only the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the JAR
CMD ["java", "-jar", "app.jar"]


