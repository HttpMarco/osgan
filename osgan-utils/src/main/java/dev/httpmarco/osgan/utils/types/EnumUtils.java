package dev.httpmarco.osgan.utils.types;

public class EnumUtils {

    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String value) {
        return getEnum(enumClass, value) != null;
    }

    public static <E extends Enum<E>> boolean isValidEnumIgnoreCase(Class<E> enumClass, String value) {
        return getEnumIgnoreCase(enumClass, value) != null;
    }

    public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String value) {
        if (value == null) {
            return null;
        }

        try {
            return Enum.valueOf(enumClass, value);
        } catch (final IllegalArgumentException ex) {
            return null;
        }
    }

    public static <E extends Enum<E>> E getEnumIgnoreCase(Class<E> enumClass, String value) {
        if (value == null || !enumClass.isEnum()) {
            return null;
        }

        for (E each : enumClass.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(value)) {
                return each;
            }
        }
        return null;
    }
}
