# 🛠️ Step 1: Build Stage - Use Maven to build the JAR
FROM maven:3.9.6-eclipse-temurin AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# 🚀 Step 2: Runtime with yt-dlp
FROM eclipse-temurin:21-jdk

# Install Python and yt-dlp inside final image
RUN apt-get update && \
    apt-get install -y python3 python3-pip ffmpeg && \
    pip3 install yt-dlp && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8027

ENTRYPOINT ["java", "-jar", "app.jar"]
