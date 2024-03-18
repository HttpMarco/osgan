package dev.httpmarco.osgan.utils.validate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Check {

    // simple not null check
    public static void notNull(Object object, String reason) {
        if (Objects.isNull(object)) {
            throw new NullPointerException(reason);
        }
    }

    public static void notNull(Object object, String reason, Object... arguments) {
        if (Objects.isNull(object)) {
            throw new NullPointerException(MessageFormat.format(reason, arguments));
        }
    }

    public static void argCondition(boolean condition, String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void stateCondition(boolean condition, String message) {
        if (condition) {
            throw new IllegalStateException(message);
        }
    }
}
