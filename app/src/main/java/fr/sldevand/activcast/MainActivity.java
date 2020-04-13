package fr.sldevand.activcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import fr.sldevand.activcast.activity.SettingsActivity;
import fr.sldevand.activcast.network.GetHttp;
import fr.sldevand.activcast.network.HttpParamsBuilder;
import fr.sldevand.activcast.network.NetworkUtil;
import fr.sldevand.activcast.network.PostHttp;
import fr.sldevand.activcast.service.YtUrlResolver;

public class MainActivity extends AppCompatActivity implements YtUrlResolver.OnResolvedUrlListener, PostHttp.OnHttpResponseListener, GetHttp.OnHttpResponseListener {

    protected String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnectivity();

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        baseUrl = sharedPreferences.getString(getString(R.string.api_url_key), "");

        ImageButton playButton = findViewById(R.id.button_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetHttp getHttp = new GetHttp();
                getHttp.setOnResponseListener(new GetHttp.OnHttpResponseListener() {
                    @Override
                    public void onGetResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    }
                });
                getHttp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, baseUrl + "/command/play");
            }
        });

        ImageButton stopButton = findViewById(R.id.button_stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetHttp getHttp = new GetHttp();
                getHttp.setOnResponseListener(new GetHttp.OnHttpResponseListener() {
                    @Override
                    public void onGetResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    }
                });
                getHttp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, baseUrl + "/command/stop");
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

        this.extract(ytLink);
    }

    private void extract(String ytLink) {
        if (null == ytLink) {
            Toast.makeText(this, R.string.no_yt_link, Toast.LENGTH_LONG).show();
            finish();
        }

        YtUrlResolver ytUrlResolver = new YtUrlResolver();
        ytUrlResolver.setResolvedUrlListener(this);
        YtUrlResolver.resolve(this, ytLink);
    }

    @Override
    public void onResolvedUrl(String ytUrl) {
        String url = baseUrl + "/omx";
        Toast.makeText(getApplicationContext(),url, Toast.LENGTH_SHORT).show();
        Log.e("onResolvedUrl", url);
        try {
            PostHttp postHttp = new PostHttp();
            postHttp.setOnResponseListener(this);
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("url", ytUrl);
            String body = HttpParamsBuilder.buildString(params);
            postHttp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, body);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetResponse(String response) {
        Log.e("IN GET RESPONSE", response);
    }

    @Override
    public void onPostResponse(String response) {
        Log.e("IN POST RESPONSE", response);
    }
}
