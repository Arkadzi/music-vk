package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public interface BaseView<T> {
    void showMenu(int item);
    void showProgress(BaseListPresenter.State state);
    void renderData(@NonNull List<T> data);
    void hideProgress();
    void showMessage(@NonNull String s);
    void requestNewToken();
    void navigateBy(@NonNull T item);

    void showProgressDialog();

    void hideProgressDialog();

    void dismiss();
}
