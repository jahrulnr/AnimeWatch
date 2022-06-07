package jahrulnr.animeWatch.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jess.ui.TwoWayGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.animeClick;
import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.Class.episodePreview;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.nontonEpsListAdapter;
import jahrulnr.animeWatch.config;

public class nontonView extends AppCompatActivity {

    private JahrulnrLib it;
    private RelativeLayout loadingFullscreen,  loadingGridView;
    private ImageButton moreEps, serverBtn;
    private WebView webView;
    private RelativeLayout epsContainer;
    private LinearLayout serverContainer;
    private TextView nameEps, thisEps;
    private GridView serverGridView;
    private TwoWayGridView gridView;
    private boolean epsShow = false;
    private final animeList animelist = new animeList();
    private episodeList epsList = new episodeList();
    private final List<episodeList> eps = new ArrayList<>();
    private nontonEpsListAdapter epsListAdapter;
    private int idEps = -1;
    private String server;
    private final List<episodePreview.episodeServerList> serverList = new ArrayList<>();
    private episodePreview.episodeServerAdapter episodeServerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
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
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonton_view);
    }

    private void setup(){
        it.animate(loadingFullscreen, true);
        nameEps.setText(animelist.nama);
        thisEps.setText(epsList.episode);
        it.animate(loadingGridView, true);
        it.executer(() -> {
            String source = JahrulnrLib.getRequest(epsList.link, null);
            serverList.clear();
            serverList.addAll(episodePreview.getServer(source));
            episodeServerAdapter.notifyDataSetChanged();

            if (server == null && !serverList.isEmpty()) {
                // default get 0
                server = serverList.get(0).server;
            }

            if (server != null) {
                // set history
                String h = JahrulnrLib.getRequest(config.apiLink, server, null);
                runOnUiThread(() -> {
                    try {
                        String s = h.replaceAll("\\Q<script\\E(.*?)\\Q</script>\\E", "");
                        Matcher sM = JahrulnrLib.preg_match(s, "(.*)\\Qsrc=\"\\E((http)?(s)?(:)?//.*?/.*?)\\\" ?");
                        if (sM.find()) {
                            s = sM.group(2);
                            if (sM.group(3) == null) s = "https:" + s;
                            webView.loadUrl(s);
                        } else {
                            webView.loadData(
                                    "<html><body style=\"padding: 0; margin: 0;width: 100%; height:100%;background:black\">" +
                                            h +
                                            "</body></html>", "text/html", "UTF-8");
                        }
                        webView.post(() -> {
                            it.animate(loadingFullscreen, false);
                        });
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        finish();
                    }

                    // save history
                    dbFiles db = new dbFiles(this);
                    db.add(epsList);
                    db.save();
                });

                if (eps.isEmpty()) {
                    animeClick anm = new animeClick();
                    String s = JahrulnrLib.getRequest(animelist.link, null).replaceAll("\n", "");
                    Matcher getAnimeID = JahrulnrLib.preg_match(s,
                            "\\Qname=\"series_id\" value=\"\\E([0-9]+?)\\Q\">\\E");
                    if (getAnimeID.find()) {
                        String p = "misha_number_of_results=100000" +
                                "&misha_order_by=date-DESC" +
                                "&action=mishafilter" +
                                "&series_id=" + getAnimeID.group(1);
                        eps.addAll(anm.getEpisode(p, config.episode_pattern1, true));
                    } else {
                        eps.addAll(anm.getEpisode(s, config.episode_pattern2, false));
                    }
                    Collections.reverse(eps);
                    idEps = 0;
                    for (episodeList e : this.eps) {
                        if (e.episode.contains(epsList.episode)) {
                            break;
                        }
                        idEps++;
                    }
                }

                if (!eps.isEmpty()) {
                    runOnUiThread(() -> {
                        epsListAdapter.notifyDataSetChanged();
                        it.animate(loadingGridView, false);
                    });
                }
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Link not available", Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.grid_animation);
        LayoutAnimationController animationController = new LayoutAnimationController(animation);

        // find by id
        loadingGridView = findViewById(R.id.loadingGridView);
        loadingFullscreen = findViewById(R.id.loadingFullscreen);
        epsContainer = findViewById(R.id.EpsContainer);
        serverContainer = findViewById(R.id.serverContainer);
        gridView = findViewById(R.id.EpsGridview);
        webView = findViewById(R.id.nontonAnime);
        nameEps  = findViewById(R.id.tEpsLain);
        thisEps = findViewById(R.id.thisEps);
        serverGridView = serverContainer.findViewById(R.id.serverGridview);
        epsContainer.setLayoutAnimation(animationController);
        serverContainer.setLayoutAnimation(animationController);
        epsContainer.setVisibility(View.GONE);
        epsListAdapter = new nontonEpsListAdapter(this, animelist, eps);
        gridView.setAdapter(epsListAdapter);

        it = new JahrulnrLib(this);
        Intent intent = getIntent();
        animelist.nama = intent.getStringExtra("nama");
        animelist.img_link = intent.getStringExtra("img_link");
        animelist.link = intent.getStringExtra("anime_link");
        epsList.animeList = animelist;
        epsList.episode = intent.getStringExtra("episode");
        epsList.link = intent.getStringExtra("episode_link");
        server = intent.getStringExtra("server");

        // set webview
        webView.setNetworkAvailable(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        webView.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setUserAgentString(new WebView(this).getSettings().getUserAgentString());
        webView.loadData("<html><body style=\"padding: 0; margin: 0;width: 100%; height:100%;background:black\"></body></html>",
                "text/html", "UTF-8");

        episodeServerAdapter = new episodePreview.episodeServerAdapter(this, serverList);
        serverGridView.setAdapter(episodeServerAdapter);
        setup();

        webView.setOnLongClickListener(view -> {
            unregisterForContextMenu(webView);
            WebView.HitTestResult result = webView.getHitTestResult();
            if(result.getType() == 7){
                registerForContextMenu(webView);
            }

            return false;
        });

        moreEps = findViewById(R.id.moreEps);
        moreEps.setOnClickListener(view -> epsMenu());
        thisEps.setOnClickListener(view -> {
            if (!eps.isEmpty()) {
                gridView.requestFocus();
                gridView.smoothScrollToPosition(idEps);
            }
        });

        serverBtn = findViewById(R.id.videoServer);
        serverBtn.setOnClickListener(view -> {
            if(serverContainer.getVisibility() == View.GONE){
                serverBtn.setColorFilter(getResources().getColor(R.color.info));
                it.animate(serverContainer, true);
            }else{
                serverBtn.setColorFilter(getResources().getColor(R.color.white));
                it.animate(serverContainer, false);
            }
        });

        serverGridView.setOnItemClickListener((adapterView, view, i, l) -> {
            try {
                it.animate(loadingFullscreen, true);
                it.executer(() -> {
                    String s = JahrulnrLib.getRequest(config.apiLink, serverList.get(i).server, null)
                            .replaceAll("\\Q<script\\E(.*?)\\Q</script>\\E", "");
                    Matcher sM = JahrulnrLib.preg_match(s, "(.*)\\Qsrc=\"\\E((http)?(s)?(:)?//.*?/.*?)\\\" ?");
                    runOnUiThread(() -> {
                        if(sM.find()){
                            webView.loadUrl(sM.group(3) == null ? "https:" + s : sM.group(2));
                        }
                        else{
                            webView.loadData("<html><body style=\"padding: 0; margin: 0;width: 100%; height:100%;background:black\">" +
                                    s +
                                    "</body></html>", "text/html", "UTF-8");
                        }
                        epsMenu();
                        it.animate(loadingFullscreen, false);
                    });
                });
            } catch (IllegalStateException e) {
                e.printStackTrace();
                finish();
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            this.epsList = this.eps.get(position);
            this.epsList.animeList = this.animelist;
            this.server = null;
            setup();
            epsMenu();
            System.out.println(epsList);
        });

        epsContainer.setOnClickListener(view -> epsMenu());
    }

    private void epsMenu(){
        if (epsShow) {
            webView.resumeTimers();
            moreEps.setColorFilter(getResources().getColor(R.color.white));
            serverBtn.setColorFilter(getResources().getColor(R.color.white));
            it.animate(epsContainer, false);
            it.animate(serverContainer, false);
            epsShow = false;
            return;
        }

        webView.pauseTimers();
        moreEps.setColorFilter(getResources().getColor(R.color.info));
        epsContainer.startLayoutAnimation();
        it.animate(epsContainer, true);
        epsShow = true;
        if(idEps > -1) {
            gridView.post(() -> {
                int tempId = idEps;
                if(tempId + 3 < eps.size()){
                    tempId += 3;
                }
                gridView.smoothScrollToPosition(tempId);
            });
        }
    }

    @Override
    public void onBackPressed() {
        if(epsShow)
            epsMenu();
        else
            finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.pauseTimers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
    }
}