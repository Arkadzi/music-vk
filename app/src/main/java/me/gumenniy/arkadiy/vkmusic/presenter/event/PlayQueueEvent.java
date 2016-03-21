package me.gumenniy.arkadiy.vkmusic.presenter.event;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Song;

/**
 * Created by Arkadiy on 18.03.2016.
 */
public class PlayQueueEvent {
    public List<Song> queue;
    public int position;

    public PlayQueueEvent(List<Song> queue, int position) {
        this.queue = queue;
        this.position = position;
    }
}
