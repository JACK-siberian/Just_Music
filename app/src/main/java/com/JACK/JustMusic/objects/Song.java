package com.JACK.JustMusic.objects;

import android.net.Uri;

import com.JACK.JustMusic.myUtil.MyUtil;

import java.io.Serializable;

public class Song implements Serializable{
    private long id;
    private String artist;
    private String title_song;
    private String album;
    private long duration;
    private String uri;

    public Song(  long id, String artist, String title, String album, long dur, String uri) {
        this.id = id;
        this.artist = artist;
        this.title_song = title;
        this.album = album;
        this.uri = uri;
        this.duration = dur;
    }

    public String getArtist() {
        return artist;
    }
    public String getTitle() {
        return title_song;
    }
    public String getAlbum() {
        return album;
    }
    public Uri getUri() {
        return Uri.parse(uri);
    }
    public long getDuration() {
        return duration;
    }
    public String getFormatDuration() {
        return MyUtil.formatTime(duration);
    }
}
