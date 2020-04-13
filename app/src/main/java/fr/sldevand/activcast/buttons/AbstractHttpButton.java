package fr.sldevand.activcast.buttons;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

abstract public class AbstractHttpButton {

    String commandUrl;
    Context context;
    OnResponseListener onResponseListener;

    public interface OnResponseListener {
        void onResponse(String response);
    }

    AbstractHttpButton(Context context, ImageButton imageButton, String commandUrl) {
        this.commandUrl = commandUrl;
        this.context = context;

        imageButton.setOnClickListener(onClickListener());
    }

    private View.OnClickListener onClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHttpRequest();
            }
        };
    }

    abstract public void launchHttpRequest();

    public void setOnResponseListener(OnResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }
}
