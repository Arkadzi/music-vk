package me.gumenniy.arkadiy.vkmusic.rest.model.adapter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.gumenniy.arkadiy.vkmusic.model.Song;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResponse;
import me.gumenniy.arkadiy.vkmusic.rest.model.VKResult;

/**
 * Created by Arkadiy on 01.04.2016.
 */
public class VKResultTypeAdapter<T> implements JsonDeserializer<VKResult<T>> {
    private Gson gson = new GsonBuilder().create();

    @Override
    public VKResult<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Log.e("reader", String.format("%s %s %s", json, typeOfT, context));
        VKResult<T> result;
        try {
            result = gson.fromJson(json, typeOfT);
            Log.e("result", String.valueOf(result));
            return result;
        } catch (Exception e) {
            JsonArray response = json.getAsJsonObject().getAsJsonArray("response");
            Type listType = new TypeToken<List<Song>>(){}.getType();
            List<T> list = gson.fromJson(response, listType);
            Log.e("result", String.valueOf(list.getClass() + list.toString()));
            return new VKResult<>(new VKResponse<>(6000, list));
        }
    }
}
