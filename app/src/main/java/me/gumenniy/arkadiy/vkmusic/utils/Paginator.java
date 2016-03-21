package me.gumenniy.arkadiy.vkmusic.utils;

import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class Paginator {
    public interface OnPaginateListener {
        void onPaginate();
    }
    private int mScrollState;
    private OnPaginateListener onPaginateListener;

    public void paginateListView(ListView listView) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int firstVisible;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if ((totalItemCount - visibleItemCount) == (firstVisibleItem)
                        && mScrollState != 0
                        && firstVisibleItem > firstVisible) {
                    if (onPaginateListener != null) {
                        onPaginateListener.onPaginate();
                    }
                }
                firstVisible = firstVisibleItem;
            }
        });
    }

    public void setOnPaginateListener(OnPaginateListener onPaginateListener) {
        this.onPaginateListener = onPaginateListener;
    }
}
