package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;

public class nontonView extends AppCompatActivity {

    private JahrulnrLib it;
    private Activity act;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_nonton_view);
        act = this;
        it = new JahrulnrLib(this);

        Intent intent = getIntent();
        String nama = intent.getStringExtra("nama"),
                img_link = intent.getStringExtra("img_link"),
                anime_link = intent.getStringExtra("anime_link"),
                episode = intent.getStringExtra("episode"),
                eps_link = intent.getStringExtra("eps_link");
        animeList animelist = new animeList();
        episodeList epsList = new episodeList();
        WebView webView = findViewById(R.id.nontonAnime);
        if (eps_link != null) {
            it.executer(() -> {
                String h = JahrulnrLib.getUniversalRequest(eps_link);
                Matcher videoM = JahrulnrLib.preg_match(h,
                        "<iframe width=\"100%\" height=\"100%\" src=\"?(.*?)\" frameborder=\"0\"");

                if (videoM.find()) {
                    runOnUiThread(() -> {
                        // set webview
                        webView.setNetworkAvailable(true);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setAppCacheEnabled(true);
                        webView.getSettings().setAppCachePath(act.getCacheDir().getPath());
                        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                        webView.getSettings().setBuiltInZoomControls(true);
                        webView.loadUrl(videoM.group(1));

                        // save history
                        animelist.nama = nama;
                        animelist.img_link = img_link;
                        animelist.link = anime_link;
                        epsList.animeList = animelist;
                        epsList.episode = episode;
                        epsList.link = eps_link;
                        dbFiles db = new dbFiles(this);
                        db.add(epsList);
                        db.save();

                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Link not available", Toast.LENGTH_LONG).show();
                        finish();
                    });
                }
            });
        } else {
            Toast.makeText(this, "Link not available", Toast.LENGTH_LONG).show();
            finish();
        }

        Button moreEps = findViewById(R.id.moreEps);
        moreEps.setOnClickListener(view -> {
            Log.e("moreEps", "holaa");

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        act.finish();
    }
}