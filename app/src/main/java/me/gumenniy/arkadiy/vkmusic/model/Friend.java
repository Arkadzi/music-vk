package me.gumenniy.arkadiy.vkmusic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class Friend {
    private long id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("photo_50")
    private String photo;

    public Friend() {

    }

    public Friend(long id, String firstName, String lastName, String photo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoto() {
        return photo;
    }
}
