package me.gumenniy.arkadiy.vkmusic.app.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.List;

import javax.inject.Inject;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.SearchPresenter;
import me.gumenniy.arkadiy.vkmusic.presenter.SongListPresenter;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;

/**
 * Created by Arkadiy on 21.04.2016.
 */
public class SearchDialogFragment extends DialogFragment {
    public static final String POSITION = "position";

    @Inject
    SearchPresenter presenter;

    public static DialogFragment newInstance(int i) {

        DialogFragment fragment = new SearchDialogFragment();

        Bundle args = new Bundle();
        args.putInt(POSITION, i);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicApplication.getApp(getActivity()).getComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final int position = getArguments().getInt(POSITION);
        List<Song> data = presenter.getData();
        final Song song = data.get(position);
        builder.setTitle(song.getTitle());
        builder.setItems(R.array.menu_song_new, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.handleMenuClick(Settings.Menu.Add, song, position);
            }
        });
        return builder.create();
    }
}