package dev.httpmarco.osgon.files.configuration.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.httpmarco.osgon.files.configuration.gson.exclusion.ByteExclusionStrategy;

import java.io.FileReader;
import java.io.FileWriter;

public class JsonUtils {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setExclusionStrategies(new ByteExclusionStrategy())
            .create();
    private static final Gson PRETTY_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .setExclusionStrategies(new ByteExclusionStrategy())
            .create();


    public static Gson getGson() {
        return GSON;
    }

    public static Gson getPrettyGson() {
        return PRETTY_GSON;
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static String toPrettyJson(Object object) {
        return PRETTY_GSON.toJson(object);
    }

    public static void writeJson(Object object, FileWriter fileWriter) {
        GSON.toJson(object, fileWriter);
    }

    public static void writePrettyJson(Object object, FileWriter fileWriter) {
        PRETTY_GSON.toJson(object, fileWriter);
    }


    public static <T> T fromJson(String string, Class<T> tClass) {
        return GSON.fromJson(string, tClass);
    }

    public static <T> T fromPrettyJson(String string, Class<T> tClass) {
        return PRETTY_GSON.fromJson(string, tClass);
    }

    public static <T> T fromJson(FileReader fileReader, Class<T> tClass) {
        return GSON.fromJson(fileReader, tClass);
    }

    public static <T> T fromPrettyJson(FileReader fileReader, Class<T> tClass) {
        return PRETTY_GSON.fromJson(fileReader, tClass);
    }


}

