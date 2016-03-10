package me.gumenniy.arkadiy.vkmusic.rest.model;

import me.gumenniy.arkadiy.vkmusic.rest.model.VKError;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResponse;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class VKResult<T> {
    private VKError error;
    private VKResponse<T> response;

    public boolean isSuccessful() {
        return error == null && response != null;
    }

    public VKResult(VKError error) {
        this.error = error;
    }
    public VKResult(VKResponse<T> response) {
        this.response = response;
    }

    public VKResponse<T> getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return String.valueOf(error) + " " + String.valueOf(response);
    }

    public VKError getError() {
        return error;
    }
}
