package me.gumenniy.arkadiy.vkmusic.presenter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public abstract class BaseListPresenter<D> {
    public static final int STATE_FIRST_LOAD = 0;
    public static final int STATE_PAGINATE = 1;
    public static final int STATE_REFRESH = 2;
    public static final int STATE_IDLE = 3;
    public static final int STATE_FIRST_BIND = 4;

    private int count;
    private BaseView<List<D>> view;
    private List<D> data;
    protected int state;
    private VkApi vkApi;
    private UserSession user;

    public BaseListPresenter(VkApi api, UserSession user) {
        this.vkApi = api;
        this.user = user;
        reset();
    }

    public void bindView(BaseView<List<D>> view) {
        Log.e("BaseListPresenter", String.valueOf(view));
        this.view = view;
        if (view != null) {
            if (state == STATE_FIRST_BIND) {
                loadData(STATE_FIRST_LOAD);
            } else if (state == STATE_IDLE) {
                onLoadingStop();
            } else {
                view.renderData(data);
                onLoadingStart(state);
            }
        }
    }

    protected void reset() {
        state = STATE_FIRST_BIND;
        data = new ArrayList<>();
        count = -1;
    }


    public void loadData(int state) {
        onLoadingStart(state);

        Call<VKResult<D>> call = getApiCall(vkApi, user);
        call.enqueue(new Callback<VKResult<D>>() {
            @Override
            public void onResponse(Response<VKResult<D>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    successfulResponse(response.body());
                } else {
                    showError(String.valueOf(response.errorBody()));
                }
                onLoadingStop();
            }

            @Override
            public void onFailure(Throwable t) {
                onLoadingStop();
                showError(t.getMessage());
            }
        });

    }

    private void showError(String s) {
        Log.e("Presenter", "error " + s);
        if (view != null) {
            view.showError(s);
        }
    }


    private void successfulResponse(VKResult<D> result) {
        if (result.isSuccessful()) {
            data.addAll(result.getResponse().getItems());
            count = result.getResponse().getCount();
        } else if (view != null) {
            view.requestNewToken();
        }
    }

    protected abstract Call<VKResult<D>> getApiCall(VkApi api, UserSession user);

    public void refresh() {
        data.clear();
        loadData(STATE_REFRESH);
    }

    public void paginate() {
        if (count > data.size()) {
            loadData(STATE_PAGINATE);
        }
    }

    private void onLoadingStart(int state) {
        this.state = state;

        if (view != null) {
            view.showProgress(state);
        }
    }

    private void onLoadingStop() {
        state = STATE_IDLE;

        if (view != null) {
            view.hideProgress();
            view.renderData(data);
        }
    }

    public abstract void handleClick(int position);

    public BaseView<List<D>> getView() {
        return view;
    }

    public List<D> getData() {
        return data;
    }
}
