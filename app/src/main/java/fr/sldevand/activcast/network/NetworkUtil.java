package fr.sldevand.activcast.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

import fr.sldevand.activcast.R;

public class NetworkUtil {
	
	private static int TYPE_WIFI = 1;
	private static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;
	
	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
		if (null != activeNetwork) {
			if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;
			
			if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		} 
		return TYPE_NOT_CONNECTED;
	}
	
	static String getConnectivityStatusString(Context context) {
		int conn = NetworkUtil.getConnectivityStatus(context);
		String status = null;
		if (conn == NetworkUtil.TYPE_WIFI) {
			status = context.getString(R.string.wifi_enabled);
		} else if (conn == NetworkUtil.TYPE_MOBILE) {
			status = context.getString(R.string.mobile_data_enabled);
		} else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
			status = context.getString(R.string.not_connected_to_internet);
		}
		return status;
	}
}