package dev.httpmarco.osgan.reflections;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public final class FieldBindingReflections<T> extends Reflections<T> {

    private final Field fieldBinding;

    FieldBindingReflections(Class<T> clazz, Field fieldBinding) {
        super(clazz);
        this.fieldBinding = fieldBinding;
    }

    public @Nullable Class<?> generic(int index) {
        return this.generics()[index];
    }

    public @NotNull Class<?>[] generics() {
        var genericType = fieldBinding.getGenericType();
        if (genericType instanceof ParameterizedType parameterizedType) {
            return Arrays.stream(parameterizedType.getActualTypeArguments()).map(type -> (Class<?>) type).toArray(value -> new Class<?>[value]);
        } else {
            throw new UnsupportedOperationException("Cannot read generic from field: " + fieldBinding.getName());
        }
    }

    @SneakyThrows
    public Object value(Object parent) {
        this.access();
        return this.fieldBinding.get(parent);
    }

    @SuppressWarnings("unchecked")
    public <O> O value(Object parent, Class<O> valueType) {
        return (O) this.value(parent);
    }

    public void access() {
        this.fieldBinding.setAccessible(true);
    }
}