package me.gumenniy.arkadiy.vkmusic.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.adapter.SongAdapter;
import me.gumenniy.arkadiy.vkmusic.app.dialogs.SongDialogFragment;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.SongListPresenter;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public class SongListFragment extends BaseListFragment<Song, SongListPresenter> {
    private static final String USER_ID = "user_id";
    private static final String RECOMMENDED = "recommended";

    public static Fragment newInstance(String userId, String title, boolean recommended) {
        SongListFragment fragment = new SongListFragment();

        Bundle args = new Bundle();
        args.putBoolean(RECOMMENDED, recommended);
        args.putString(USER_ID, userId);
        args.putString(TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void inject(RestComponent component) {
        component.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getArguments().getString(TITLE));
    }

    @Nullable
    @Override
    protected DialogFragment getMenuDialog(int item) {
        return SongDialogFragment.newInstance(item);
    }

    @Override
    protected boolean isHandleLongClick() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String userId = getArguments().getString(USER_ID, SongListPresenter.CURRENT_USER);
        boolean recommended = getArguments().getBoolean(RECOMMENDED);
        getPresenter().setUserId(userId,  recommended);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected AbstractListAdapter<Song> getListAdapter() {
        return new SongAdapter(getActivity(), new ArrayList<Song>());
    }
}
