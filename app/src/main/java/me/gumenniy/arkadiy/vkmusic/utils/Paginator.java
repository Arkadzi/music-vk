package me.gumenniy.arkadiy.vkmusic.utils;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Arkadiy on 25.02.2016.
 */
public class Paginator {
    private Runnable pagingRunnable;
    private int mScrollState;

    public Paginator(Runnable runnable) {
        pagingRunnable = runnable;
    }

    public void paginate(ListView listView) {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;
                Log.e("Paginator", String.valueOf(mScrollState));
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((totalItemCount - visibleItemCount) == (firstVisibleItem) && mScrollState != 0) {
                    pagingRunnable.run();
                }
            }
        });
    }
}
