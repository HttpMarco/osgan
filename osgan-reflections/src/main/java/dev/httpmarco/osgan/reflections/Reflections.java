package dev.httpmarco.osgan.reflections;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

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

    public static <T> T newInstance(Class<T> clazz, Object... objects) {
        try {
            return clazz.getConstructor(Arrays.stream(objects).map(Object::getClass).toArray(Class[]::new)).newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getField(String id, Object value) {
        try {
            return getField(value.getClass().getDeclaredField(id), value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
