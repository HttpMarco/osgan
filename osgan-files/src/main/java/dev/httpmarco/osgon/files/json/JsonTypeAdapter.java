package dev.httpmarco.osgon.files.json;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import dev.httpmarco.osgon.files.DocumentTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class JsonTypeAdapter<T> implements JsonDeserializer<T>, JsonSerializer<T>, DocumentTypeAdapter<T> {

    private Class<T> type;

}
