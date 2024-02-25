package dev.httpmarco.osgon.files;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Accessors(fluent = true)
public abstract class Document<T> {

    private final Path path;
    private T value;

    public Document(T defaultValue, Path path) {
        this.value = defaultValue;
        this.path = path;

        this.updateDocument();
    }

    public void value(T value) {
        this.value = value;
    }

    public void updateWithValue(T value) {
        this.value = value;
        this.updateDocument();
    }

    public abstract void updateDocument();

    @SneakyThrows
    public void delete() {
        Files.delete(this.path);
    }
}
