package fr.sldevand.activcast.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

import fr.sldevand.activcast.R;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private OnNetworkChangedListener onNetworkChangedListener;

    public void setOnNetworkChangedListener(OnNetworkChangedListener onNetworkChangedListener) {
        this.onNetworkChangedListener = onNetworkChangedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.requireNonNull(intent.getAction()).equals(context.getString(R.string.wifi_state_change))) {
            Integer status = NetworkUtil.getConnectivityStatus(context);
            String statusStr = NetworkUtil.getConnectivityStatusString(context);

            if (null != onNetworkChangedListener)
                onNetworkChangedListener.onChange(status, statusStr);
        }
    }

    public interface OnNetworkChangedListener {
        void onChange(Integer status, String statusStr);
    }
}
