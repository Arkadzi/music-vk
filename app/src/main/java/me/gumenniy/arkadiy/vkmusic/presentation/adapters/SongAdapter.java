package me.gumenniy.arkadiy.vkmusic.presentation.adapters;

import android.content.Context;

import java.util.List;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.domain.model.Song;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class SongAdapter extends AbstractListAdapter<Song> {
    public SongAdapter(Context context, List<Song> data) {
        super(context, data);
    }

    @Override
    protected int[] getTextViewIds() {
        return new int[] {R.id.main, R.id.secondary, R.id.third};
    }

    @Override
    protected int getLayoutId() {
        return R.layout.iconless_list_item;
    }

    @Override
    protected String[] getText(Song item) {
        return new String[] {item.getTitle(), item.getArtist(), formatMillis(item.getDuration())};
    }

    public String formatMillis(long seconds) {
        long minutes = seconds / 60 ;
        long hours = minutes/60;
        seconds %= 60;
        minutes %= 60;

        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(String.format("%d:", hours));
        }
        builder.append(String.format("%02d:", minutes));
        builder.append(String.format("%02d", seconds));
        return builder.toString();
    }
}
