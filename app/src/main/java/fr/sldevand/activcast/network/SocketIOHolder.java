package fr.sldevand.activcast.network;

import android.os.AsyncTask;

import java.net.URISyntaxException;

import fr.sldevand.activcast.helper.PrefsManager;
import fr.sldevand.activcast.interfaces.SocketIOEventsListener;
import io.socket.client.IO;
import io.socket.client.Socket;

public final class SocketIOHolder {

    static Socket socket;
    private static SocketEmitTask _task;
    private static SocketIOEventsListener mEventsListener;

    public static void launch() {
        try {
            String socketIOUrl = PrefsManager.apiDomain + ":" + PrefsManager.socketIOPort;
            if (null == socket) {
                socket = IO.socket(socketIOUrl);
                socket.io().timeout(5000);
            }
            start();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void start() {
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    public static void stop() {
        if (socket != null && socket.connected()) {
            socket.off();
            socket.close();
        }
    }

    public static void emitNoControl(String event, String data) {
        SocketEmitTask task = new SocketEmitTask(event);
        task.execute(data);
    }

    public static void emit(String event, String data) {
        if (null == SocketIOHolder.getSocket()) {
            return;
        }
        if (null == _task) {
            _task = new SocketEmitTask(event);
        }

        if (_task.getStatus() != AsyncTask.Status.RUNNING) {
            _task = new SocketEmitTask(event);
            _task.execute(data);
        }
    }

    public static void initEventListeners() {

        if (null == socket) {
            return;
        }
        socket.on(Socket.EVENT_CONNECT, args -> {
            if (null != mEventsListener) mEventsListener.onSocketIOConnect();
        }).on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
            if (null != mEventsListener) mEventsListener.onSocketIOTimeout();
        }).on(Socket.EVENT_DISCONNECT, args -> {
            if (null != mEventsListener) mEventsListener.onSocketIODisconnect();
        }).on(Socket.EVENT_MESSAGE, args -> {
            if (null != mEventsListener) mEventsListener.onSocketIOMessage(args);
        });
    }

    public static void setEventsListener(SocketIOEventsListener mEventsListener) {
        SocketIOHolder.mEventsListener = mEventsListener;
    }

    public static Socket getSocket(){
        return socket;
    }
}
