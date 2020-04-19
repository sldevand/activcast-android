package fr.sldevand.activcast.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.preference.PreferenceManager;

import fr.sldevand.activcast.R;

public class PrefsManager {

    public static String apiDomain;
    public static String apiUrl;
    public static String socketIOPort;
    private static SharedPreferences sharedPreferences;

    public static void launch(Context context) {
        if (null != sharedPreferences) {
            return;
        }
        Resources res = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        apiDomain = sharedPreferences.getString(res.getString(R.string.domain_url_key), "");
        apiUrl = sharedPreferences.getString(res.getString(R.string.api_url_key), "");
        socketIOPort = sharedPreferences.getString(res.getString(R.string.socket_io_port_key), "");
    }

    public static boolean areTherePrefs() {
        return !"".equals(apiDomain) || !"".equals(apiUrl) || !"".equals(socketIOPort);
    }
}
