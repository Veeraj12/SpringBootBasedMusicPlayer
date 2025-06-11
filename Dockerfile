# Step 1: Build
FROM maven:3.9.6-eclipse-temurin AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run + yt-dlp
FROM eclipse-temurin:21-jdk
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        python3 \
        python3-pip \
        ffmpeg \
        curl \
        ca-certificates && \
    pip3 install --no-cache-dir yt-dlp && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8027
ENTRYPOINT ["java", "-jar", "app.jar"]
