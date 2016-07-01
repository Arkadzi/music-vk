package me.gumenniy.arkadiy.vkmusic.injection;

import javax.inject.Singleton;

import dagger.Component;
import me.gumenniy.arkadiy.vkmusic.data.LoadService;
import me.gumenniy.arkadiy.vkmusic.presentation.activities.LoginActivity;
import me.gumenniy.arkadiy.vkmusic.presentation.activities.MainActivity;
import me.gumenniy.arkadiy.vkmusic.data.MusicService;
import me.gumenniy.arkadiy.vkmusic.presentation.dialogs.PopularSongDialogFragment;
import me.gumenniy.arkadiy.vkmusic.presentation.dialogs.SearchDialogFragment;
import me.gumenniy.arkadiy.vkmusic.presentation.dialogs.SongDialogFragment;
import me.gumenniy.arkadiy.vkmusic.presentation.fragments.CacheFragment;
import me.gumenniy.arkadiy.vkmusic.presentation.fragments.FriendListFragment;
import me.gumenniy.arkadiy.vkmusic.presentation.fragments.GroupListFragment;
import me.gumenniy.arkadiy.vkmusic.presentation.fragments.PopularSongsFragment;
import me.gumenniy.arkadiy.vkmusic.presentation.fragments.SearchFragment;
import me.gumenniy.arkadiy.vkmusic.presentation.fragments.SongListFragment;

/**
 * Created by Arkadiy on 07.03.2016.
 */
@Component(
        modules = ApplicationModule.class
)
@Singleton
public interface ApplicationComponent {
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

    void inject(LoadService loadService);

}
