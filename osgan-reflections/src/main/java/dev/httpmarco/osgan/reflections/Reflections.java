package dev.httpmarco.osgan.reflections;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    public static Object getField(String id, Object value) {
        try {
            return getField(value.getClass().getDeclaredField(id), value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void callMethod(Method method, Object object, Object... args) {
        try {
            method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
