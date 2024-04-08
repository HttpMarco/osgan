package dev.httpmarco.osgan.reflections;

import dev.httpmarco.osgan.reflections.common.Allocator;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Accessors(fluent = true)
@Getter(AccessLevel.PACKAGE)
@SuppressWarnings("unchecked") // it's fine
@AllArgsConstructor
public class Reflections<T> {

    private final Class<T> clazz;

    public Field[] fields() {
        return clazz.getDeclaredFields();
    }

    public Set<Field> allFields() {
        var fields = new HashSet<>(Arrays.asList(clazz.getDeclaredFields()));
        var scannedPathClass = clazz;

        while (scannedPathClass.getSuperclass() != null) {
            scannedPathClass = (Class<T>) scannedPathClass.getSuperclass();
            fields.addAll(Arrays.asList(scannedPathClass.getDeclaredFields()));
        }
        return fields;
    }

    @SneakyThrows
    public Field field(String id) {
        var field = this.clazz.getDeclaredField(id);
        field.setAccessible(true);
        return field;
    }

    @Contract("_ -> new")
    public static <R> @NotNull Reflections<R> on(Class<R> clazz) {
        return new Reflections<>(clazz);
    }

    @Contract("_ -> new")
    public static <R> @NotNull Reflections<R> on(R value) {
        return new ObjectBindingReflection<>((Class<R>) value.getClass(), value);
    }

    @Contract("_ -> new")
    public static <R> @NotNull Reflections<R> on(Field field) {
        return new FieldBindingReflections<>((Class<R>) field.getType(), field);
    }

    public T allocate() {
        return Allocator.allocate(clazz);
    }

    @SneakyThrows
    public T instance(Object... args) {
        return clazz.getDeclaredConstructor(Arrays.stream(args).map(Object::getClass).toArray(value -> new Class<?>[value])).newInstance(args);
    }

    @SneakyThrows
    public Method method(String id) {
        var method = this.clazz.getDeclaredMethod(id);
        method.setAccessible(true);
        return method;
    }
}
