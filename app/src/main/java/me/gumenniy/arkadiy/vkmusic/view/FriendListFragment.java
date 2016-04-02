package me.gumenniy.arkadiy.vkmusic.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.adapter.FriendAdapter;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.presenter.FriendListPresenter;

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
    protected void inject(RestComponent component) {
        component.inject(this);
    }

    @Override
    protected AbstractListAdapter<Friend> getListAdapter() {
        return new FriendAdapter(getActivity(), new ArrayList<Friend>());
    }

    @Override
    public void navigateBy(Friend item) {
        String userId = String.valueOf(item.getId());
        String title = String.format("%s %s", item.getFirstName(), item.getLastName());

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SongListFragment.newInstance(userId, title, false))
                .addToBackStack(null)
                .commit();
    }
}
