package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.ui.TwoWayGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.animeClick;
import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.nontonEpsListAdapter;

public class nontonView extends AppCompatActivity {

    private static JahrulnrLib it;
    private Activity act;
    private ImageButton moreEps;
    private WebView webView;
    private RelativeLayout epsContainer;
    private TwoWayGridView gridView;
    private boolean epsShow = false;
    animeList animelist = new animeList();
    episodeList epsList = new episodeList();
    List<episodeList> eps = new ArrayList<>();
    int idEps = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // hide status bar and nav bar after a short delay, or if the user interacts with the middle of the screen
                );
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonton_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        act = this;
        it = new JahrulnrLib(this);
        Intent intent = getIntent();
        String nama = intent.getStringExtra("nama"),
                img_link = intent.getStringExtra("img_link"),
                anime_link = intent.getStringExtra("anime_link"),
                episode = intent.getStringExtra("episode"),
                eps_link = intent.getStringExtra("eps_link");
        epsContainer = findViewById(R.id.EpsContainer);
        gridView = findViewById(R.id.EpsGridview);
        epsContainer.setVisibility(View.GONE);
        webView = findViewById(R.id.nontonAnime);
        if (eps_link != null) {
            it.executer(() -> {
                String h = JahrulnrLib.getUniversalRequest(eps_link);
                Matcher videoM = JahrulnrLib.preg_match(h,
                        "<iframe width=\"100%\" height=\"100%\" src=\"?(.*?)\" frameborder=\"0\"");

                boolean found = videoM.find();
                if(!found){
                    videoM = JahrulnrLib.preg_match(h,
                            "<IFRAME SRC=\"?(.*?)\" allowfullscreen=\"true\"");
                    found = videoM.find();
                    System.out.println(videoM.group(1));
                }

                // set history
                animelist.nama = nama;
                animelist.img_link = img_link;
                animelist.link = anime_link;
                epsList.animeList = animelist;
                epsList.episode = episode;
                epsList.link = eps_link;

                if (found) {
                    Matcher finalVideoM = videoM;
                    runOnUiThread(() -> {
                        // set webview
                        webView.setNetworkAvailable(true);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setDomStorageEnabled(true);
                        webView.getSettings().setUseWideViewPort(true);
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setAppCacheEnabled(true);
                        webView.getSettings().setAppCachePath(act.getCacheDir().getPath());
                        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                        webView.getSettings().setBuiltInZoomControls(true);
                        webView.loadUrl(finalVideoM.group(1));

                        // save history
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

                if(eps.isEmpty()) {
                    animeClick anm = new animeClick();
                    String s = it.getUniversalRequest(anime_link);
                    eps = anm.getEpisode(s);
                    Collections.reverse(eps);
                    idEps = 0;
                    for (episodeList e : this.eps) {
                        if (e.episode.contains(epsList.episode)) {
                            break;
                        }
                        idEps++;
                    }
                }

                if(!eps.isEmpty()) {
                    runOnUiThread(() -> {
                        nontonEpsListAdapter adapter = new nontonEpsListAdapter(this, animelist, eps);
                        gridView.setAdapter(adapter);
                    });
                }
            });
        } else {
            Toast.makeText(this, "Link not available", Toast.LENGTH_LONG).show();
            finish();
        }

        moreEps = findViewById(R.id.moreEps);
        TextView thisEps = findViewById(R.id.thisEps);
        moreEps.setOnClickListener(view -> {
            epsMenu();
        });
        thisEps.setText(episode);
        thisEps.setOnClickListener(view -> {
            if(!eps.isEmpty()){
                gridView.smoothScrollToPosition(idEps);
            }
        });
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            episodeList eps = this.eps.get(position);
            animeList animelist = this.animelist;
            Intent i = new Intent(nontonView.this, nontonView.class);
            i.putExtra("nama", animelist.nama);
            i.putExtra("img_link", animelist.img_link);
            i.putExtra("anime_link", animelist.link);
            i.putExtra("episode", eps.episode);
            i.putExtra("eps_link", eps.link);
            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        });
        epsContainer.setOnClickListener(view -> {
            epsMenu();
        });
    }

    private void epsMenu(){
        if (epsShow) {
            webView.onResume();
            webView.setClickable(true);
            webView.setFocusable(true);
            webView.setFocusableInTouchMode(true);
            webView.setActivated(true);
            moreEps.setColorFilter(getResources().getColor(R.color.white));
            epsContainer.setVisibility(View.GONE);
            this.epsShow = false;
            return;
        }

        webView.onPause();
        webView.setClickable(false);
        webView.setFocusable(false);
        webView.setFocusableInTouchMode(false);
        webView.setActivated(false);
        moreEps.setColorFilter(getResources().getColor(R.color.info));
        epsContainer.setVisibility(View.VISIBLE);
        this.epsShow = true;
        if(idEps > -1) {
            act.runOnUiThread(() -> {
                gridView.post(() -> {
                    int tempId = idEps;
                    if(tempId + 3 < eps.size()){
                        tempId += 3;
                    }
                    gridView.smoothScrollToPosition(tempId);
                });
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(epsShow){
            epsMenu();
        }else {
            finish();
            super.onBackPressed();
        }
    }
}