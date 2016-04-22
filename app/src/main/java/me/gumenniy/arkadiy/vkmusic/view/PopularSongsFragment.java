package me.gumenniy.arkadiy.vkmusic.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.adapter.SongAdapter;
import me.gumenniy.arkadiy.vkmusic.app.dialogs.PopularSongDialogFragment;
import me.gumenniy.arkadiy.vkmusic.app.dialogs.SongDialogFragment;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.PopularSongsPresenter;

/**
 * Created by Arkadiy on 30.03.2016.
 */
public class PopularSongsFragment extends BaseListFragment<Song, PopularSongsPresenter> {

    private AlertDialog dialog;

    public static Fragment newInstance(String title) {
        PopularSongsFragment fragment = new PopularSongsFragment();

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        menu.findItem(R.id.only_eng_item).setChecked(presenter.isOnlyForeign());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.only_eng_item) {
            item.setChecked(!item.isChecked());
            presenter.onCheckableMenuItemClicked(item.isChecked());
        } else if (id == R.id.genre_item) {
            showGenreDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    protected DialogFragment getMenuDialog(int item) {
        return PopularSongDialogFragment.newInstance(item);
    }

    @Override
    protected boolean isHandleLongClick() {
        return true;
    }

    private void showGenreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_genre);
        builder.setItems(R.array.genres, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int[] ids = getResources().getIntArray(R.array.genre_ids);
                presenter.onMenuItemClicked(ids[which]);
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroyView();
    }

    @Override
    protected void inject(RestComponent component) {
        component.inject(this);
    }

    @Override
    protected AbstractListAdapter<Song> getListAdapter() {
        return new SongAdapter(getActivity(), new ArrayList<Song>());
    }
}
