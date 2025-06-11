# Use lightweight JDK image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the already built JAR into the container
COPY target/app app.jar

# Run the app
CMD ["java", "-jar", "app.jar"]
