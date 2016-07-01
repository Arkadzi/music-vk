package me.gumenniy.arkadiy.vkmusic.data.rest.model.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import me.gumenniy.arkadiy.vkmusic.domain.model.Artwork;

/**
 * Created by Arkadiy on 03.04.2016.
 */
public class ArtworkAdapter implements JsonDeserializer<Artwork> {

    @Override
    public Artwork deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
//            JsonArray imageArray = json.getAsJsonObject()
//                    .get("album").getAsJsonObject()
//                    .get("image").getAsJsonArray();
            JsonArray imageArray = json.getAsJsonObject()
                    .get("track").getAsJsonObject()
                    .get("album").getAsJsonObject()
                    .get("image").getAsJsonArray();

            String uri = imageArray
                    .get(imageArray.size() - 1).getAsJsonObject()
                    .get("#text").getAsString();
            return new Artwork(uri);
        } catch (Exception e) {
            return null;
        }
    }
}
