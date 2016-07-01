package me.gumenniy.arkadiy.vkmusic.domain.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class Song implements Serializable {
    public static final String LOCAL_ID = "0";
    private String id;
    private String title;
    private String artist;
    private String url;
    private int duration;
    @SerializedName("owner_id")
    private String ownerId;
    @SerializedName("lyrics_id")
    private String lyricsId;

    public Song() {

    }


    public Song(String id, String title, String artist, String url, int duration, String lyricsId, String ownerId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.duration = duration;
        this.ownerId = ownerId;
        this.lyricsId = lyricsId;
    }

    public int getDuration() {
        return duration;
    }

    public String getArtist() {
        return artist;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public String getKey() {
        return String.format("%s%s", getTitle().toLowerCase(),getArtist().toLowerCase()).replace(" ", "");
    }

    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Song && ((Song) o).getKey().equals(getKey());
    }

    public String getLyricsId() {
        return lyricsId == null ? "0" : lyricsId;
    }

    public boolean hasLyrics() {
        return !getLyricsId().equals("0");
    }
}
