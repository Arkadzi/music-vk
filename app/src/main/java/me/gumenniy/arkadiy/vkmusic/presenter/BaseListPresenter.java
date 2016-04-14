package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
    @NonNull
    private List<D> data;
    @NonNull
    private VkApi vkApi;
    @NonNull
    private UserSession user;
    @Nullable
    private Call<VKResult<D>> call;

    public BaseListPresenter(@NonNull VkApi api, @NonNull UserSession user) {
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
        if (call != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (call != null) {
                        call.cancel();
                        call = null;
                    }
                }
            }).start();
        }
        state = STATE_FIRST_BIND;
        data = new ArrayList<>();
        count = -1;
    }


    protected void loadData(int state) {
        onLoadingStart(state);

        call = getApiCall(vkApi, user);
        call.enqueue(new Callback<VKResult<D>>() {
            @Override
            public void onResponse(Response<VKResult<D>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    successfulResponse(response.body());
                } else {
                    showError(String.valueOf(response.errorBody()));
                }
                onLoadingStop();
                call = null;
            }

            @Override
            public void onFailure(Throwable t) {
                onLoadingStop();
                showError(t.toString());
                call = null;
            }
        });

    }

    protected void showError(@NonNull String s) {
        Log.e("Presenter", "error " + s);
        if (view != null) {
            view.showError(s);
        }
    }


    private void successfulResponse(@NonNull VKResult<D> result) {
        if (result.isSuccessful()) {
            data.addAll(result.getData());
            count = result.getCount();
        } else if (view != null) {
            if (!handled(result.getError())) {
                view.requestNewToken();
            }
        }
    }

    protected boolean handled(@NonNull VKError error) {
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

    @NonNull
    protected abstract Call<VKResult<D>> getApiCall(@NonNull VkApi api, @NonNull UserSession user);

    public void refresh() {
        reset();
        loadData(STATE_REFRESH);
    }

    public void paginate() {
        if (count > data.size() && state == STATE_IDLE) {
            loadData(STATE_PAGINATE);
        }
    }

    protected void onLoadingStart(int state) {
        this.state = state;

        if (view != null) {
            view.showProgress(state);
        }
    }

    protected void onLoadingStop() {
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

    @NonNull
    public List<D> getData() {
        return data;
    }
}
