package me.gumenniy.arkadiy.vkmusic.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class Song {
    private String id;
    private String title;
    private String artist;
    private String url;
    private int duration;

    public Song() {

    }

    public Song(String id, String title, String artist, String url, int duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.duration = duration;
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
}
