package me.gumenniy.arkadiy.vkmusic.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.PlaybackPresenter;

/**
 * Created by Arkadiy on 05.04.2016.
 */
public class ArtworkAdapter extends PagerAdapter {
    private final PlaybackPresenter presenter;
    private final LayoutInflater inflater;
    private final Context context;
    private List<Song> songs;

    public ArtworkAdapter(Context c, PlaybackPresenter presenter) {
        this.presenter = presenter;
        this.context = c;
        this.songs = new ArrayList<>();
        inflater = LayoutInflater.from(c);
    }

    @Nullable
    private Song getItem(int position) {
        return songs.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        Song item = getItem(position);
        View view = inflater.inflate(R.layout.artwork_item, collection, false);
        if (item != null) {
            view.setTag(item.getKey());
            Bitmap bitmap = presenter.askBitmap(item);
            String url = presenter.askUrl(item);
            ((TextView) view.findViewById(R.id.title)).setText(position + ") " + String.valueOf(item));
            if (bitmap != null) {
                updateView(view, bitmap);
            } else if (url != null) {
                updateView(view, url);
            }
            collection.addView(view);
        }
        return view;
    }

    public void updateView(View view, @NotNull Bitmap bitmap) {
        ((ImageView) view.findViewById(R.id.artwork)).setImageBitmap(bitmap);
    }

    public void updateView(View view, @NotNull String url) {
        ImageView viewById = (ImageView) view.findViewById(R.id.artwork);
        Picasso.with(context).load(url).placeholder(R.drawable.default_cover).into(viewById);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setData(List<Song> queue) {
        songs.clear();
        songs.addAll(queue);
        notifyDataSetChanged();
    }
}
