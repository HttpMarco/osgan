package dev.httpmarco.osgan.reflections;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class Reflections<T> {

    private final Class<T> clazz;
    private @Nullable T value;

    public static <D> Reflections<D> of(Class<D> clazz) {
        return new Reflections<>(clazz);
    }

    public Reflections<T> withValue(Object value) {
        this.value = clazz.cast(value);
        return this;
    }

    @SneakyThrows
    public Field field(String id) {
        var field = this.clazz.getDeclaredField(id);
        field.setAccessible(true);
        return field;
    }

    @SneakyThrows
    public Method method(String id) {
        var method = this.clazz.getDeclaredMethod(id);
        method.setAccessible(true);
        return method;
    }

    @SneakyThrows
    public T newInstanceWithNoArgs() {
        return this.clazz.getConstructor().newInstance();
    }

    public T allocate() {
        return Allocator.allocate(clazz);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public T value(Field field) {
        field.setAccessible(true);
        return (T) field.get(this.value);
    }

    @SneakyThrows
    public T value(String fieldId) {
        return this.value(field(fieldId));
    }

    @SneakyThrows
    public void modify(Field field, Object value) {
        field.setAccessible(true);
        field.set(this.value, value);
    }

    @SneakyThrows
    public void modify(String fieldId, Object value) {
        this.modify(field(fieldId), value);
    }

    @SneakyThrows
    public void applyMethod(Method method, Object... args) {
        method.invoke(value, args);
    }

    @SneakyThrows
    public void applyMethod(String methodId, Object... args) {
        method(methodId).invoke(value, args);
    }
}
