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
		    "--no-playlist","--user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) ...",
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
		String line;
		String jsonLine = null;
		
		System.out.println("YT-DLP OUTPUT:");
		while ((line = reader.readLine()) != null) {
		    System.out.println(line);
		    if (line.trim().startsWith("{") && jsonLine == null) {
		        jsonLine = line.trim(); // get the first JSON line
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
