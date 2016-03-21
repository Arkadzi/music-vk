package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.Nullable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKError;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public abstract class BaseListPresenter<D> implements BasePresenter<BaseView<D>> {
    public static final int STATE_FIRST_LOAD = 0;
    public static final int STATE_PAGINATE = 1;
    public static final int STATE_REFRESH = 2;
    public static final int STATE_IDLE = 3;
    public static final int STATE_FIRST_BIND = 4;
    protected int state;
    private int count;
    @Nullable
    private BaseView<D> view;
    @NotNull
    private List<D> data;
    @NotNull
    private VkApi vkApi;
    @NotNull
    private UserSession user;

    public BaseListPresenter(@NotNull VkApi api, @NotNull UserSession user) {
        Log.e("listpresenter", hashCode() + " " + api.hashCode());
        this.vkApi = api;
        this.user = user;
        reset();
    }

    @Override
    public void bindView(@Nullable BaseView<D> view) {
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

    protected void showError(String s) {
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
            if (!handled(result.getError())) {
                view.requestNewToken();
            }
        }
    }

    protected boolean handled(VKError error) {
        switch (error.getErrorCode()) {
            case 15:
                showError(error.getErrorMessage());
                if (view != null) {
                    view.dismiss();
                }
                break;
            default:return false;
        }
        return true;
    }

    @NotNull
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
            view.renderData(data);
            view.hideProgress();
        }
    }

    public abstract void handleClick(int position);

    @Nullable
    public BaseView<D> getView() {
        return view;
    }

    @NotNull
    public List<D> getData() {
        return data;
    }
}
