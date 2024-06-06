package dev.httpmarco.osgan.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class OsganGsonContext {

    public static Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().setExclusionStrategies(new OsganGsonExclusionStrategy()).create();

}
