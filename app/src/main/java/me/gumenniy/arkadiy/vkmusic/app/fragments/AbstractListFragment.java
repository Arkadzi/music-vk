package me.gumenniy.arkadiy.vkmusic.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.app.RequestTokenListener;
import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataFailedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataNotLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.DataStartLoadEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.TokenRequiredEvent;
import me.gumenniy.arkadiy.vkmusic.utils.Paginator;

/**
 * Created by Arkadiy on 28.02.2016.
 */
public abstract class AbstractListFragment<T> extends Fragment {
    private static final String PROGRESS = "PROGRESS";
    private List<T> data;
    private AbstractListAdapter<T> mAdapter;
    private EventBus bus;
    private RequestTokenListener listener;
    private SwipeRefreshLayout mSwipeRefresh;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            paginateList();
        }
    };
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refresh();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        data = new ArrayList<>();

        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        ListView mListView = (ListView) view.findViewById(R.id.list);
        mSwipeRefresh.setOnRefreshListener(refreshListener);
        mAdapter = getAdapter(data);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data != null)
                    onClick(data.get(position));
            }
        });

        Paginator paginator = new Paginator(runnable);
        paginator.paginate(mListView);

        return view;
    }

    protected abstract void onClick(T item);

    protected abstract AbstractListAdapter<T> getAdapter(List<T> mData);

    @Subscribe
    public void onDataLoaded(DataLoadedEvent<T> event) {
        boolean isLoading = event.isLoading;
        List<T> data = event.data;

        Log.e("AbstractListFragment", "loading " + isLoading + "data null " + (data == null) + "data this.data " + (data == this.data));
        if (isLoading) {
            showProgressBar();
        } else {
            hideProgressBar();
        }
        if (data == null) {
            if (!isLoading) {
                refresh();
            }
        } else if (data != this.data) {
            this.data = data;
            mAdapter.setData(this.data);
        }
    }


    @Subscribe
    public void onDataNotLoaded(DataNotLoadedEvent event) {
        hideProgressBar();
        Toast.makeText(getActivity().getApplicationContext(), R.string.not_loaded, Toast.LENGTH_SHORT).show();
    }


    @Subscribe
    public void onDataFailure(DataFailedEvent event) {
        hideProgressBar();
        Toast.makeText(getActivity().getApplicationContext(), R.string.failure, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onDataStartLoad(DataStartLoadEvent event) {
        showProgressBar();
    }

    @Subscribe
    public void onRequestToken(TokenRequiredEvent event) {
        if (listener != null) {
            listener.onRequest();
        }
    }

    private void showProgressBar() {
        if (!mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefresh.setRefreshing(true);
                }
            });
        }
        getArguments().putBoolean(PROGRESS, true);
    }

    private void hideProgressBar() {
        mSwipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(false);
            }
        });
        getArguments().putBoolean(PROGRESS, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        DataLoadedEvent<T> stickyEvent = removeStickyEvent(bus);
        if (stickyEvent == null) {
            stickyEvent = new DataLoadedEvent<>(null, false);
        }
        onDataLoaded(stickyEvent);
    }

    protected abstract DataLoadedEvent<T> removeStickyEvent(EventBus bus);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bus = ((MusicApplication) getActivity().getApplication()).getBus();
        listener = (RequestTokenListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        bus = null;
    }

    private void paginateList() {
        boolean isLoading = getArguments().getBoolean(PROGRESS);
        if (!isLoading) {
            Log.e("SongListFragment", "paginate");
            bus.post(getDataLoadEvent(false));
        }
    }

    protected abstract Object getDataLoadEvent(boolean refresh);

    private void refresh() {
        Log.e("SongListFragment", "refresh");
        mAdapter.setData(new ArrayList<T>());
        bus.post(getDataLoadEvent(true));
    }


}
