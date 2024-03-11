package dev.httpmarco.osgan.files.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import dev.httpmarco.osgan.files.annotations.ConfigExclude;

public final class JsonByteExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(ConfigExclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(ConfigExclude.class);
    }
}
