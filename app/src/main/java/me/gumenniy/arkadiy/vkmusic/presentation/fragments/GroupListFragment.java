package me.gumenniy.arkadiy.vkmusic.presentation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.presentation.adapters.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.presentation.adapters.GroupAdapter;
import me.gumenniy.arkadiy.vkmusic.injection.ApplicationComponent;
import me.gumenniy.arkadiy.vkmusic.domain.model.Group;
import me.gumenniy.arkadiy.vkmusic.presentation.presenter.GroupListPresenter;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public class GroupListFragment extends BaseListFragment<Group, GroupListPresenter> {

    public static Fragment newInstance(String title) {
        GroupListFragment fragment = new GroupListFragment();

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

    @Override
    protected AbstractListAdapter<Group> getListAdapter() {
        return new GroupAdapter(getActivity(), new ArrayList<Group>());
    }

    @Override
    public void navigateBy(@NonNull Group item) {
        String groupId = String.format("-%s", item.getId());
        String title = item.getName();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SongListFragment.newInstance(groupId, title, false))
                .addToBackStack(null)
                .commit();
    }

}
