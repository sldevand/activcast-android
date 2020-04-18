package fr.sldevand.activcast.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import fr.sldevand.activcast.R;

public class PrefsManager {

    public static String apiUrl;
    private static SharedPreferences sharedPreferences;

    public static void launch(Context context) {
        if (null != sharedPreferences) {
            return;
        }
        Resources res = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        apiUrl = sharedPreferences.getString(res.getString(R.string.api_url_key), "");
    }

    public static boolean areTherePrefs() {
        return !"".equals(apiUrl);
    }
}
