package me.gumenniy.arkadiy.vkmusic.rest.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class VKResult<T> {
    private VKError error;
    private VKResponse<T> response;
    private List<T> altResponse;

    public VKResult(VKError error) {
        this.error = error;
    }

    public VKResult(VKResponse<T> response) {
        this.response = response;
    }

    public boolean isSuccessful() {
        return error == null && (response != null || altResponse != null);
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


    @NotNull
    public List<T> getData() {
        if (response != null) {
            return response.getItems();
        } else if (altResponse != null) {
            return altResponse;
        } else {
            return new ArrayList<>();
        }
    }

    public int getCount() {
        if (response != null) {
            return response.getCount();
        } else if (altResponse != null) {
            return Short.MAX_VALUE;
        } else {
            return 0;
        }
    }
}
