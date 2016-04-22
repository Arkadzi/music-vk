package me.gumenniy.arkadiy.vkmusic.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.MainActivity;
import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.adapter.SongAdapter;
import me.gumenniy.arkadiy.vkmusic.app.dialogs.SearchDialogFragment;
import me.gumenniy.arkadiy.vkmusic.app.dialogs.SongDialogFragment;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.SearchPresenter;

/**
 * Created by Arkadiy on 30.03.2016.
 */
public class SearchFragment extends BaseListFragment<Song, SearchPresenter> implements SearchView.OnQueryTextListener {

    private SearchView searchView;

    public static Fragment newInstance(String title) {
        SearchFragment fragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getArguments().getString(TITLE));
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    protected DialogFragment getMenuDialog(int item) {
        return SearchDialogFragment.newInstance(item);
    }

    @Override
    protected boolean isHandleLongClick() {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery(presenter.getQuery(),false);
            }
        });
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return !((MainActivity) getActivity()).handleBackPress();
            }
        });

        menu.findItem(R.id.action_performer).setChecked(presenter.isOnlyPerformer());
        searchView.setOnQueryTextListener(SearchFragment.this);
    }

    public void clearFocus() {
        if (searchView != null && searchView.hasFocus()) {
            searchView.clearFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_performer) {
            item.setChecked(!item.isChecked());
            String query = searchView.getQuery().toString();
            if (searchView.isIconified()) {
                query = presenter.getQuery();
            }
            presenter.onSearchSubmit(query, item.isChecked());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        presenter.onSearchSubmit(query, presenter.isOnlyPerformer());
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

    @Override
    protected void inject(RestComponent component) {
        component.inject(this);
    }

    @Override
    protected AbstractListAdapter<Song> getListAdapter() {
        return new SongAdapter(getActivity(), new ArrayList<Song>());
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
