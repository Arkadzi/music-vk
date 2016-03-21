package me.gumenniy.arkadiy.vkmusic.rest;


import me.gumenniy.arkadiy.vkmusic.model.Friend;
import me.gumenniy.arkadiy.vkmusic.model.Group;
import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public interface VkApi {
    @GET("audio.get?v=5.45&need_user=0")
    Call<VKResult<Song>> getSongs(
            @Query("owner_id") String ownerId,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("access_token") String token);

    @GET("friends.get?v=5.45&order=name&fields=domain,photo_50,can_see_audio")
    Call<VKResult<Friend>> getFriends(
            @Query("user_id") String userId,
            @Query("offset") int offset,
            @Query("count") int count);

    @GET("groups.get?v=5.45&extended=1&fields=photo_50,can_see_audio")
    Call<VKResult<Group>> getGroups(
            @Query("user_id") String userId,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("access_token") String token);
}
