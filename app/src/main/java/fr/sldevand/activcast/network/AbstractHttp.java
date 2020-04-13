package fr.sldevand.activcast.network;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

abstract public class AbstractHttp extends AsyncTask<String, Void, String> {

    private OnHttpResponseListener responseListener;

    @Override
    abstract protected String doInBackground(String... strings);

    String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (null != this.responseListener) {
            responseListener.onResponse(s);
        }
    }

    public interface OnHttpResponseListener {
        void onResponse(String response);
    }

    public void setOnResponseListener(OnHttpResponseListener rl) {
        this.responseListener = rl;
    }
}
