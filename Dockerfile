# ---------- Build Stage ----------
FROM maven:3.9.3-eclipse-temurin AS build
WORKDIR /app

# Copy the pom and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the code
COPY . .

# Package the app, skipping tests
RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jdk AS runtime
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Run the JAR
CMD ["java", "-jar", "app.jar"]
