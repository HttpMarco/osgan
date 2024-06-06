package dev.httpmarco.osgan.files;

import dev.httpmarco.osgan.utils.data.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public final class OsganFile {

    private final Path path;
    private final File file;

    @SneakyThrows
    public OsganFile(@NotNull Path path, OsganFileCreateOption createOption) {
        this.path = path;
        this.file = path.toFile();


        if (createOption == OsganFileCreateOption.NOTHING) {
            return;
        }

        if (createOption == OsganFileCreateOption.CREATION && file.exists()) {
            return;
        }

        mkdirParent(path);
        if (file.getName().contains(".")) {
            Files.createFile(path);
        } else {
            Files.createDirectory(path);
        }
    }

    @SafeVarargs
    public final <T> OsganFileDocument<T> asDocument(T defaultValue, Pair<Class<?>, Object>... typeAdapters) {
        return new OsganFileDocument<>(this, defaultValue, typeAdapters);
    }

    @SneakyThrows
    private void mkdirParent(Path path) {
        while (path.getParent() != null) {
            path = path.getParent();

            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        }
    }

    public boolean download(String url) {
        try (var inputStream = new URL(url).openStream();
             var outputStream = new BufferedOutputStream(new FileOutputStream(file.toString()))) {

            var buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void create(String path) {
        create(Path.of(path));
    }

    @SneakyThrows
    public static void create(Path path) {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

    @Contract(pure = true)
    public static @NotNull OsganFile define(String path) {
        return define(path, OsganFileCreateOption.NOTHING);
    }

    @Contract(pure = true)
    public static @NotNull OsganFile define(String namedPath, @NotNull OsganFileCreateOption option) {
        return new OsganFile(Path.of(namedPath), option);
    }
}
