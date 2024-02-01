package dev.httpmarco.osgan.utils.decrypter;

import java.util.Base64;

public final class Base64Decrypter {

    public static String encode(String content) {
        return new String(Base64.getEncoder().encode(content.getBytes()));
    }

    public static String decode(String content) {
        return new String(Base64.getDecoder().decode(content.getBytes()));
    }
}
