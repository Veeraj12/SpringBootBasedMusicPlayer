package com.example.music_streamer.controller;

import com.example.music_streamer.model.AudioResponse;
import com.example.music_streamer.service.YtdlpService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class YtdlpController {

    private final YtdlpService ytdlpService;

    public YtdlpController(YtdlpService ytdlpService) {
        this.ytdlpService = ytdlpService;
        System.out.println("In controller 1");
    }
    
    
    @GetMapping("/info")
    public ResponseEntity<AudioResponse> getAudioInfo(@RequestParam String youtubeUrl) throws IOException {
        AudioResponse audio = ytdlpService.extractAudio(youtubeUrl);
        System.out.println("getting the Audio");
		return ResponseEntity.ok(audio);
    }

    
//    @GetMapping("/stream")
//    public void stream(@RequestParam String url, HttpServletResponse response) throws IOException {
//        URL decodedUrl = new URL(URLDecoder.decode(url, StandardCharsets.UTF_8));
//        response.setContentType("audio/mpeg");
//        response.setHeader("Content-Disposition", "inline; filename=\"audio.mp3\"");
//        response.setHeader("Accept-Ranges", "bytes");
//        try (InputStream in = decodedUrl.openStream(); 
//        		OutputStream out = response.getOutputStream()) {
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = in.read(buffer)) != -1) {
//                out.write(buffer, 0, bytesRead); // fixed typo here: it should be (buffer, 0, bytesRead)
//            }
//        }
//    }

    @GetMapping("/stream")
    public void streamAudio(@RequestParam String url, HttpServletResponse response) {
        try (InputStream in = new URL(url).openStream()) {
            response.setContentType("audio/mp4"); // For itag=140 (M4A)
            StreamUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
