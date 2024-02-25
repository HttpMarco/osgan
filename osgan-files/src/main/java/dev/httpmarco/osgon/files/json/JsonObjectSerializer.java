package dev.httpmarco.osgon.files.json;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JsonObjectSerializer {

    private JsonObject jsonObject = new JsonObject();

    public JsonObjectSerializer(String gsonObject) {
        this.jsonObject = JsonUtils.getGson().fromJson(gsonObject, JsonObject.class);
    }

    public JsonObjectSerializer append(String key, String value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public String readString(String key) {
        return jsonObject.get(key).getAsString();
    }

    public JsonObjectSerializer append(String key, int value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public int readInt(String key) {
        return jsonObject.get(key).getAsInt();
    }

    public JsonObjectSerializer append(String key, double value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public double readDouble(String key) {
        return jsonObject.get(key).getAsDouble();
    }

    public JsonObjectSerializer append(String key, long value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public long readLong(String key) {
        return jsonObject.get(key).getAsLong();
    }

    public JsonObjectSerializer append(String key, boolean value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public boolean readBoolean(String key) {
        return jsonObject.get(key).getAsBoolean();
    }

    public JsonObjectSerializer append(String key, Object value) {
        append(key, JsonUtils.toJson(value));
        return this;
    }

    public <T> T readObject(String key, Class<T> clazz) {
        return JsonUtils.fromJson(readString(key), clazz);
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
