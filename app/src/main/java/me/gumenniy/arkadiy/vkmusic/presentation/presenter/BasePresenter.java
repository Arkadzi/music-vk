package me.gumenniy.arkadiy.vkmusic.presentation.presenter;

import android.support.annotation.Nullable;

/**
 * Created by Arkadiy on 07.03.2016.
 */
public interface BasePresenter<V> {
    void bindView(@Nullable V view);

    void handleClick(int position);
}
