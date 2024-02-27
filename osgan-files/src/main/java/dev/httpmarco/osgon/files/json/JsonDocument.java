package dev.httpmarco.osgon.files.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import dev.httpmarco.osgon.files.Document;
import lombok.SneakyThrows;
import java.nio.file.Path;

public class JsonDocument<T> extends Document<T> {

    private final Gson gson;

    @SneakyThrows
    public JsonDocument(T defaultValue, Path path, JsonTypeAdapter<?>... typeAdapters) {
        super(path, defaultValue);

        var builder = new GsonBuilder().setExclusionStrategies(new JsonByteExclusionStrategy())
                .disableHtmlEscaping()
                .setPrettyPrinting();

        for (var adapter : typeAdapters) {
            builder.registerTypeAdapter(adapter.type(), adapter);
        }

        this.gson = builder.create();
        this.initialize();
    }

    @Override
    public String stringToDocument() {
        return gson.toJson(this.value());
    }

    @Override
    @SuppressWarnings("unchecked")
    public T documentToString(String stringValue) {
        return (T) gson.fromJson(stringValue, value().getClass());
    }
}
