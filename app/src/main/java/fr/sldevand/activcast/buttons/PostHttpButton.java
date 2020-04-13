package fr.sldevand.activcast.buttons;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Map;

import fr.sldevand.activcast.R;
import fr.sldevand.activcast.network.AbstractHttp;
import fr.sldevand.activcast.network.HttpParamsBuilder;
import fr.sldevand.activcast.network.PostHttp;
import fr.sldevand.activcast.utils.Toaster;

public class PostHttpButton extends AbstractHttpButton {

    private Map<String, TextView> fields;

    PostHttpButton(Context context, ImageButton imageButton, String commandUrl) {
        super(context, imageButton, commandUrl);
        this.fields = new LinkedHashMap<>();
    }

    public PostHttpButton addField(String name, TextView view) {
        this.fields.put(name, view);

        return this;
    }

    @Override
    public void launchHttpRequest() {
        if (fields.isEmpty()) {
            Toaster.shortToast(context, R.string.empty_fields);
            return;
        }

        PostHttp postHttp = new PostHttp();
        postHttp.setOnResponseListener(new AbstractHttp.OnHttpResponseListener() {
            @Override
            public void onResponse(String response) {
                if (null != onResponseListener) {
                    onResponseListener.onResponse(response);
                }
            }
        });

        try {
            /* Get Text on each added Textview */
            Map<String, Object> params = new LinkedHashMap<>();
            for (Map.Entry<String, TextView> entry : fields.entrySet()) {
                params.put(entry.getKey(), entry.getValue().getText().toString());
            }
            String body = HttpParamsBuilder.buildString(params);
            postHttp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, commandUrl, body);
        } catch (Exception exception) {
            Toaster.shortToast(context, R.string.no_fields);
        }
    }
}
