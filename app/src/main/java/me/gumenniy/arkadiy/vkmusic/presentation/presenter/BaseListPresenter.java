package me.gumenniy.arkadiy.vkmusic.presentation.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.gumenniy.arkadiy.vkmusic.domain.model.Song;
import me.gumenniy.arkadiy.vkmusic.data.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.data.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.VKError;
import me.gumenniy.arkadiy.vkmusic.data.rest.model.VKListResult;
import me.gumenniy.arkadiy.vkmusic.app.utils.Settings;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public abstract class BaseListPresenter<D> implements BasePresenter<BaseView<D>> {
    protected State state;
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
    private Call<VKListResult<D>> call;

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
            if (state == State.STATE_FIRST_BIND) {
                loadData(State.STATE_FIRST_LOAD);
            } else if (state == State.STATE_IDLE) {
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
        state = State.STATE_FIRST_BIND;
        data = new ArrayList<>();
        count = -1;
    }


    protected void loadData(State state) {
        onLoadingStart(state);

        call = getApiCall(vkApi, user);
        call.enqueue(new Callback<VKListResult<D>>() {
            @Override
            public void onResponse(Response<VKListResult<D>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    successfulResponse(response.body());
                } else {
                    showMessage(String.valueOf(response.errorBody()));
                }
                onLoadingStop();
                call = null;
            }

            @Override
            public void onFailure(Throwable t) {
                onLoadingStop();
                showMessage(t.toString());
                call = null;
            }
        });

    }

    protected void showMessage(@NonNull String message) {
        if (view != null) {
            view.showMessage(message);
        }
    }


    private void successfulResponse(@NonNull VKListResult<D> result) {
        if (result.isSuccessful()) {
            data.addAll(result.getData());
            count = result.getCount();
        } else if (view != null) {
            if (!handled(result.getError())) {
                view.requestNewToken();
            }
        }
    }

    protected void dismissProgressDialog() {
        BaseView<D> view = getView();
        if (view != null) {
            view.hideProgressDialog();
        }
    }

    protected void showProgressDialog() {
        BaseView<D> view = getView();
        if (view != null) {
            view.showProgressDialog();
        }
    }

    protected boolean handled(@NonNull VKError error) {
        switch (error.getErrorCode()) {
            case 15:
                showMessage(error.getErrorMessage());
                if (view != null) {
                    view.dismiss();
                }
                break;
            default:return false;
        }
        return true;
    }

    @NonNull
    protected abstract Call<VKListResult<D>> getApiCall(@NonNull VkApi api, @NonNull UserSession user);

    public void refresh() {
        reset();
        loadData(State.STATE_REFRESH);
    }

    public void paginate() {
        if (count > data.size() && state == State.STATE_IDLE) {
            loadData(State.STATE_PAGINATE);
        }
    }

    protected void onLoadingStart(State state) {
        this.state = state;

        if (view != null) {
            view.showProgress(state);
        }
    }

    protected void onLoadingStop() {
        state = State.STATE_IDLE;

        if (view != null) {
            view.renderData(data);
            view.hideProgress();
        }
    }

    @Override
    public abstract void handleClick(int position);

    public void handleMenuClick(Settings.Menu which, Song song, int position) {

    }

    public final void handleLongClick(int position){
        if (view != null) {
            view.showMenu(position);
        }
    }

    @Nullable
    public BaseView<D> getView() {
        return view;
    }

    @NonNull
    public List<D> getData() {
        return data;
    }


    @NonNull
    public VkApi getVkApi() {
        return vkApi;
    }

    @NonNull
    public UserSession getUser() {
        return user;
    }
}
