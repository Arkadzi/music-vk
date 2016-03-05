package me.gumenniy.arkadiy.vkmusic.rest.model;

import java.util.List;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class VKResponse<T> {
    private int count;
    private List<T> items;

    public VKResponse(int count, List<T> items) {
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
