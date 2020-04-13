package fr.sldevand.activcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.sldevand.activcast.activity.SettingsActivity;
import fr.sldevand.activcast.buttons.AbstractHttpButton;
import fr.sldevand.activcast.buttons.GetHttpButton;
import fr.sldevand.activcast.network.AbstractHttp;
import fr.sldevand.activcast.network.HttpParamsBuilder;
import fr.sldevand.activcast.network.NetworkUtil;
import fr.sldevand.activcast.network.PostHttp;
import fr.sldevand.activcast.service.YtUrlResolver;
import fr.sldevand.activcast.utils.Toaster;

public class MainActivity extends AppCompatActivity implements YtUrlResolver.OnResolvedUrlListener {

    public static final String YOUTUBE_URL_PATTERN = "https://youtu.be/";

    protected String baseUrl;
    protected EditText editText;
    protected ImageButton playButton;
    protected ImageButton stopButton;
    protected Button launchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnectivity();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        baseUrl = sharedPreferences.getString(getString(R.string.api_url_key), "");
        editText = findViewById(R.id.youtube_text_uri);

        playButton = findViewById(R.id.button_play);
        playButton.setEnabled(false);
        GetHttpButton playHttpButton = new GetHttpButton(this, playButton, baseUrl + "/command/play");
        playHttpButton.setOnResponseListener(new AbstractHttpButton.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                setButtonsStates(response);
            }
        });

        stopButton = findViewById(R.id.button_stop);
        stopButton.setEnabled(false);
        GetHttpButton stopHttpButton = new GetHttpButton(this, stopButton, baseUrl + "/command/stop");
        stopHttpButton.setOnResponseListener(new AbstractHttpButton.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                setButtonsStates(response);
            }
        });

        launchButton = findViewById(R.id.launch_button);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ytLink = editText.getText().toString();
                extract(ytLink);
            }
        });

        if (null == savedInstanceState && Intent.ACTION_SEND.equals(getIntent().getAction())
                && getIntent().getType() != null && "text/plain".equals(getIntent().getType())
        ) {
            handleIntent();
        }
    }

    private void checkConnectivity() {
        Integer conStatus = NetworkUtil.getConnectivityStatus(this);
        routeViews(conStatus);
    }

    private void routeViews(Integer state) {
        if (NetworkUtil.TYPE_NOT_CONNECTED == state) {
            setContentView(R.layout.activity_main_error);
            return;
        }
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent prefIntent = new Intent(this, SettingsActivity.class);
            startActivity(prefIntent);
        }

        return true;
    }

    private void handleIntent() {
        String ytLink = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        editText.setText(ytLink);
        this.extract(ytLink);
    }

    private void extract(String ytLink) {
        if (null == ytLink) {
            Toaster.longToast(this, R.string.no_yt_link);
            return;
        }

        Pattern r = Pattern.compile(YOUTUBE_URL_PATTERN);
        Matcher m = r.matcher(ytLink);
        if (!m.lookingAt()) {
            Toaster.longToast(this, R.string.youtube_pattern_no_match);
            return;
        }

        YtUrlResolver ytUrlResolver = new YtUrlResolver();
        ytUrlResolver.setResolvedUrlListener(this);
        YtUrlResolver.resolve(this, ytLink);
    }

    @Override
    public void onResolvedUrl(String ytUrl) {
        String url = baseUrl + "/omx";
        Toaster.shortToast(getApplicationContext(), url);
        try {
            PostHttp launchPostHttp = new PostHttp();
            launchPostHttp.setOnResponseListener(launchResponseListener());

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("url", ytUrl);
            String body = HttpParamsBuilder.buildString(params);

            launchPostHttp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, body);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toaster.shortToast(getApplicationContext(), e.getMessage());
        }
    }

    public PostHttp.OnHttpResponseListener launchResponseListener() {
        return new AbstractHttp.OnHttpResponseListener() {
            @Override
            public void onResponse(String response) {
                setButtonsStates(response);
            }
        };
    }

    public void setButtonsStates(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("running")) {
                boolean running = Boolean.valueOf(String.valueOf(jsonObject.get("running")));
                launchButton.setEnabled(!running);
                playButton.setEnabled(running);
                stopButton.setEnabled(running);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toaster.shortToast(getApplicationContext(), e.getMessage());
        }
    }
}
