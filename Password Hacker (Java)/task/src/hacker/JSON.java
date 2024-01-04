package hacker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class JSON {

    public static String convertToJSON(Map<String,String> data) {
        Type type = new TypeToken<Map<String,String>>(){}.getType();
        Gson gson = new Gson();
        return gson.toJson(data, type);
    }

    public static Map<String,String> convertFromJSON(String text) {
        Type type = new TypeToken<Map<String,String>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(text, type);
    }
}
