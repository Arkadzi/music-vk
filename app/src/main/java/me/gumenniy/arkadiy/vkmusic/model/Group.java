package me.gumenniy.arkadiy.vkmusic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class Group {
    private long id;
    private String name;
    @SerializedName("photo_50")
    private String photo;

    public Group() {

    }

    public Group(long id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }
}
