package dev.httpmarco.osgan.reflections;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public final class Reflections {

    @SneakyThrows
    public static void modifyField(Field field, Object object, Object value) {
        field.setAccessible(true);
        field.set(object, value);
    }

    @SneakyThrows
    public static Object getField(Field field, Object object) {
        field.setAccessible(true);
        return field.get(object);
    }
}
