package fr.sldevand.activcast.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.util.Map;

public class HttpParamsBuilder {

    public static String buildString( Map<String, Object> params ) throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        return postData.toString();
    }

}
