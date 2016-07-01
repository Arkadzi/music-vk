package me.gumenniy.arkadiy.vkmusic.data.rest.model;

/**
 * Created by Arkadiy on 20.05.2016.
 */
public class VKResult<T> {
    private T response;
    private VKError error;

    public T getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return String.valueOf(error) + " " + String.valueOf(response);
    }


    public VKError getError() {
        return error;
    }

    public boolean isSuccessful() {
        return error == null && response != null;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
