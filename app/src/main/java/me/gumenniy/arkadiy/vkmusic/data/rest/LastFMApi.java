package me.gumenniy.arkadiy.vkmusic.data.rest;


import me.gumenniy.arkadiy.vkmusic.domain.model.Album;
import me.gumenniy.arkadiy.vkmusic.domain.model.Artwork;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Arkadiy on 24.02.2016.
 */
public interface LastFMApi {

    @GET("http://ws.audioscrobbler.com/2.0/?method=track.getInfo&format=json")
    Call<Album> getAlbum(
            @Query("api_key") String apiKey,
            @Query("artist") String artist,
            @Query("track") String track);

    @GET("http://ws.audioscrobbler.com/2.0/?method=album.getInfo&format=json")
    Call<Artwork> getArtwork(
            @Query("api_key") String apiKey,
            @Query("artist") String artist,
            @Query("album") String album);

    @GET("http://ws.audioscrobbler.com/2.0/?method=track.getInfo&format=json")
    Call<Artwork> getArtwork2(
            @Query("api_key") String apiKey,
            @Query("artist") String artist,
            @Query("track") String track);
}
