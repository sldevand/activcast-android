package fr.sldevand.activcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
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
    public static final Pattern YOUTUBE_PAGE_LINK = Pattern.compile("(http|https)://(www\\.|m.|)youtube\\.com/watch\\?v=(.+?)( |\\z|&)");
    public static final Pattern YOUTUBE_SHORT_LINK = Pattern.compile("(http|https)://(www\\.|)youtu.be/(.+?)( |\\z|&)");

    protected String baseUrl;
    protected EditText editText;
    protected ImageButton playButton;
    protected ImageButton stopButton;
    protected ImageButton fwd30Button;
    protected ImageButton back30Button;
    protected ImageButton fwd600Button;
    protected ImageButton back600Button;
    protected Button launchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnectivity();
        displayVersionTextView();
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

        fwd30Button = findViewById(R.id.button_ff);
        fwd30Button.setEnabled(false);
        GetHttpButton fwd30HttpButton = new GetHttpButton(this, fwd30Button, baseUrl + "/command/fwd30");
        fwd30HttpButton.setOnResponseListener(new AbstractHttpButton.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                setButtonsStates(response);
            }
        });

        back30Button = findViewById(R.id.button_rewind);
        back30Button.setEnabled(false);
        GetHttpButton back30HttpButton = new GetHttpButton(this, back30Button, baseUrl + "/command/back30");
        back30HttpButton.setOnResponseListener(new AbstractHttpButton.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                setButtonsStates(response);
            }
        });

        fwd600Button = findViewById(R.id.button_next);
        fwd600Button.setEnabled(false);
        GetHttpButton fwd600HttpButton = new GetHttpButton(this, fwd600Button, baseUrl + "/command/fwd600");
        fwd600HttpButton.setOnResponseListener(new AbstractHttpButton.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                setButtonsStates(response);
            }
        });

        back600Button = findViewById(R.id.button_previous);
        back600Button.setEnabled(false);
        GetHttpButton back600HttpButton = new GetHttpButton(this, back600Button, baseUrl + "/command/back600");
        back600HttpButton.setOnResponseListener(new AbstractHttpButton.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                setButtonsStates(response);
            }
        });

        launchButton = findViewById(R.id.launch_button);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extract(editText.getText().toString());
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

        Matcher matcherShortLink = YOUTUBE_SHORT_LINK.matcher(ytLink);
        Matcher matcherPageLink = YOUTUBE_PAGE_LINK.matcher(ytLink);

        if (!matcherShortLink.find() && !matcherPageLink.find()) {
            Toaster.shortToast(this, R.string.youtube_pattern_no_match);
            return;
        }

        try {
            //Convert youtube link on Android device
            YtUrlResolver ytUrlResolver = new YtUrlResolver();
            ytUrlResolver.setResolvedUrlListener(this);
            YtUrlResolver.resolve(this, ytLink);
        } catch (Exception exception) {
            String url = baseUrl + "/yt";
            //Convert youtube link with youtube-dl on Raspbian if android device cannot
            launchVideo(url, ytLink);
        }
    }

    @Override
    public void onResolvedUrl(String ytUrl) {
        String url = baseUrl + "/omx";
        launchVideo(url, ytUrl);
    }

    public void launchVideo(String url, String videoUrl) {

        Toaster.shortToast(getApplicationContext(), url);
        try {
            PostHttp launchPostHttp = new PostHttp();
            launchPostHttp.setOnResponseListener(launchResponseListener());

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("url", videoUrl);
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
                fwd30Button.setEnabled(running);
                fwd600Button.setEnabled(running);
                back30Button.setEnabled(running);
                back600Button.setEnabled(running);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toaster.shortToast(getApplicationContext(), e.getMessage());
        }
    }

    private void displayVersionTextView() {
        TextView versionTextView = findViewById(R.id.versionTextView);
        try {
            String versionName = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0)
                    .versionName;
            versionTextView.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
