package dev.httpmarco.osgan.utils.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OsganFiles {

    public static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (var outputStream = new FileOutputStream(file, false)) {
            int read;
            var bytes = new byte[8192];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
    }
}
