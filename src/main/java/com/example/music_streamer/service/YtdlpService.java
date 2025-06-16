package com.example.music_streamer.service;

import com.example.music_streamer.model.AudioResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class YtdlpService {

    public AudioResponse extractAudio(String url) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                "yt-dlp", "--no-warnings",
                "--cookies", "/app/cookies.txt",
                "--no-playlist",
                "--user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
                "--geo-bypass-country", "IN",
              "--print", "url", url
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String jsonLine = null;

            System.out.println("YT-DLP OUTPUT:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.trim().startsWith("{") && jsonLine == null) {
                    jsonLine = line.trim();
                }
            }

            // Wait with timeout
            boolean finished = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                throw new RuntimeException("yt-dlp timed out");
            }

            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errLine;
            System.out.println("YT-DLP ERR:");
            while ((errLine = err.readLine()) != null) {
                System.err.println(errLine);
            }

            if (jsonLine == null) {
                throw new RuntimeException("No valid JSON data found in yt-dlp output.");
            }

            System.out.println("Clean JSON: " + jsonLine);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonLine);

            System.out.println("Reading cookies from: /app/cookies.txt");
            File cookieFile = new File("/app/cookies.txt");
            System.out.println("Exists? " + cookieFile.exists());
            System.out.println("Size: " + cookieFile.length());

            // Extract metadata
            String title = root.has("title") ? root.get("title").asText() : "Unknown Title";
            String uploader = root.has("uploader") ? root.get("uploader").asText() : "Unknown";
            int duration = root.has("duration") ? root.get("duration").asInt() : 0;
            String thumbnail = root.has("thumbnail") ? root.get("thumbnail").asText() : "";

            // Extract audio URL from formats
            String audioUrl = null;
            JsonNode formats = root.get("formats");

            if (formats != null && formats.isArray()) {
                for (JsonNode format : formats) {
                    String formatId = format.has("format_id") ? format.get("format_id").asText() : "";
                    String ext = format.has("ext") ? format.get("ext").asText() : "";
                    String acodec = format.has("acodec") ? format.get("acodec").asText() : "";
                    boolean hasVideo = format.has("vcodec") && !format.get("vcodec").asText().equals("none");

                    // Find audio-only formats (no video)
                    if (!hasVideo && format.has("url")) {
                        audioUrl = format.get("url").asText();
                        System.out.println("Selected format_id: " + formatId + ", ext: " + ext);
                        break;
                    }
                }
            }

            if (audioUrl == null) {
                throw new RuntimeException("No audio-only format found.");
            }

            System.out.println("Here At end of service");
            return new AudioResponse(title, uploader, duration, thumbnail, audioUrl);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to extract audio: " + e.getMessage());
        }
    }
}
