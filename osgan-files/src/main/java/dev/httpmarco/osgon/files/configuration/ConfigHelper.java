package dev.httpmarco.osgon.files.configuration;

import dev.httpmarco.osgon.files.configuration.gson.JsonUtils;

import java.io.*;

public final class ConfigHelper {

    public static <T> T getConfig(String name, Class<T> clazz) {
        try {
            return JsonUtils.fromPrettyJson(new FileReader(name), clazz);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getConfigOrCreate(String name, Class<T> clazz, T defaultObject) {
        if (doesConfigExist(name)) {
            return getConfig(name, clazz);
        }
        saveConfig(name, defaultObject);
        return defaultObject;
    }


    public static void saveConfig(String name, Object object) {
        try {
            FileWriter fileWriter = new FileWriter(name);
            JsonUtils.writePrettyJson(object, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean doesConfigExist(String name) {
        return new File(name).exists();
    }
}
