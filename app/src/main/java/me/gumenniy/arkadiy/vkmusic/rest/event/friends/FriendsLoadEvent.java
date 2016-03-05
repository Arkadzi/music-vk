package me.gumenniy.arkadiy.vkmusic.rest.event.friends;

import me.gumenniy.arkadiy.vkmusic.rest.event.DataLoadEvent;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class FriendsLoadEvent extends DataLoadEvent {
    public FriendsLoadEvent(boolean refresh) {
        super(refresh);
    }
}
