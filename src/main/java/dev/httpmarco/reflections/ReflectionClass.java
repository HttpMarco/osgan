package dev.httpmarco.reflections;

import dev.httpmarco.reflections.allocate.ReflectionClassAllocater;
import dev.httpmarco.reflections.utils.JavaParameters;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class ReflectionClass<T> {

    private final Class<T> clazz;

    public ReflectionClass(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Stream<Field> fieldsStream() {
        return Arrays.stream(this.clazz.getDeclaredFields());
    }

    public void modifyField(Object parent, Field field, Object newValue) {
        try {
            field.setAccessible(true);
            field.set(parent, newValue);
        } catch (IllegalAccessException e) {
            System.err.println("Cannot set field '" + field.getName() + "' on " + parent.getClass().getSimpleName() + " with value: " + newValue.toString());
        }
    }

    public List<Field> fields() {
        return Arrays.stream(this.clazz.getDeclaredFields()).toList();
    }

    public Field filed(String id) {
        try {
            return this.clazz.getDeclaredField(id);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Field> field(String id) {
        try {
            return Optional.of(this.clazz.getDeclaredField(id));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    public T emptyInstance(){
        return ReflectionClassAllocater.allocate(this.clazz);
    }

    public boolean isJavaParameter() {
        return JavaParameters.ELEMENTS.contains(this.clazz) || this.clazz.isPrimitive();
    }

    public boolean isCollection() {
        return Collection.class.isAssignableFrom(this.clazz);
    }

    public boolean isNumber() {
        return Number.class.isAssignableFrom(clazz) ||
                (clazz.isPrimitive() && clazz == int.class || clazz == long.class || clazz == double.class
                        || clazz == float.class || clazz == short.class || clazz == byte.class);
    }
}
