package me.gumenniy.arkadiy.vkmusic.data.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Arkadiy on 26.02.2016.
 */
public class VKError {

    @SerializedName("error_code")
    int errorCode;

    @SerializedName("error_msg")
    String errorMessage;

    public VKError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "error " + errorCode + ": " + errorMessage;
    }
}
