package fr.sldevand.activcast.service;

import org.json.JSONException;
import org.json.JSONObject;

import fr.sldevand.activcast.network.SocketIOHolder;

public class CommandSocketIoService {
    private OnResponseListener onResponseListener;

    public void launch(String uri, String videoUrl) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url", videoUrl);
        SocketIOHolder.emit(uri, jsonObject.toString());
    }

    public void isRunning() {
        SocketIOHolder.emit("isRunning", "");
    }

    public void play() {
        SocketIOHolder.emit("play", "");
    }

    public void stop() {
        SocketIOHolder.emit("stop", "");
    }

    public void fwd30() {
        SocketIOHolder.emit("fwd30", "");
    }

    public void fwd600() {
        SocketIOHolder.emit("fwd600", "");
    }

    public void back30() {
        SocketIOHolder.emit("back30", "");
    }

    public void back600() {
        SocketIOHolder.emit("back600", "");
    }

    public void initListeners() {
        if (null == SocketIOHolder.getSocket()) {
            return;
        }
        SocketIOHolder.getSocket().on("success", (args) -> {
            for (Object arg : args) {
                if (null != onResponseListener) onResponseListener.onSuccess(String.valueOf(arg));
            }
        }).on("trouble", (args) -> {
            for (Object arg : args) {
                if (null != onResponseListener) onResponseListener.onError(String.valueOf(arg));
            }
        });
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    public interface OnResponseListener {
        void onSuccess(String success);

        void onError(String error);
    }
}
