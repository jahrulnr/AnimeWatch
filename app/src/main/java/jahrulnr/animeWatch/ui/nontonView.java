package jahrulnr.animeWatch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
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

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private String htmlWebView = "";
    private RelativeLayout loadingFullscreen, loadingGridView;
    private ImageButton moreEps, serverBtn;
    private WebView webView;
    private RelativeLayout epsContainer;
    private LinearLayout serverContainer;
    private TextView nameEps, thisEps;
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
    protected void onCreate(Bundle savedInstanceState) {
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
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonton_view);
    }

    private void setup() {
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
                setWebURL(server, () -> {
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
                    for (episodeList e : eps) {
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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onStart() {
        super.onStart();

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(getAssets().open("webView/nontonWebView.html")));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
            htmlWebView = total.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.grid_animation);
        LayoutAnimationController animationController = new LayoutAnimationController(animation);

        // find by id
        loadingGridView = findViewById(R.id.loadingGridView);
        loadingFullscreen = findViewById(R.id.loadingFullscreen);
        epsContainer = findViewById(R.id.EpsContainer);
        serverContainer = findViewById(R.id.serverContainer);
        gridView = findViewById(R.id.EpsGridview);
        webView = findViewById(R.id.nontonAnime);
        nameEps = findViewById(R.id.tEpsLain);
        thisEps = findViewById(R.id.thisEps);
        GridView serverGridView = serverContainer.findViewById(R.id.serverGridview);
        epsContainer.setLayoutAnimation(animationController);
        serverContainer.setLayoutAnimation(animationController);
        epsContainer.setVisibility(View.GONE);
        epsListAdapter = new nontonEpsListAdapter(this, eps);
        gridView.setAdapter(epsListAdapter);

        it = new JahrulnrLib(this);
        Intent intent = getIntent();
        animelist.nama = StringEscapeUtils.unescapeJava(intent.getStringExtra("nama"));
        animelist.img_link = StringEscapeUtils.unescapeJava(intent.getStringExtra("img_link"));
        animelist.link = intent.getStringExtra("anime_link");
        epsList.animeList = animelist;
        epsList.episode = intent.getStringExtra("episode");
        epsList.link = intent.getStringExtra("episode_link");
        server = intent.getStringExtra("server");

        // set webview
        webView.setNetworkAvailable(true);
        webView.setBackgroundColor(Color.BLACK);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDomStorageEnabled(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setAppCachePath(getCacheDir().getAbsolutePath());
        System.out.println(getCacheDir().getAbsolutePath());
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    it.animate(loadingFullscreen, false);
                } else if(loadingFullscreen.getVisibility()==View.GONE){
                    it.animate(loadingFullscreen, true);
                }
            }
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                System.out.println("console.log: "+ consoleMessage.message());
                return true;
            }
        });

        episodeServerAdapter = new episodePreview.episodeServerAdapter(this, serverList);
        serverGridView.setAdapter(episodeServerAdapter);
        setup();

        moreEps = findViewById(R.id.moreEps);
        moreEps.setOnClickListener(view -> epsMenu());
        thisEps.setOnClickListener(view -> {
            if (!eps.isEmpty()) {
                gridView.requestFocus();
                gridView.post(() -> {
                    if(loadingGridView.getVisibility()==View.GONE)
                        gridView.smoothScrollToPosition(idEps);
                });
            }
        });

        serverBtn = findViewById(R.id.videoServer);
        serverBtn.setOnClickListener(view -> {
            if (serverContainer.getVisibility() == View.GONE) {
                serverBtn.setColorFilter(getResources().getColor(R.color.info));
                it.animate(serverContainer, true);
            } else {
                serverBtn.setColorFilter(getResources().getColor(R.color.white));
                it.animate(serverContainer, false);
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            this.epsList = this.eps.get(position);
            this.epsList.animeList = this.animelist;
            this.server = null;
            setup();
            epsMenu();
        });

        epsContainer.setOnClickListener(view -> epsMenu());
        serverGridView.setOnItemClickListener((adapterView, view, i, l)
                -> setWebURL(serverList.get(i).server, ()->epsMenu()));
    }

    private void setWebURL(String request, @Nullable Runnable code) {
        it.executer(() -> {
            String s = JahrulnrLib.getRequest(config.apiLink, request, null)
                    .replaceAll("\\Q<script\\E(.*?)\\Q</script>\\E", "")
                    .replace("src=\"//", "src=\"https://");
            runOnUiThread(() -> {
                if (!htmlWebView.isEmpty()) {
                    webView.loadData(htmlWebView.replace("__code__", s), "text/html", "UTF-8");
                    System.out.println(htmlWebView.replace("__code__", s));
                } else {
                    webView.loadData(s, "text/html", "UTF-8");
                }

                if(code != null)
                    code.run();
            });
        });
    }

    private void epsMenu() {
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
        if (idEps > -1)
            gridView.post(() -> {
                if(loadingGridView.getVisibility()==View.GONE)
                    gridView.smoothScrollToPosition(idEps + 3 < eps.size() ? idEps + 3 : idEps);
            });
    }

    @Override
    public void onBackPressed() {
        if (epsShow)
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