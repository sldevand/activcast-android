package fr.sldevand.activcast.service;

import android.os.AsyncTask;
import android.util.Log;

import fr.sldevand.activcast.helper.PrefsManager;
import fr.sldevand.activcast.network.AbstractHttp;
import fr.sldevand.activcast.network.GetHttp;
import fr.sldevand.activcast.network.PostHttp;

abstract public class AbstractHttpService {
    private OnResponseListener onResponseListener;

    protected void get(String uri) {
        GetHttp getHttp = new GetHttp();
        getHttp.setOnResponseListener(new AbstractHttp.OnHttpResponseListener() {
            @Override
            public void onResponse(String response) {
                if (null != onResponseListener) {
                    onResponseListener.onResponse(response);
                }
            }
        });
        getHttp.executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                PrefsManager.apiDomain + PrefsManager.apiUrl + uri
        );
    }

    protected void post(String uri, String body) {
        PostHttp postHttp = new PostHttp();
        postHttp.setOnResponseListener(new AbstractHttp.OnHttpResponseListener() {
            @Override
            public void onResponse(String response) {
                if (null != onResponseListener) {
                    onResponseListener.onResponse(response);
                }
            }
        });
        postHttp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                PrefsManager.apiDomain + PrefsManager.apiUrl + uri,
                body
        );
    }

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    public interface OnResponseListener {
        void onResponse(String response);
    }
}
