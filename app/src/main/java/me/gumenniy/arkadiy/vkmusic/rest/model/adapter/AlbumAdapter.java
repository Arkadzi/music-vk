package me.gumenniy.arkadiy.vkmusic.rest.model.adapter;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import me.gumenniy.arkadiy.vkmusic.model.Album;

/**
 * Created by Arkadiy on 03.04.2016.
 */
public class AlbumAdapter implements JsonDeserializer<Album> {

    @Override
    public Album deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String album = json.getAsJsonObject()
                    .get("track").getAsJsonObject()
                    .get("album").getAsJsonObject()
                    .get("title").getAsString();
            return new Album(album);
        } catch (Exception e) {
            return null;
        }
    }
}
