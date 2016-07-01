package me.gumenniy.arkadiy.vkmusic.data.rest.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class VKListResult<T> extends VKResult<VKListResponse<T>> {
    private List<T> altResponse;

    public VKListResult(VKListResponse<T> response) {
        setResponse(response);
    }

    @Override
    public boolean isSuccessful() {
        return super.isSuccessful() || altResponse != null;
    }

    @NotNull
    public List<T> getData() {
        if (getResponse() != null) {
            return getResponse().getItems();
        } else if (altResponse != null) {
            return altResponse;
        } else {
            return new ArrayList<>();
        }
    }

    public int getCount() {
        if (getResponse() != null) {
            return getResponse().getCount();
        } else if (altResponse != null) {
            return Short.MAX_VALUE;
        } else {
            return 0;
        }
    }
}
