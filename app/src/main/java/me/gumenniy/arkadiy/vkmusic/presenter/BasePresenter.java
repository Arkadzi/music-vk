package me.gumenniy.arkadiy.vkmusic.presenter;

import android.support.annotation.Nullable;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public interface BasePresenter<V> {
    void bindView(@Nullable V view);
}
