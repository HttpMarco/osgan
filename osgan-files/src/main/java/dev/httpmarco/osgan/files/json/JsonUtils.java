package dev.httpmarco.osgan.files.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;

public class JsonUtils {

    private static final Gson JSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setExclusionStrategies(new JsonByteExclusionStrategy())
            .create();
    private static final Gson PRETTY_JSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .setExclusionStrategies(new JsonByteExclusionStrategy())
            .create();


    public static Gson getGson() {
        return JSON;
    }

    public static Gson getPrettyGson() {
        return PRETTY_JSON;
    }

    public static String toJson(Object object) {
        return JSON.toJson(object);
    }

    public static String toPrettyJson(Object object) {
        return PRETTY_JSON.toJson(object);
    }

    public static void writeJson(Object object, FileWriter fileWriter) {
        JSON.toJson(object, fileWriter);
    }

    public static void writePrettyJson(Object object, FileWriter fileWriter) {
        PRETTY_JSON.toJson(object, fileWriter);
    }

    public static <T> T fromJson(String string, Class<T> tClass) {
        return JSON.fromJson(string, tClass);
    }

    public static <T> T fromJson(FileReader fileReader, Class<T> tClass) {
        return JSON.fromJson(fileReader, tClass);
    }
}

