package me.gumenniy.arkadiy.vkmusic.presenter;

import java.util.List;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public interface BaseView<T> {
    void showProgress(int state);
    void renderData(List<T> data);
    void hideProgress();
    void showError(String s);
    void requestNewToken();
    void navigateBy(T item);
    void dismiss();
}
