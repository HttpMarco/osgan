package dev.httpmarco.reflections;

import dev.httpmarco.reflections.allocate.ReflectionClassAllocater;
import dev.httpmarco.reflections.utils.JavaParameters;

import java.lang.reflect.Field;
import java.util.Optional;

public final class ReflectionClass<T> {

    private final Class<T> clazz;

    public ReflectionClass(Class<T> clazz) {
        this.clazz = clazz;
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
}
