package dev.httpmarco.osgon.files.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import dev.httpmarco.osgon.files.Document;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public class JsonDocument<T> extends Document<T> {

    private final Gson gson;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public JsonDocument(T defaultValue, Path path, TypeAdapter<?>... typeAdapters) {
        super(defaultValue, path);

        var builder = new GsonBuilder().setExclusionStrategies(new JsonByteExclusionStrategy())
                .disableHtmlEscaping()
                .setPrettyPrinting();

        for (var adapter : typeAdapters) {
            builder.registerTypeAdapter(adapter.getClass(), adapter);
        }

        this.gson = builder.create();

        if (Files.exists(path)) {
            value((T) gson.fromJson(Files.readString(path()), defaultValue.getClass()));
        }
    }

    @SneakyThrows
    @Override
    public void updateDocument() {
        Files.writeString(path(), gson.toJson(value()));
    }
}
