package fr.sldevand.activcast.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fr.sldevand.activcast.helper.ErrorResponse;

public class GetHttp extends AbstractHttp{
    @Override
    protected String doInBackground(String... strings) {
        String address = strings[0];
        if (null == address || address.equals("")) {
            return ErrorResponse.NOT_FOUND;
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
