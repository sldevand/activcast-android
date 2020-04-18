package fr.sldevand.activcast;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import fr.sldevand.activcast.activity.SettingsActivity;
import fr.sldevand.activcast.helper.PrefsManager;
import fr.sldevand.activcast.network.NetworkUtil;
import fr.sldevand.activcast.service.AbstractService;
import fr.sldevand.activcast.service.CommandService;
import fr.sldevand.activcast.service.YtUrlResolver;
import fr.sldevand.activcast.utils.Toaster;

public class MainActivity extends AppCompatActivity {
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
    protected CommandService commandService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnectivity();
        displayVersionTextView();
        PrefsManager.launch(this);
        baseUrl = PrefsManager.apiUrl;

        commandService = new CommandService();
        commandService.setOnResponseListener(new AbstractService.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("running")) {
                        boolean running = Boolean.valueOf(String.valueOf(jsonObject.get("running")));
                        setButtonStates(running);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toaster.shortToast(getApplicationContext(), e.getMessage());
                }
            }
        });

        editText = findViewById(R.id.youtube_text_uri);

        playButton = findViewById(R.id.button_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandService.play();
            }
        });

        stopButton = findViewById(R.id.button_stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandService.stop();
            }
        });

        fwd30Button = findViewById(R.id.button_ff);
        fwd30Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandService.fwd30();
            }
        });

        back30Button = findViewById(R.id.button_rewind);
        back30Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandService.back30();
            }
        });


        fwd600Button = findViewById(R.id.button_next);
        fwd600Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandService.fwd600();
            }
        });

        back600Button = findViewById(R.id.button_previous);
        back600Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandService.back600();
            }
        });

        launchButton = findViewById(R.id.launch_button);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extract(editText.getText().toString());
            }
        });

        setButtonStates(false);

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

        //Convert youtube link on Android device
        YtUrlResolver ytUrlResolver = new YtUrlResolver();
        ytUrlResolver.setResolvedUrlListener(new YtUrlResolver.OnResolvedUrlListener() {
            @Override
            public void onResolvedUrl(String url) {
                launchVideo("/omx", url);
            }

            @Override
            public void onError(String message, String youtubeLink) {
                launchVideo("/yt", youtubeLink);
            }
        });
        launchButton.setEnabled(false);
        YtUrlResolver.resolve(this, ytLink);
    }

    public void launchVideo(String uri, String videoUrl) {
        try {
            commandService.launch(uri, videoUrl);
        } catch (Exception e) {
            commandService.isRunning();
            e.printStackTrace();
            Toaster.shortToast(getApplicationContext(), e.getMessage());
        }
    }

    public void setButtonStates(boolean running) {
        launchButton.setEnabled(!running);
        playButton.setEnabled(running);
        stopButton.setEnabled(running);
        fwd30Button.setEnabled(running);
        fwd600Button.setEnabled(running);
        back30Button.setEnabled(running);
        back600Button.setEnabled(running);
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
