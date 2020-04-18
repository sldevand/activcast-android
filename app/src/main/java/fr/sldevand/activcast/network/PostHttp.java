package fr.sldevand.activcast.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import fr.sldevand.activcast.helper.ErrorResponse;

public class PostHttp extends AbstractHttp {
    @Override
    protected String doInBackground(String... strings) {
        String address = strings[0];
        String body = strings[1];
        if (null == address || address.equals("")
            || null == body || body.equals("")
        ) {
            return ErrorResponse.NOT_FOUND;
        }

        InputStream in = null;
        try {
            byte[] postDataBytes = body.getBytes(StandardCharsets.UTF_8);

            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            urlConnection.getOutputStream().write(postDataBytes);

            in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorResponse.NOT_FOUND;
        } finally {
            try {
                if (null != in) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ErrorResponse.NOT_FOUND;
    }
}
