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

    @GET("audio.getRecommendations?v=5.45&shuffle=1")
    Call<VKResult<Song>> getRecommendedSongs(
            @Query("owner_id") String ownerId,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("access_token") String token);

    @GET("audio.getPopular?v=5.45")
    Call<VKResult<Song>> getPopularSongsByGenre(
            @Query("only_eng") int eng,
            @Query("genre_id") int genre,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("access_token") String token);

    @GET("audio.getPopular?v=5.45")
    Call<VKResult<Song>> getPopularSongs(
            @Query("only_eng") int eng,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("access_token") String token);

    @GET("audio.search?v=5.45&sort=2&auto_complete=1 &search_own=1")
    Call<VKResult<Song>> getSongsByQuery(
            @Query("q") String query,
            @Query("performer_only") int performer,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("access_token") String token);

    @GET("friends.get?v=5.45&order=name&fields=domain,photo_100,can_see_audio")
    Call<VKResult<Friend>> getFriends(
            @Query("user_id") String userId,
            @Query("offset") int offset,
            @Query("count") int count);

    @GET("groups.get?v=5.45&extended=1&fields=photo_100,can_see_audio")
    Call<VKResult<Group>> getGroups(
            @Query("user_id") String userId,
            @Query("offset") int offset,
            @Query("count") int count,
            @Query("access_token") String token);
}
