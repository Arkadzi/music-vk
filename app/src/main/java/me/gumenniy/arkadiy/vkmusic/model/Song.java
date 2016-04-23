package me.gumenniy.arkadiy.vkmusic.model;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class Song {
    public static final String LOCAL_ID = "0";
    private String id;
    private String title;
    private String artist;
    private String url;
    private int duration;
    @SerializedName("owner_id")
    private String ownerId;

    public Song() {

    }

    public Song(String id, String title, String artist, String url, int duration, String ownerId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.duration = duration;
        this.ownerId = ownerId;
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
}
