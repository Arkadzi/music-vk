package me.gumenniy.arkadiy.vkmusic.rest.model;

/**
 * Created by Arkadiy on 21.04.2016.
 */
public class VKAddRemoveResult {
    private VKError error;
    private long response;

    public VKError getError() {
        return error;
    }

    public long getResponse() {
        return response;
    }

    public boolean isSuccessful() {
        return error == null;
    }
}
