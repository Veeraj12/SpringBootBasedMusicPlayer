# Use OpenJDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the jar file (you must build it first)
COPY target/*.jar app.jar

# Set the command to run your app
CMD ["java", "-jar", "app.jar"]
