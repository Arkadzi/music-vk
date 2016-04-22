package me.gumenniy.arkadiy.vkmusic.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.app.LoginActivity;
import me.gumenniy.arkadiy.vkmusic.app.MusicApplication;
import me.gumenniy.arkadiy.vkmusic.app.adapter.AbstractListAdapter;
import me.gumenniy.arkadiy.vkmusic.app.dialogs.ProgressDialogFragment;
import me.gumenniy.arkadiy.vkmusic.app.injection.RestComponent;
import me.gumenniy.arkadiy.vkmusic.presenter.BaseListPresenter;
import me.gumenniy.arkadiy.vkmusic.presenter.BaseView;
import me.gumenniy.arkadiy.vkmusic.utils.Messages;
import me.gumenniy.arkadiy.vkmusic.utils.Paginator;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public abstract class BaseListFragment<D, P extends BaseListPresenter<D>> extends Fragment
        implements BaseView<D>, Paginator.OnPaginateListener,
        SwipeRefreshLayout.OnRefreshListener,
        AbstractListAdapter.OnItemClickListener,
        OnBackPressListener, AbstractListAdapter.OnItemLongClickListener {

    public static final String TITLE = "title";
    @Bind(R.id.list)
    RecyclerView recyclerView;
    @Bind(R.id.progress_bar)
    CircularProgressView progressBar;
    @Bind(R.id.bottom_progress_bar)
    View bottomProgressBar;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeLayout;
    @Bind(R.id.empty_view)
    View emptyView;
    @Inject
    P presenter;
    private AbstractListAdapter<D> adapter;
    @Nullable
    private String title;
    private boolean visible;
    private float height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(MusicApplication.getApp(getActivity()).getComponent());
    }

    protected abstract void inject(RestComponent component);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getTitle());
        }
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        ButterKnife.bind(this, view);
        initViews();
        presenter.bindView(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        height = bottomProgressBar.getHeight();
    }

    @Override
    public void navigateBy(@NonNull D item) {

    }

    @Override
    public final void showMenu(int item) {
        if (item < presenter.getData().size()) {
            DialogFragment newFragment = getMenuDialog(item);
            if (newFragment != null) {
                newFragment.show(getChildFragmentManager(), "dialog");
            }
        }
    }

    @Nullable
    protected DialogFragment getMenuDialog(int item) {
        return null;
    }

    private void initViews() {
        float metrics = getResources().getDisplayMetrics().density;
        float dimension = getResources().getDimension(R.dimen.bottom_progress_height);
        height = dimension * metrics;

        bottomProgressBar.setTranslationY(height);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = getListAdapter();
        adapter.setClickListener(this);
        if (isHandleLongClick()) {
            adapter.setLongClickListener(this);
        }
        recyclerView.setAdapter(adapter);
        Paginator paginator = new Paginator();
        paginator.setOnPaginateListener(this);
        paginator.paginateListView(recyclerView);
        swipeLayout.setOnRefreshListener(this);
    }

    protected boolean isHandleLongClick() {
        return false;
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
    public void showProgress(BaseListPresenter.State state) {
        setVisibility(emptyView, false);
        setVisibility(recyclerView, state == BaseListPresenter.State.STATE_PAGINATE);
        setVisibility(progressBar, state == BaseListPresenter.State.STATE_FIRST_LOAD);
        animate(bottomProgressBar, state == BaseListPresenter.State.STATE_PAGINATE);
        setRefreshing(state == BaseListPresenter.State.STATE_REFRESH);
    }

    @Override
    public void hideProgress() {
        setVisibility(recyclerView, true);
        setVisibility(emptyView, recyclerView.getAdapter().getItemCount() == 0);
        setVisibility(progressBar, false);
        animate(bottomProgressBar, false);
        setRefreshing(false);
    }


    private void animate(final View view, final boolean makeVisible) {
        boolean animate = (visible && !makeVisible)
                || (!visible && makeVisible);
        visible = makeVisible;
        if (animate) {
            if (makeVisible) {
                view.animate().cancel();
                view.setTranslationY(height);
                view.animate().translationY(0);
            } else {
                view.animate().cancel();
                view.setTranslationY(0);
                view.animate().translationY(height);
            }
            Log.e("view", view.getY() + " " + view.getTranslationY() + " " + view.getHeight());
        }
    }

    @Override
    public void showProgressDialog() {
        DialogFragment newFragment = ProgressDialogFragment.newInstance();
        newFragment.setCancelable(false);
        newFragment.show(getChildFragmentManager(), "progress_dialog");
    }

    @Override
    public void hideProgressDialog() {
        DialogFragment fragment = (DialogFragment) getChildFragmentManager().findFragmentByTag("progress_dialog");
        if (fragment != null) {
            fragment.dismiss();
        }
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
    public void dismiss() {
        getActivity().onBackPressed();
    }

    @Override
    public void showMessage(@NonNull String message) {
        Integer errorId = Messages.get(message);
        if (errorId != null) {
            message = getString(errorId);
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void renderData(@NonNull List<D> data) {
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
    public void onItemClick(int position) {
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

    @Override
    public boolean backPressHandled() {
        return false;
    }

    @Override
    public void onItemLongClick(int position) {
        presenter.handleLongClick(position);
    }
}
