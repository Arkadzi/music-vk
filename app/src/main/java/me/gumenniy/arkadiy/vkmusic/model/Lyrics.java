package me.gumenniy.arkadiy.vkmusic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Arkadiy on 20.05.2016.
 */
public class Lyrics {
    @SerializedName("lyrics_id")
    private String lyricsId;
    private String text;

    public Lyrics(String lyricsId, String text) {
        this.lyricsId = lyricsId;
        this.text = text;
    }

    public String getLyricsId() {
        return lyricsId;
    }

    public String getText() {
        return text;
    }
}
