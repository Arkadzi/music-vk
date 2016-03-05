package me.gumenniy.arkadiy.vkmusic.rest.event.friends;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataLoadedEvent;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class FriendsLoadedEvent extends DataLoadedEvent<Friend> {
    public FriendsLoadedEvent(List<Friend> data, boolean isLoading) {
        super(data, isLoading);
    }
}
