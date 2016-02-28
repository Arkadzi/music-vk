package me.gumenniy.arkadiy.vkmusic.pojo;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class AudioResult {
    private VKError error;
    private VKAudioResponse response;

    public AudioResult(VKError error) {
        this.error = error;
    }
    public AudioResult(VKAudioResponse response) {
        this.response = response;
    }

    public VKAudioResponse getResponse() {
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
