package me.gumenniy.arkadiy.vkmusic.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.LoginActivity;
import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.presenter.BaseListPresenter;
import me.gumenniy.arkadiy.vkmusic.presenter.BaseView;
import me.gumenniy.arkadiy.vkmusic.utils.Errors;
import me.gumenniy.arkadiy.vkmusic.utils.Paginator;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public abstract class BaseListFragment<D, P extends BaseListPresenter<D>> extends Fragment
        implements BaseView<List<D>>, Paginator.OnPaginateListener,
        SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener {

    @Bind(R.id.list) ListView listView;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.bottom_progress_bar) View bottomProgressBar;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeLayout;
    @Inject P presenter;
    private AbstractListAdapter<D> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(MusicApplication.getApp(getActivity()).getComponent());
        Log.e("BaseListFragment", String.valueOf(presenter));
    }

    protected abstract void inject(RestComponent component);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ButterKnife.bind(this, view);
        initViews();
        presenter.bindView(this);

        return view;
    }

    private void initViews() {
        listView.setOnItemClickListener(this);
        adapter = getListAdapter();
        listView.setAdapter(adapter);
        Paginator paginator = new Paginator();
        paginator.setOnPaginateListener(this);
        paginator.paginateListView(listView);
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        presenter.bindView(null);
    }

    protected abstract AbstractListAdapter<D> getListAdapter();

    public P getPresenter() {
        return presenter;
    }

    @Override
    public void showProgress(int state) {
        setVisibility(listView, state == BaseListPresenter.STATE_PAGINATE);
        setVisibility(progressBar, state == BaseListPresenter.STATE_FIRST_LOAD);
        setVisibility(bottomProgressBar, state == BaseListPresenter.STATE_PAGINATE);
        setRefreshing(state == BaseListPresenter.STATE_REFRESH);
    }

    @Override
    public void hideProgress() {
        setVisibility(listView, true);
        setVisibility(progressBar, false);
        setVisibility(bottomProgressBar, false);
        setRefreshing(false);
    }

    private void setRefreshing(final boolean shouldRefresh) {
        if (swipeLayout.isRefreshing() != shouldRefresh) {
            swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(shouldRefresh);
                }
            });
        }
    }

    @Override
    public void showError(String error) {
        Integer errorId = Errors.get(error);
        if (errorId != null) {
            error = getString(errorId);
        }
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void renderData(List<D> data) {
        adapter.setData(data);
    }

    @Override
    public void onPaginate() {
        presenter.paginate();
    }

    @Override
    public void onRefresh() {
        presenter.refresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        presenter.handleClick(position);
    }

    private void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void requestNewToken() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(intent);
    }
}
