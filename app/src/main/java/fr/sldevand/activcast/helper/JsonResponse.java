package fr.sldevand.activcast.helper;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonResponse {
    public final static String NOT_FOUND = "{\"status\": 0, \"message\": \"Could not connect\"}";
    public final static String SUCCESS = "{\"status\": 1, \"message\": \"Success\"}";

    public static boolean isRunning(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("running")) {
            return jsonObject.getBoolean("running");
        }

        throw new JSONException("Cannot find running parameter");
    }

    public static String getMessage(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("message")) {
            return jsonObject.getString("message");
        }

        throw new JSONException("Cannot find message parameter");
    }
}
