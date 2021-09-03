package me.nathan.bot.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PlayerHelper {

    /** Credit to Salhack for UUID shit */
    public static String getUuid(String name) {
        JsonParser parser = new JsonParser();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        try {
            String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
            if(UUIDJson.isEmpty()) return "invalid name";
            JsonObject UUIDObject = (JsonObject) parser.parse(UUIDJson);
            return reformatUuid(UUIDObject.get("id").toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "error";
    }

    private static String reformatUuid(String uuid) {
        String longUuid = "";

        longUuid += uuid.substring(1, 9) + "-";
        longUuid += uuid.substring(9, 13) + "-";
        longUuid += uuid.substring(13, 17) + "-";
        longUuid += uuid.substring(17, 21) + "-";
        longUuid += uuid.substring(21, 33);

        return longUuid;
    }
}
