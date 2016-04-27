package me.gumenniy.arkadiy.vkmusic.app.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.app.adapters.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.adapters.SongAdapter;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.presenter.CachePresenter;
import me.gumenniy.arkadiy.vkmusic.presenter.SimpleBaseView;
import me.gumenniy.arkadiy.vkmusic.presenter.State;

/**
 * Created by Arkadiy on 22.04.2016.
 */
public class CacheFragment extends Fragment implements SimpleBaseView<Song>,AbstractListAdapter.OnItemClickListener,
        AbstractListAdapter.OnItemLongClickListener, OnBackPressListener {
    @Bind(R.id.list)
    RecyclerView recyclerView;
    @Bind(R.id.progress_bar)
    CircularProgressView progressBar;
    @Bind(R.id.empty_view)
    View emptyView;
    @Inject
    CachePresenter presenter;
    private AbstractListAdapter<Song> adapter;

    public static Fragment newInstance() {
        Fragment fragment = new CacheFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MusicApplication.getApp(getActivity()).getComponent().inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.loaded_songs);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_simple, container, false);

        ButterKnife.bind(this, view);
        initViews();
        presenter.bindView(this);

        return view;
    }

    private void initViews() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new SongAdapter(getActivity(), new ArrayList<Song>());
        adapter.setClickListener(this);
        adapter.setLongClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
        presenter.bindView(null);
    }

    @Override
    public void showMenu(int item) {

    }

    @Override
    public void showProgress(State state) {
        setVisibility(emptyView, false);
        setVisibility(recyclerView, false);
        setVisibility(progressBar, true);
    }

    @Override
    public void hideProgress() {
        setVisibility(recyclerView, true);
        setVisibility(emptyView, recyclerView.getAdapter().getItemCount() == 0);
        setVisibility(progressBar, false);
    }

    private void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void renderData(@NonNull List<Song> data) {
        adapter.setData(data);
    }

    @Override
    public void showMessage(@NonNull String s) {

    }

    @Override
    public void onItemClick(int position) {
        presenter.handleClick(position);
    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public boolean backPressHandled() {
        return false;
    }
}
