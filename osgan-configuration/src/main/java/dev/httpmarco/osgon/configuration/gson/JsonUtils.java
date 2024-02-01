package dev.httpmarco.osgon.configuration.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.httpmarco.osgon.configuration.gson.exclusion.ByteExclusionStrategy;

import java.io.FileReader;
import java.io.FileWriter;

public class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setExclusionStrategies(new ByteExclusionStrategy()).create();

    public static Gson getGson() {
        return GSON;
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static void writeJson(Object object, FileWriter fileWriter) {
        GSON.toJson(object, fileWriter);
    }

    public static <T> T fromJson(String string, Class<T> tClass) {
        return GSON.fromJson(string, tClass);
    }

    public static <T> T fromJson(FileReader fileReader, Class<T> tClass) {
        return GSON.fromJson(fileReader, tClass);
    }
}