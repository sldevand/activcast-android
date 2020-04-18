package fr.sldevand.activcast.service;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.sldevand.activcast.network.HttpParamsBuilder;

public class CommandService extends AbstractService {

    public void launch(String uri, String videoUrl) throws UnsupportedEncodingException {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("url", videoUrl);
        String body = HttpParamsBuilder.buildString(params);
        post(uri, body);
    }

    public void isRunning() {
        get("/command/isRunning");
    }

    public void play() {
        get("/command/play");
    }

    public void stop() {
        get("/command/stop");
    }

    public void fwd30() {
        get("/command/fwd30");
    }

    public void fwd600() {
        get("/command/fwd600");
    }

    public void back30() {
        get("/command/back30");
    }

    public void back600() {
        get("/command/back600");
    }
}
