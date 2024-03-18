package dev.httpmarco.osgan.utils.types;

import dev.httpmarco.osgan.utils.Patterns;

public final class UniqueIDUtils {

    public static boolean isUniqueId(String input) {
        return input.matches(Patterns.UUID_PATTERN.pattern());
    }
}
