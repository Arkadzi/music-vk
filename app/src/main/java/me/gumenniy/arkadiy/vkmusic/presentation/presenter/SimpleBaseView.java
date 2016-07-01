package me.gumenniy.arkadiy.vkmusic.presentation.presenter;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Arkadiy on 22.04.2016.
 */
public interface SimpleBaseView<T> {
    void showMenu(int item);
    void showProgress(State state);
    void renderData(@NonNull List<T> data);
    void hideProgress();
    void showMessage(@NonNull String s);
}
