package me.gumenniy.arkadiy.vkmusic.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.presentation.adapters.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.presentation.adapters.FriendAdapter;
import me.gumenniy.arkadiy.vkmusic.injection.ApplicationComponent;
import me.gumenniy.arkadiy.vkmusic.domain.model.Friend;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.FriendListPresenter;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public class FriendListFragment extends BaseListFragment<Friend, FriendListPresenter> {

    public static Fragment newInstance(String title) {
        FriendListFragment fragment = new FriendListFragment();

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getArguments().getString(TITLE));
    }

    @Override
    protected void inject(ApplicationComponent component) {
        component.inject(this);
    }

    @NonNull
    @Override
    protected AbstractListAdapter<Friend> getListAdapter() {
        return new FriendAdapter(getActivity(), new ArrayList<Friend>());
    }

    @Override
    public void navigateBy(@NonNull Friend item) {
        String userId = String.valueOf(item.getId());
        String title = String.format("%s %s", item.getFirstName(), item.getLastName());

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SongListFragment.newInstance(userId, title, false))
                .addToBackStack(null)
                .commit();
    }
}
