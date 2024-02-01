package dev.httpmarco.reflections.allocate;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public final class ReflectionClassAllocater {

    public static final Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException var1) {
            throw new RuntimeException(var1);
        }
    }

    public static <T> T allocate(Class<T> tClass) {
        try {
            return (T) unsafe.allocateInstance(tClass);
        } catch (InstantiationException e) {
            try {
                // default empty constructor
                return tClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ex) {
                System.err.println("Cannot create new object: " + tClass.getSimpleName());
            }
        }
        return null;
    }
}