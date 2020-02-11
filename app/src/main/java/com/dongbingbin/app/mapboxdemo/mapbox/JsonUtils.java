package com.dongbingbin.app.mapboxdemo.mapbox;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * json工具类
 *
 * @author Chanven
 */
public class JsonUtils {
    private static Gson mGson = new Gson();

    public static synchronized <T> String toJsonString(Object src, TypeToken<T> typeToken) {
        try {
            return mGson.toJson(src, typeToken.getType());
        } catch (Exception e) {
            return null;
        }
    }

    public static synchronized String toJsonString(Object src) {
        try {
            return mGson.toJson(src);
        } catch (Exception e) {
            return null;
        }
    }

    public static synchronized <T> T fromJsonString(String jsonString, TypeToken<T> typeToken) {
        try {
            return mGson.fromJson(jsonString, typeToken.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized <T> T fromJsonString(String jsonString, Class<T> objClass) {
        try {
            return mGson.fromJson(jsonString, objClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized <T> T fromJsonString(String jsonString, Type type) {
        try {
            return mGson.fromJson(jsonString, type);
        } catch (Exception e) {
            return null;
        }
    }

    public static synchronized <T> T fromJsonString(JsonElement json, Class<T> classOfT) {
        try {
            return mGson.fromJson(json, classOfT);
        } catch (Exception e) {
            return null;
        }
    }

}
