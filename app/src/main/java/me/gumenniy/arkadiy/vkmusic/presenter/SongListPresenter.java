package me.gumenniy.arkadiy.vkmusic.presenter;

import javax.inject.Inject;
import javax.inject.Singleton;

import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.rest.VkApi;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;

/**
 * Created by Arkadiy on 07.03.2016.
 */

@Singleton
public class SongListPresenter extends BaseListPresenter<Song> {
    private String userId;
    public final static String CURRENT_USER = "current";

    @Inject
    public SongListPresenter(VkApi api, UserSession user) {
        super(api, user);
    }

    @Override
    protected Call<VKResult<Song>> getApiCall(VkApi api, UserSession user) {
        String ownerId = (userId.equals(CURRENT_USER)) ? user.getClientId() : userId;
        return api.getSongs(ownerId, getData().size(), 100, user.getToken());
    }

    @Override
    public void handleClick(int position) {

    }

    public void setUserId(String userId) {
        this.userId = userId;
        reset();
    }
}
