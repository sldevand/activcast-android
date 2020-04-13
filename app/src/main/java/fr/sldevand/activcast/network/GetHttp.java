package fr.sldevand.activcast.network;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetHttp extends AsyncTask<String, Void, String> {

    private OnHttpResponseListener responseListener;

    @Override
    protected String doInBackground(String... strings) {
        String address = strings[0];
        String NOT_FOUND = "404";
        if (null == address || address.equals("")) {
            return NOT_FOUND;
        }
        InputStream in = null;
        try {
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return "404";
        } finally {
            try {
                if (null != in) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return NOT_FOUND;
    }

    public void setOnResponseListener(OnHttpResponseListener rl) {
        this.responseListener = rl;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (null != this.responseListener) {
            responseListener.onGetResponse(s);
        }
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();

        return sb.toString();
    }

    public interface OnHttpResponseListener {
        void onGetResponse(String response);
    }
}
