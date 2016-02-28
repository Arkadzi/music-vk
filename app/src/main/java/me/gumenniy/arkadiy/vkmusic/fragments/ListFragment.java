package me.gumenniy.arkadiy.vkmusic.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import me.gumenniy.arkadiy.vkmusic.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.RequestTokenListener;
import me.gumenniy.arkadiy.vkmusic.adapter.SongAdapter;
import me.gumenniy.arkadiy.vkmusic.pojo.Song;
import me.gumenniy.arkadiy.vkmusic.rest.event.CancelLoadEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.SongsFailedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.SongsLoadEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.SongsLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.SongsNotLoadedEvent;
import me.gumenniy.arkadiy.vkmusic.rest.event.TokenRequiredEvent;
import me.gumenniy.arkadiy.vkmusic.utils.Paginator;

/**
 * Created by Arkadiy on 18.02.2016.
 */
public class ListFragment extends Fragment {

    private static final String PROGRESS = "PROGRESS";
    private List<Song> songs;
    private SongAdapter mAdapter;
    private Bus mBus;
    private RequestTokenListener listener;
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
    private SwipeRefreshLayout mSwipeRefresh;

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        songs = new ArrayList<>();

        mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        ListView mListView = (ListView) view.findViewById(R.id.list);

        mSwipeRefresh.setOnRefreshListener(refreshListener);
        mAdapter = new SongAdapter(getActivity(), songs);
        mListView.setAdapter(mAdapter);

        Paginator paginator = new Paginator(runnable);
        paginator.paginate(mListView);

        return view;
    }

    @Subscribe
    public void onSongsLoaded(SongsLoadedEvent event) {
        boolean isLoading = event.isLoading;
        List<Song> data = event.songs;

        Log.e("ListFragment", "loading " + isLoading + "data null " + (data == null) + "data songs " + (data == songs));
        if (isLoading) {
            showProgressBar();
        } else {
            hideProgressBar();
        }
        if (data == null) {
            if (!isLoading) {
                refresh();
            }
        } else if (data != songs) {
            songs = data;
            mAdapter.setSongs(songs);
        }
    }

    @Subscribe
    public void onSongsNotLoaded(SongsNotLoadedEvent event) {
        hideProgressBar();
        Toast.makeText(getActivity().getApplicationContext(), R.string.not_loaded, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onSongsFailure(SongsFailedEvent event) {
        hideProgressBar();
        Toast.makeText(getActivity().getApplicationContext(), R.string.failure, Toast.LENGTH_SHORT).show();
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
        mSwipeRefresh.setRefreshing(false);
        getArguments().putBoolean(PROGRESS, false);
    }

    @Subscribe
    public void onRequestToken(TokenRequiredEvent event) {
        if (listener != null) {
            listener.onRequest();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ListFragment", "onDestroy()");
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBus = ((MusicApplication) getActivity().getApplication()).getBus();
        listener = (RequestTokenListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        mBus = null;
    }

    private void paginateList() {
        boolean isLoading = getArguments().getBoolean(PROGRESS);
        if (!isLoading) {
            Log.e("ListFragment", "paginate");
            showProgressBar();
            mBus.post(new SongsLoadEvent(false));
        }
    }

    private void refresh() {
        Log.e("ListFragment", "refresh");
        showProgressBar();
        mAdapter.setSongs(new ArrayList<Song>());
        mBus.post(new SongsLoadEvent(true));
    }
}
