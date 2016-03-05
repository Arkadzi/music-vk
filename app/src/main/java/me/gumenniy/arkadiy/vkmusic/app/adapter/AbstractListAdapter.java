package me.gumenniy.arkadiy.vkmusic.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
        if (view == null) {
            view = inflater.inflate(getLayoutId(), parent, false);
        }

        T item = getItem(position);
        String[] text = getText(item);
        int[] textViewIds = getTextViewIds();
        for (int i = 0; i < textViewIds.length ; i++) {
            TextView textView = (TextView) view.findViewById(textViewIds[i]);
            textView.setText(text[i]);
        }

        return view;
    }

    protected abstract int[] getTextViewIds();

    protected abstract int getLayoutId();

    protected abstract String[] getText(T item);

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }
}
