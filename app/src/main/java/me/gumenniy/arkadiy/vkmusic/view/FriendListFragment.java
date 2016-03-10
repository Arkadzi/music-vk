package me.gumenniy.arkadiy.vkmusic.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.adapter.FriendAdapter;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.presenter.FriendListPresenter;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public class FriendListFragment extends BaseListFragment<Friend, FriendListPresenter> {

    @Override
    protected void inject(RestComponent component) {
        component.inject(this);
    }

    @Override
    protected AbstractListAdapter<Friend> getListAdapter() {
        return new FriendAdapter(getActivity(), new ArrayList<Friend>());
    }

    public static Fragment newInstance() {
        FriendListFragment fragment = new FriendListFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }
}
