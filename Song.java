package com.example.powerplayer;

import android.net.Uri;

public class Song {
    private String title;
    private String artist;
    private long duration;
    private Uri uri;

    public Song(String title, String artist, long duration, Uri uri) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.uri = uri;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public long getDuration() { return duration; }
    public Uri getUri() { return uri; }
}
