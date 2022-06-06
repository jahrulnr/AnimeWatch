package jahrulnr.animeWatch.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import com.jess.ui.TwoWayGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.animeClick;
import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.Class.episodePreview;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.nontonEpsListAdapter;

public class nontonView extends AppCompatActivity {

    private static JahrulnrLib it;
    private RelativeLayout loading;
    private ImageButton moreEps, serverBtn;
    private WebView webView;
    private RelativeLayout epsContainer;
    private LinearLayout serverContainer;
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
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.grid_animation);
        LayoutAnimationController animationController = new LayoutAnimationController(animation);

        // find by id
        loading = findViewById(R.id.loadingContainer);
        epsContainer = findViewById(R.id.EpsContainer);
        serverContainer = findViewById(R.id.serverContainer);
        gridView = findViewById(R.id.EpsGridview);
        webView = findViewById(R.id.nontonAnime);
        TextView nameEps  = findViewById(R.id.tEpsLain);
        TextView thisEps = findViewById(R.id.thisEps);
        GridView serverGridView = serverContainer.findViewById(R.id.serverGridview);
        epsContainer.setLayoutAnimation(animationController);
        serverContainer.setLayoutAnimation(animationController);
        loading.setVisibility(View.VISIBLE);
        webView.loadData("<html><body style=\"padding: 0; margin: 0;width: 100%; height:100%;background:black\"></body></html>",
                "text/html", "UTF-8");

        it = new JahrulnrLib(this);
        Intent intent = getIntent();
        String nama = intent.getStringExtra("nama"),
                img_link = intent.getStringExtra("img_link"),
                anime_link = intent.getStringExtra("anime_link"),
                episode = intent.getStringExtra("episode"),
                episode_link = intent.getStringExtra("episode_link"),
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
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setUserAgentString(new WebView(this).getSettings().getUserAgentString());

        String source = JahrulnrLib.getRequest(episode_link, null);
        List<episodePreview.episodeServerList> serverList = episodePreview.getServer(source);
        serverGridView.setAdapter(new episodePreview.episodeServerAdapter(this, serverList));
        if(server == null && !serverList.isEmpty()){
            // default get 0
            server = serverList.get(0).server;
//            server = it.getRequest(JahrulnrLib.config.apiLink, serverList.get(0).server, null);
        }

        epsContainer.setVisibility(View.GONE);

        if (server != null) {
            String finalServer = server;
            nameEps.setText(nama);
            it.executer(() -> {
                // set history
                System.out.println("finalServer: " + finalServer);
                AtomicReference<String> h = new AtomicReference<>(JahrulnrLib.getRequest(JahrulnrLib.config.apiLink, finalServer, null));
                runOnUiThread(() -> {
                    animelist.nama = nama;
                    animelist.img_link = img_link;
                    animelist.link = anime_link;
                    epsList.animeList = animelist;
                    epsList.episode = episode;
                    epsList.link = episode_link;
                    try {
                        String s = h.get().replaceAll("\\Q<script\\E(.*?)\\Q</script>\\E", "");
                        Matcher sM = it.preg_match(s, "(.*)\\Qsrc=\"\\E((http)?(s)?(:)?//.*?/.*?)\\\" ?");
                        if(sM.find()){
                            s = sM.group(2);
                            if(sM.group(3) == null) s = "https:" + s;
                            webView.loadUrl(s);
                            System.out.println("Matcher used");
                        }
                        else{
                            h.set("<html><body style=\"padding: 0; margin: 0;width: 100%; height:100%;background:black\">" +
                                    h.get() +
                                    "</body></html>");
                            webView.loadData(h.get(), "text/html", "UTF-8");
                            System.out.println("iframe used");
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        finish();
                    }

                    // save history
                    dbFiles db = new dbFiles(this);
                    db.add(epsList);
                    db.save();
                });

                if(eps.isEmpty()) {
                    animeClick anm = new animeClick();
                    System.out.println("anime_link: " + anime_link);
                    String s = it.getRequest(anime_link, null);
                    Matcher getAnimeID = it.preg_match(s.replaceAll("\n", ""),
                            "\\Qname=\"series_id\" value=\"\\E([0-9]+?)\\Q\">\\E");
                    if(getAnimeID.find()) {
                        String p = "misha_number_of_results=100000" +
                                "&misha_order_by=date-DESC" +
                                "&action=mishafilter" +
                                "&series_id=" + getAnimeID.group(1);
                        eps = anm.getEpisode(p, JahrulnrLib.config.episode_pattern1, true);
                    }else {
                        eps = anm.getEpisode(s, JahrulnrLib.config.episode_pattern2, false);
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

                if(!eps.isEmpty()) {
                    runOnUiThread(() -> {
                        nontonEpsListAdapter adapter = new nontonEpsListAdapter(this, animelist, eps);
                        gridView.setAdapter(adapter);
                        loading.setVisibility(View.GONE);
                    });
                }
            });
        } else {
            Toast.makeText(this, "Link not available", Toast.LENGTH_LONG).show();
            finish();
        }

        webView.setOnLongClickListener(view -> {
            unregisterForContextMenu(webView);
            WebView.HitTestResult result = webView.getHitTestResult();
            System.out.println("Im here");
            if(result.getType() == 7){
                registerForContextMenu(webView);
            }

            return false;
        });

        moreEps = findViewById(R.id.moreEps);
        moreEps.setOnClickListener(view -> epsMenu());
        thisEps.setText(episode);
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

        List<episodePreview.episodeServerList> finalServerList = serverList;
        serverGridView.setOnItemClickListener((adapterView, view, i, l) -> {
            try {
                AtomicReference<String> s = new AtomicReference<>();
                it.executer(() -> {
                    s.set(it.getRequest(it.config.apiLink, finalServerList.get(i).server, null)
                            .replaceAll("\\Q<script\\E(.*?)\\Q</script>\\E", ""));
                    Matcher sM = it.preg_match(s.get(), "(.*)\\Qsrc=\"\\E((http)?(s)?(:)?//.*?/.*?)\\\" ?");
                    runOnUiThread(() -> {
                        if(sM.find()){
                            s.set(sM.group(2));
                            if(sM.group(3) == null) s.set("https:" + s);
                            webView.loadUrl(s.get());
                            System.out.println("Matcher used");
                        }
                        else{
                            s.set("<html><body style=\"padding: 0; margin: 0;width: 100%; height:100%;background:black\">" +
                                    s +
                                    "</body></html>");
                            webView.loadData(s.get(), "text/html", "UTF-8");
                            System.out.println("iframe used");
                        }
                        epsMenu();
                    });
                });
            } catch (IllegalStateException e) {
                e.printStackTrace();
                finish();
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            episodeList eps = this.eps.get(position);
            animeList animelist = this.animelist;
            System.out.println(eps);
            Intent i = new Intent(nontonView.this, nontonView.class);
            i.putExtra("nama", animelist.nama);
            i.putExtra("img_link", animelist.img_link);
            i.putExtra("anime_link", animelist.link);
            i.putExtra("episode", eps.episode);
            i.putExtra("episode_link", eps.link);
            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            finish();
        });

        epsContainer.setOnClickListener(view -> {
            epsMenu();
        });
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