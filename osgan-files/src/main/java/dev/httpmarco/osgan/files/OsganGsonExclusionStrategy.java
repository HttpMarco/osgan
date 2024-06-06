package dev.httpmarco.osgan.files;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public final class OsganGsonExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(DocumentExclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(DocumentExclude.class);
    }
}
