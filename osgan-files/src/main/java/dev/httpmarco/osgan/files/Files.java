package dev.httpmarco.osgan.files;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class Files {

    public static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (var outputStream = new FileOutputStream(file, false)) {
            int read;
            var bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }

    @SneakyThrows
    public static void writeString(Path path, String content) {
        java.nio.file.Files.writeString(path, content);
    }

    public static void writeString(String path, String content) {
        writeString(Path.of(path), content);
    }

    @SneakyThrows
    public static String readString(Path path) {
        return java.nio.file.Files.readString(path);
    }

    public static String readString(String path) {
        return readString(Path.of(path));
    }
}
