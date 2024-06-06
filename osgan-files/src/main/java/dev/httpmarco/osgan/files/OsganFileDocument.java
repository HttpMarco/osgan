package dev.httpmarco.osgan.files;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Accessors(fluent = true)
public final class OsganFileDocument<T> {

    private final OsganFile osganFile;

    private final T defaultValue;

    @Getter
    private T content;

    public OsganFileDocument(OsganFile osganFile, T defaultValue) {
        this.osganFile = osganFile;
        this.defaultValue = defaultValue;

        if (!osganFile.file().exists()) {
            save();
        }

        this.update();

        // we update the current context with new fields
        this.save();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void update() {
        this.content = (T) OsganGsonContext.GSON.fromJson(Files.readString(osganFile.path(), StandardCharsets.UTF_8), defaultValue.getClass());
    }

    @SneakyThrows
    public void save() {
        Files.writeString(osganFile.path(), OsganGsonContext.GSON.toJson(content == null ? defaultValue : content), StandardCharsets.UTF_8);
    }
}
