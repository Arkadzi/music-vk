package me.gumenniy.arkadiy.vkmusic.pojo;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class User {
    private String token;
    private String id;

    public User(String token, String id) {
        this.token = token;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public String getId() {
        return id;
    }
}
