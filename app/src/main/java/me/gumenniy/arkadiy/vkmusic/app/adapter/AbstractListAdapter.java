package me.gumenniy.arkadiy.vkmusic.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public abstract class AbstractListAdapter<T> extends BaseAdapter {

    private List<T> mData;
    private final LayoutInflater inflater;

    public AbstractListAdapter(Context context, List<T> data) {
        mData = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        T item = getItem(position);
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(getLayoutId(), parent, false);

            int[] textViewIds = getTextViewIds();
            TextView[] textViews = new TextView[textViewIds.length];
            ImageView imageView = (ImageView) view.findViewById(getImageViewId());

            for (int i = 0; i < textViewIds.length ; i++) {
                textViews[i] = (TextView) view.findViewById(textViewIds[i]);
            }
            holder = new ViewHolder(textViews, imageView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String[] text = getText(item);
        TextView[] textViews = holder.textViews;
        for (int i = 0; i < textViews.length ; i++) {
            textViews[i].setText(text[i]);
        }
        if (holder.imageView != null) {
            handleImageView(holder.imageView);
        }

        return view;
    }

    private void handleImageView(ImageView imageView) {

    }

    protected int getImageViewId() {
        return -1;
    }

    protected abstract int[] getTextViewIds();

    protected abstract int getLayoutId();

    protected abstract String[] getText(T item);

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView[] textViews;
        ImageView imageView;

        public ViewHolder(TextView[] textViews, ImageView imageView) {
            this.textViews = textViews;
            this.imageView = imageView;
        }
    }
}
