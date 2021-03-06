package me.gumenniy.arkadiy.vkmusic.data.rest.model;

import java.util.List;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class VKListResponse<T> {
    private int count;
    private List<T> items;

    public VKListResponse(int count, List<T> items) {
        this.count = count;
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "count = " + count;
    }
}
