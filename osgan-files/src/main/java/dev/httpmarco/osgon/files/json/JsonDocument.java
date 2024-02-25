package dev.httpmarco.osgon.files.json;

import dev.httpmarco.osgon.files.Document;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public class JsonDocument<T> extends Document<T> {

    public JsonDocument(T defaultValue, Path path) {
        super(defaultValue, path);
    }

    @SneakyThrows
    @Override
    public void updateDocument() {
        Files.writeString(path(), JsonUtils.toPrettyJson(value()));
    }
}
