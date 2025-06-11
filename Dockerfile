# 🛠️ Step 1: Build Stage - Use Maven to build the JAR
FROM maven:3.9.6-eclipse-temurin AS builder

# Set working directory
WORKDIR /app

# Copy only pom.xml first for caching dependencies
COPY pom.xml .

# Download dependencies first (faster rebuilds)
RUN mvn dependency:go-offline

# Now copy the rest of the source code
COPY . .

# Build the application (skip tests to save time)
RUN mvn clean package -DskipTests

# 🏃‍♂️ Step 2: Run Stage - Use lightweight JDK to run the JAR
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the generated JAR from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the app port (change if needed)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
