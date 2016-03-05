package me.gumenniy.arkadiy.vkmusic.app.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.adapter.FriendAdapter;
import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.friends.FriendsLoadEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.friends.FriendsLoadedEvent;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public class FriendListFragment extends AbstractListFragment<Friend> {

    public static FriendListFragment newInstance() {
        FriendListFragment fragment = new FriendListFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected Object getDataLoadEvent(boolean refresh) {
        return new FriendsLoadEvent(refresh);
    }

    @Override
    protected void onClick(Friend item) {

    }

    @Override
    protected FriendAdapter getAdapter(List<Friend> mData) {
        return new FriendAdapter(getActivity(), mData);
    }

    @Override
    protected DataLoadedEvent<Friend> removeStickyEvent(EventBus bus) {
        return bus.getStickyEvent(FriendsLoadedEvent.class);
    }

}
