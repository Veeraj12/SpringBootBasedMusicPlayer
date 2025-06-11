package com.example.music_streamer.model;

public class AudioResponse {
    private String title;
    private String uploader;
    private int duration;
    private String thumbnail;
    private String audioUrl;

    public AudioResponse() {}

    public AudioResponse(String title, String uploader, int duration, String thumbnail, String audioUrl) {
        this.title = title;
        this.uploader = uploader;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.audioUrl = audioUrl;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUploader() { return uploader; }
    public void setUploader(String uploader) { this.uploader = uploader; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}
