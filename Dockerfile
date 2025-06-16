# Step 1: Build
FROM maven:3.9.6-eclipse-temurin AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests -X

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
    pip3 install --no-cache-dir yt-dlp --break-system-packages && \
    echo "----- DEBUG: Testing yt-dlp directly -----" && \
    yt-dlp -f 140 -j "https://www.youtube.com/watch?v=dQw4w9WgXcQ" || true && \
    echo "----- END DEBUG -----" && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


WORKDIR /app
ADD https://raw.githubusercontent.com/Veeraj12/SpringBootBasedMusicPlayer/main/cookies.txt /app/cookies.txt
COPY cookies.txt /app/cookies.txt
RUN echo "Checking cookies.txt contents:" && cat /app/cookies.txt
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
