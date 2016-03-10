package me.gumenniy.arkadiy.vkmusic.presenter;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public interface BaseView<T> {
    void showProgress(int state);
    void renderData(T data);
    void hideProgress();
    void showError(String s);
    void requestNewToken();
}
