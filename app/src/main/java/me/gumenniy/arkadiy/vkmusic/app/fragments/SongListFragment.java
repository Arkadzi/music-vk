package me.gumenniy.arkadiy.vkmusic.app.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.app.adapter.SongAdapter;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.songs.SongsLoadEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.songs.SongsLoadedEvent;

/**
 * Created by Arkadiy on 18.02.2016.
 */
public class SongListFragment extends AbstractListFragment<Song> {

    public static SongListFragment newInstance() {
        SongListFragment fragment = new SongListFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected Object getDataLoadEvent(boolean refresh) {
        return new SongsLoadEvent(refresh);
    }

    @Override
    protected void onClick(Song item) {

    }

    @Override
    protected SongAdapter getAdapter(List<Song> data) {
        return new SongAdapter(getActivity(), data);
    }

    @Override
    protected DataLoadedEvent<Song> removeStickyEvent(EventBus bus) {
        return bus.getStickyEvent(SongsLoadedEvent.class);
    }

}
