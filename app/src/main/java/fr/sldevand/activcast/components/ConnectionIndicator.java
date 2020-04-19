package fr.sldevand.activcast.components;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import fr.sldevand.activcast.R;

public class ConnectionIndicator {

    private ImageView imageView;
    private TextView textView;

    public ConnectionIndicator(ImageView imageView, TextView textView) {
        this.imageView = imageView;
        this.textView = textView;
    }

    public void setConnected(Context context) {
        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_check_circle_black_24dp));
        imageView.getDrawable().setTint(context.getColor(R.color.success));
        textView.setText(R.string.connected);
    }

    public void setDisconnected(Context context) {
        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_error_black_24dp));
        imageView.getDrawable().setTint(context.getColor(R.color.warning));
        textView.setText(R.string.disconnected);
    }

    public void setTimeout(Context context) {
        imageView.setImageDrawable(context.getDrawable(R.drawable.ic_error_black_24dp));
        imageView.getDrawable().setTint(context.getColor(R.color.warning));
        textView.setText(R.string.timeout);
    }
}
