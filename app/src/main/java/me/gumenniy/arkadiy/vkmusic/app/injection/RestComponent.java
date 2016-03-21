package me.gumenniy.arkadiy.vkmusic.app.injection;

import javax.inject.Singleton;

import dagger.Component;
import me.gumenniy.arkadiy.vkmusic.app.LoginActivity;
import me.gumenniy.arkadiy.vkmusic.app.MainActivity;
import me.gumenniy.arkadiy.vkmusic.app.VKMusicService;
import me.gumenniy.arkadiy.vkmusic.view.FriendListFragment;
import me.gumenniy.arkadiy.vkmusic.view.GroupListFragment;
import me.gumenniy.arkadiy.vkmusic.view.SongListFragment;

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

    void inject(MainActivity activity);
    void inject(LoginActivity activity);

    void inject(VKMusicService VKMusicService);
}
