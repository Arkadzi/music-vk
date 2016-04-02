package me.gumenniy.arkadiy.vkmusic.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.adapter.SongAdapter;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.SearchPresenter;

/**
 * Created by Arkadiy on 30.03.2016.
 */
public class SearchFragment extends BaseListFragment<Song, SearchPresenter> implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private SearchView searchView;

    public static Fragment newInstance(String title) {
        SearchFragment fragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putString(TITLE,  title);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getArguments().getString(TITLE));
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);
        menu.findItem(R.id.action_performer).setChecked(presenter.isOnlyPerformer());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_performer) {
            item.setChecked(!item.isChecked());
            presenter.onCheckableMenuItemClicked(item.isChecked());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void inject(RestComponent component) {
        component.inject(this);
    }

    @Override
    protected AbstractListAdapter<Song> getListAdapter() {
        return new SongAdapter(getActivity(), new ArrayList<Song>());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        presenter.onSearchSubmit(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e("search fragment", "new text " + newText);
        if (newText.isEmpty()) {
            presenter.onSearchSubmit(newText);
        }
        return false;
    }

    @Override
    public boolean onClose() {
        Log.e("SearchFragment", "onClose()");
        return false;
    }

    @Override
    public boolean backPressHandled() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return true;
        }
        return super.backPressHandled();
    }
}
