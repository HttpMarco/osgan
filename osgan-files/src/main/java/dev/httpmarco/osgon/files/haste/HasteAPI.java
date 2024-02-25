package dev.httpmarco.osgon.files.haste;

import com.google.gson.JsonObject;
import dev.httpmarco.osgon.files.json.JsonUtils;
import lombok.SneakyThrows;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HasteAPI {

    private static String BYTEMC_HASTE_URL = "https://haste.bytemc.de";
    private static String BYTEMC_POST_HASTE_URL = BYTEMC_HASTE_URL + "/documents";

    public static void setBytemcHasteUrl(String bytemcHasteUrl) {
        BYTEMC_HASTE_URL = bytemcHasteUrl;
        BYTEMC_POST_HASTE_URL = BYTEMC_HASTE_URL + "/documents";
    }

    public static String createHaste(String content) {
        return postHaste(content.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static String createHaste(InputStream in) {
        return postHaste(in.readAllBytes());
    }

    public static String getHasteKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    @SneakyThrows
    private static String postHaste(byte[] postData) {
        try {
            var connection = (HttpsURLConnection) URI.create(BYTEMC_POST_HASTE_URL).toURL().openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);

            try (var out = new DataOutputStream(connection.getOutputStream())) {
                out.write(postData);
            }

            try (var in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                var response = in.readLine();
                if (response == null) {
                    return BYTEMC_HASTE_URL;
                }
                var jsonObject = JsonUtils.fromJson(response, JsonObject.class);
                if (!jsonObject.has("key")) {
                    return BYTEMC_HASTE_URL;
                }
                return BYTEMC_HASTE_URL + "/" + jsonObject.get("key").getAsString();
            }
        } catch (IOException e) {
            return BYTEMC_HASTE_URL;
        }
    }
}
