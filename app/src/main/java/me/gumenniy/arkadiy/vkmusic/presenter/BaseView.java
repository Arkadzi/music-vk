package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public interface BaseView<T> {
    void showProgress(int state);
    void renderData(@NonNull List<T> data);
    void hideProgress();
    void showError(@NonNull String s);
    void requestNewToken();
    void navigateBy(@NonNull T item);
    void dismiss();
}
