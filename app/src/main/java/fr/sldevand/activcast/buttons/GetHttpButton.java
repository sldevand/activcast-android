package fr.sldevand.activcast.buttons;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageButton;

import fr.sldevand.activcast.network.AbstractHttp;
import fr.sldevand.activcast.network.GetHttp;

public class GetHttpButton extends AbstractHttpButton {

    public GetHttpButton(Context context, ImageButton imageButton, String commandUrl) {
        super(context, imageButton, commandUrl);
    }

    @Override
    public void launchHttpRequest() {
        GetHttp getHttp = new GetHttp();
        getHttp.setOnResponseListener(new AbstractHttp.OnHttpResponseListener() {
            @Override
            public void onResponse(String response) {
                if (null != onResponseListener) {
                    onResponseListener.onResponse(response);
                }
            }
        });
        getHttp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, commandUrl);
    }
}
