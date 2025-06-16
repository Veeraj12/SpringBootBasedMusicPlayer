package com.example.music_streamer.service;

import com.example.music_streamer.model.AudioResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class YtdlpService {
	public AudioResponse extractAudio(String url) {
	    try {
		    ProcessBuilder builder = new ProcessBuilder(
		    "yt-dlp","--dump-json","--no-warnings",
		    "--cookies", "/app/cookies.txt",
		    "-j",
		    "--no-playlist",
		    url
		);
	        // ProcessBuilder builder = new ProcessBuilder(
	        //         "yt-dlp",
	        //         "-j",
	        //         "--no-playlist",
	        //         url
	        // );

	        builder.redirectErrorStream(true);
	        Process process = builder.start();
		 
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        StringBuilder jsonBuilder = new StringBuilder();
	        String line;
	        String jsonLine = null;
	        while ((line = reader.readLine()) != null) {
	            // Skip any lines that don't start with '{' (likely warnings)
	            if (line.trim().startsWith("{")) {
	                jsonLine = line.trim();
	                break; // JSON is usually a single line
	            }
	        }
	           
		 // Wait with timeout
	        boolean finished = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
	        if (!finished) {
	            process.destroy();
	            throw new RuntimeException("yt-dlp timed out");
	        }
		   

	        if (jsonLine == null) {
	            throw new RuntimeException("No valid JSON data found in yt-dlp output.");
	        }
	        
	        String json = jsonBuilder.toString();
	        System.out.println("Clean JSON: " + json);

	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode root = mapper.readTree(jsonLine);
	        
	        // Extract fields
	        String title = root.get("title").asText();
	        String uploader = root.get("uploader").asText("");
	        int duration = root.has("duration") ? root.get("duration").asInt() : 0;
	        String thumbnail = root.get("thumbnail").asText("");
	        String audioUrl = root.get("url").asText();  // Direct stream URL

	        System.out.println("Here At end of service");
	        return new AudioResponse(title, uploader, duration, thumbnail, audioUrl);

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Failed to extract audio: " + e.getMessage());
	    }
	}

}
