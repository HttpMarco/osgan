package dev.httpmarco.osgon.files;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class Document<T> {

    private final Path path;
    private T value;

    @SneakyThrows
    protected void initialize() {
        if (Files.exists(path)) {
            value(documentToString(Files.readString(path)));
        } else {
            this.updateDocument();
        }
    }

    public void value(T value) {
        this.value = value;
    }

    public void updateWithValue(T value) {
        this.value = value;
        this.updateDocument();
    }

    public void append(Consumer<T> currentValue) {
        currentValue.accept(this.value);
        this.updateDocument();
    }

    public abstract String stringToDocument();

    public abstract T documentToString(String stringValue);

    @SneakyThrows
    public void updateDocument() {
        Files.writeString(this.path, this.stringToDocument());
    }

    @SneakyThrows
    public void delete() {
        Files.delete(this.path);
    }
}
