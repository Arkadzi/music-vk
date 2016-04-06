package me.gumenniy.arkadiy.vkmusic.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class Paginator {

    public interface OnPaginateListener {
        void onPaginate();
    }

    private OnPaginateListener onPaginateListener;

    public void paginateListView(final RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int firstVisible;
            int mScrollState;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mScrollState = newState;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = recyclerView.getAdapter().getItemCount();

                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = Math.abs(firstVisibleItem - layoutManager.findLastVisibleItemPosition());
                Log.e("paginator", String.format("%d %d %d %d", firstVisibleItem, visibleItemCount, totalItemCount, mScrollState));
                if ((totalItemCount - visibleItemCount - 1) == (firstVisibleItem)
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
