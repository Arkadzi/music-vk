package me.gumenniy.arkadiy.vkmusic.presentation.presenter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Arkadiy on 31.03.2016.
 */
public interface OnSearchSubmitListener {
    void onSearchSubmit(@NotNull String query, boolean byArtist);
}
