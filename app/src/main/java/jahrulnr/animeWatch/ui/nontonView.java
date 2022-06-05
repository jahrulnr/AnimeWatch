package jahrulnr.animeWatch.ui;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // find by id
        epsContainer = findViewById(R.id.EpsContainer);
        gridView = findViewById(R.id.EpsGridview);
        webView = findViewById(R.id.nontonAnime);
        moreEps = findViewById(R.id.moreEps);
        TextView thisEps = findViewById(R.id.thisEps);

        it = new JahrulnrLib(this);
        Intent intent = getIntent();
        String nama = intent.getStringExtra("nama"),
                img_link = intent.getStringExtra("img_link"),
                anime_link = intent.getStringExtra("anime_link"),
                episode = intent.getStringExtra("episode"),
                episode_link = intent.getStringExtra("episode_link"),
                server = intent.getStringExtra("server");

        epsContainer.setVisibility(View.GONE);

        if (server != null) {
            it.executer(() -> {
                AtomicReference<String> h = new AtomicReference<>(JahrulnrLib.getRequest(JahrulnrLib.config.apiLink, server, null));

                // set history
                animelist.nama = nama;
                animelist.img_link = img_link;
                animelist.link = anime_link;
                epsList.animeList = animelist;
                epsList.episode = episode;
                epsList.link = episode_link;

                runOnUiThread(() -> {
                    if(webView.getUrl() == null) {
                        // set webview
                        Map<String, String> extraHeaders = new HashMap<String, String>();
                        extraHeaders.put("Referer", JahrulnrLib.config.home);
                        webView.setNetworkAvailable(true);
                        webView.getSettings().setUseWideViewPort(true);
                        webView.getSettings().setAppCacheEnabled(true);
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.getSettings().setBuiltInZoomControls(true);
                        webView.getSettings().setDomStorageEnabled(true);
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setAppCachePath(getCacheDir().getPath());
                        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                        webView.getSettings().setUserAgentString(new WebView(this).getSettings().getUserAgentString());
                        try {
                            h.set("<body style=\"padding: 0; margin: 0;\">" + h + "</body>");
                            webView.loadData(h.get(), "text/html", "UTF-8");
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                            finish();
                        }

                        // save history
                        dbFiles db = new dbFiles(this);
                        db.add(epsList);
                        db.save();
                    }
                });

                boolean pattern = false;
                if(eps.isEmpty()) {
                    animeClick anm = new animeClick();
                    String s = it.getRequest(anime_link, null);
                    Matcher getAnimeID = it.preg_match(s.replaceAll("\n", ""),
                            "\\Qname=\"series_id\" value=\"\\E([0-9]+?)\\Q\">\\E");
                    System.out.println("anime_link "+anime_link);
                    if(getAnimeID.find()) {
                        String p = "misha_number_of_results=100000" +
                                "&misha_order_by=date-DESC" +
                                "&action=mishafilter" +
                                "&series_id=" + getAnimeID.group(1);
                        eps = anm.getEpisode(p, JahrulnrLib.config.episode_pattern1, true);
                        System.out.println("Im pattern 1");
                        if(eps != null) pattern = true;
                    }else {
                        eps = anm.getEpisode(s, JahrulnrLib.config.episode_pattern2, false);
                        System.out.println("Im pattern 2");
                        if(eps != null) pattern = true;
                    }
                    System.out.println("eps.size() " + eps.size());
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

        moreEps.setOnClickListener(view -> epsMenu());
        thisEps.setText(episode);
        thisEps.setOnClickListener(view -> {
            if (!eps.isEmpty()) {
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
            epsContainer.setVisibility(View.GONE);
            epsShow = false;
            return;
        }

        webView.pauseTimers();
        moreEps.setColorFilter(getResources().getColor(R.color.info));
        epsContainer.setVisibility(View.VISIBLE);
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