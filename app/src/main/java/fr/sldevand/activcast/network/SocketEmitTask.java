package fr.sldevand.activcast.network;

import android.os.AsyncTask;

import fr.sldevand.activcast.helper.JsonResponse;

public class SocketEmitTask extends AsyncTask<Object, Void, String> {

    private String _event;

    SocketEmitTask(String event) {
        _event = event;
    }

    @Override
    protected String doInBackground(Object... args) {
        try {
            Thread.sleep(60);
            SocketIOHolder.socket.emit(_event, args[0]);
            return JsonResponse.SUCCESS;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return JsonResponse.NOT_FOUND;
    }

    @Override
    protected void onPostExecute(String result) {
        //intentional empty method
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}