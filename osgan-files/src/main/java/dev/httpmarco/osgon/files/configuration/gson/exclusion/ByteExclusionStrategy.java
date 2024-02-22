package dev.httpmarco.osgon.files.configuration.gson.exclusion;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import dev.httpmarco.osgon.files.configuration.ConfigExclude;

public class ByteExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(ConfigExclude.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(ConfigExclude.class);
    }
}
