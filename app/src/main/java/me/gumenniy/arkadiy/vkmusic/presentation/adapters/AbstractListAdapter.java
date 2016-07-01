package me.gumenniy.arkadiy.vkmusic.presentation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public abstract class AbstractListAdapter<T> extends RecyclerView.Adapter<AbstractListAdapter.ViewHolder> {

    private final Context context;
    private List<T> mData;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public AbstractListAdapter(Context c, List<T> data) {
        mData = data;
        this.context = c;
    }

    protected void handleImageView(T item, ImageView imageView) {
    }

    public Context getContext() {
        return context;
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(getLayoutId(), parent, false);
        return new ViewHolder(v, getTextViewIds(), getImageViewId());
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        T item = mData.get(position);
        String[] text = getText(item);
        TextView[] textViews = holder.textViews;
        for (int i = 0; i < textViews.length; i++) {
            textViews[i].setText(text[i]);
        }
        if (holder.imageView != null) {
            handleImageView(item, holder.imageView);
        }

        setListeners(holder);
    }

    private void setListeners(final ViewHolder holder) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
        if (longClickListener != null) {
            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null) {
                        longClickListener.onItemLongClick(holder.getAdapterPosition());
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.view.setOnClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView[] textViews;
        ImageView imageView;
        View view;

        public ViewHolder(View view, int[] textViewIds, int imageViewId) {
            super(view);
            this.view = view;

            textViews = new TextView[textViewIds.length];
            imageView = (ImageView) view.findViewById(imageViewId);

            for (int i = 0; i < textViewIds.length; i++) {
                textViews[i] = (TextView) view.findViewById(textViewIds[i]);
            }

        }
    }
}
