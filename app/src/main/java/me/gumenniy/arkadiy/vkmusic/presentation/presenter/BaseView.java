package me.gumenniy.arkadiy.vkmusic.presentation.presenter;

import android.support.annotation.NonNull;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public interface BaseView<T> extends SimpleBaseView<T> {

    void requestNewToken();
    void navigateBy(@NonNull T item);

    void showProgressDialog();

    void hideProgressDialog();

    void dismiss();
}
