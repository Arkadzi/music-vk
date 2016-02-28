package me.gumenniy.arkadiy.vkmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.pojo.Song;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public class SongAdapter extends BaseAdapter {

    private List<Song> mSongs;
    private final LayoutInflater inflater;

    public SongAdapter(Context context, List<Song> songs) {
        mSongs = songs;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }

    @Override
    public Song getItem(int position) {
        return mSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.icon_list_item, parent, false);
        }

        Song item = getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.main);
        TextView textView2 = (TextView) view.findViewById(R.id.secondary);
        TextView textView3 = (TextView) view.findViewById(R.id.third);

        textView.setText(item.getTitle());
        textView2.setText(item.getArtist());
        textView3.setText(formatMillis(item.getDuration()));

        return view;
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

    public void setSongs(List<Song> songs) {
        mSongs = songs;
        notifyDataSetChanged();
    }
}
