package dev.httpmarco.osgon.configuration.gson;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JsonDocument {

    private JsonObject jsonObject;

    public JsonDocument append(String key, String value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public String readString(String key) {
        return jsonObject.get(key).getAsString();
    }

    public JsonDocument append(String key, int value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public int readInt(String key) {
        return jsonObject.get(key).getAsInt();
    }

    public JsonDocument append(String key, double value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public double readDouble(String key) {
        return jsonObject.get(key).getAsDouble();
    }

    public JsonDocument append(String key, long value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public long readLong(String key) {
        return jsonObject.get(key).getAsLong();
    }

    public JsonDocument append(String key, boolean value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public boolean readBoolean(String key) {
        return jsonObject.get(key).getAsBoolean();
    }

    public JsonDocument append(String key, Object value) {
        append(key, JsonUtils.toJson(value));
        return this;
    }

    public <T> T readObject(String key, Class<T> clazz) {
        return JsonUtils.fromPrettyJson(readString(key), clazz);
    }
}
