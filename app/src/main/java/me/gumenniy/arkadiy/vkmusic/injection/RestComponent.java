package me.gumenniy.arkadiy.vkmusic.injection;

import javax.inject.Singleton;

import dagger.Component;
import me.gumenniy.arkadiy.vkmusic.app.LoginActivity;
import me.gumenniy.arkadiy.vkmusic.app.MainActivity;
import me.gumenniy.arkadiy.vkmusic.app.MusicService;
import me.gumenniy.arkadiy.vkmusic.app.dialogs.PopularSongDialogFragment;
import me.gumenniy.arkadiy.vkmusic.app.dialogs.SearchDialogFragment;
import me.gumenniy.arkadiy.vkmusic.app.dialogs.SongDialogFragment;
import me.gumenniy.arkadiy.vkmusic.app.fragments.CacheFragment;
import me.gumenniy.arkadiy.vkmusic.app.fragments.FriendListFragment;
import me.gumenniy.arkadiy.vkmusic.app.fragments.GroupListFragment;
import me.gumenniy.arkadiy.vkmusic.app.fragments.PopularSongsFragment;
import me.gumenniy.arkadiy.vkmusic.app.fragments.SearchFragment;
import me.gumenniy.arkadiy.vkmusic.app.fragments.SongListFragment;

/**
 * Created by Arkadiy on 07.03.2016.
 */
@Component(
        modules = RestClientModule.class
)
@Singleton
public interface RestComponent {
    void inject(SongListFragment fragment);
    void inject(FriendListFragment fragment);
    void inject(GroupListFragment fragment);
    void inject(SearchFragment searchFragment);
    void inject(PopularSongsFragment popularSongsFragment);

    void inject(CacheFragment cacheFragment);

    void inject(SongDialogFragment songDialogFragment);
    void inject(PopularSongDialogFragment popularSongDialogFragment);
    void inject(SearchDialogFragment searchDialogFragment);

    void inject(MainActivity activity);
    void inject(LoginActivity activity);

    void inject(MusicService musicService);

}
