package me.gumenniy.arkadiy.vkmusic.domain.model;

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
    @SerializedName("photo_100")
    private String photo;
    @SerializedName("can_see_audio")
    private int isAudioAvailable;



    public Friend() {

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

    public boolean isAudioAvailable() {
        return isAudioAvailable == 1;
    }
}
