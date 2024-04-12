package dev.httpmarco.osgan.reflections;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ObjectBindingReflection<T> extends Reflections<T> {

    private final T objectBinding;

    ObjectBindingReflection(Class<T> clazz, T objectBinding) {
        super(clazz);
        this.objectBinding = objectBinding;
    }

    @SneakyThrows
    public Object value(@NotNull Field field) {
        field.setAccessible(true);
        return field.get(this.objectBinding);
    }

    public Object value(String fieldId) {
        return this.value(this.field(fieldId));
    }

    @SuppressWarnings("unchecked")
    public <R> R value(String fieldId, Class<R> objectType) {
        return (R) this.value(fieldId);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <R> R value(@NotNull Field field, Class<R> objectType) {
        return (R) this.value(field);
    }

    @SneakyThrows
    public void modify(@NotNull Field field, Object value) {
        field.setAccessible(true);
        field.set(this.objectBinding, value);
    }

    @SneakyThrows
    public void modify(String fieldId, Object value) {
        this.modify(this.clazz().getDeclaredField(fieldId), value);
    }

    @SneakyThrows
    public void applyMethod(@NotNull Method method, Object... args) {
        method.invoke(objectBinding, args);
    }

    @SneakyThrows
    public void applyMethod(String methodId, Object... args) {
        method(methodId).invoke(objectBinding, args);
    }
}
