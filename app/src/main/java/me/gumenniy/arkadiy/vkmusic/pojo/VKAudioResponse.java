package me.gumenniy.arkadiy.vkmusic.pojo;

import java.util.List;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class VKAudioResponse {
    private int count;
    private List<Song> items;

    public VKAudioResponse(int count, List<Song> items) {
        this.count = count;
        this.items = items;
    }

    public List<Song> getItems() {
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
