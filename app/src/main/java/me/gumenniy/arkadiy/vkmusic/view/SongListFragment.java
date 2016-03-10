package me.gumenniy.arkadiy.vkmusic.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.adapter.SongAdapter;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.BaseListPresenter;
import me.gumenniy.arkadiy.vkmusic.presenter.SongListPresenter;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public class SongListFragment extends BaseListFragment<Song, SongListPresenter> {
    private static final String USER_ID = "user_id";

    @Override
    protected void inject(RestComponent component) {
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getPresenter().setUserId(getArguments().getString(USER_ID, SongListPresenter.CURRENT_USER));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected AbstractListAdapter<Song> getListAdapter() {
        return new SongAdapter(getActivity(), new ArrayList<Song>());
    }

    public static Fragment newInstance(String userId) {
        SongListFragment fragment = new SongListFragment();

        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        fragment.setArguments(args);

        return fragment;
    }

}
