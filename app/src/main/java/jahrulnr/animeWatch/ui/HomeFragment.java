package jahrulnr.animeWatch.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.apache.commons.lang.StringEscapeUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import jahrulnr.animeWatch.Class.MessageEvent;
import jahrulnr.animeWatch.Class._anime;
import jahrulnr.animeWatch.Class._manga;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.Class.episodePreview;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.homeListAdapter;
import jahrulnr.animeWatch.configAnime;
import jahrulnr.animeWatch.configManga;
import jahrulnr.animeWatch.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private View root;
    private Activity act;
    private JahrulnrLib it;
    private Animation animation;
    private SwipeRefreshLayout refreshLayout;
    private GridViewWithHeaderAndFooter homeGridview;
    private RelativeLayout loading;
    private View footerView;
    private TextView loadMore;
    private ProgressBar gridLoad;

    @SuppressLint("InflateParams")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        act = getActivity();
        it = new JahrulnrLib(act);
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.grid_animation);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        EventBus.getDefault().post(new MessageEvent(root.getId()));

        refreshLayout = root.findViewById(R.id.pullRefresh);
        homeGridview = root.findViewById(R.id.homeGridview);
        root.findViewById(R.id.episode_preview).setVisibility(View.GONE);
        loading = root.findViewById(R.id.loadingContainer);
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.more_text, null, false);
        gridLoad = footerView.findViewById(R.id.loadData);
        gridLoad.setVisibility(View.GONE);
        loadMore = footerView.findViewById(R.id.loadMore);
        loading.setVisibility(View.VISIBLE);

        Button btnAnime = root.findViewById(R.id.btnAnime);
        Button btnManga = root.findViewById(R.id.btnManga);

        animeSetup();
        btnAnime.setOnClickListener(v -> {
            animeSetup();
            it.animate(homeGridview, false);
        });
        btnManga.setOnClickListener(v -> {
            mangaSetup();
            it.animate(homeGridview, false);
        });

        return root;
    }

    private void animeSetup(){
        GridLayoutAnimationController gridLayoutAnimationController = new GridLayoutAnimationController(animation, .1f, .2f);
        homeGridview.setLayoutAnimation(gridLayoutAnimationController);
        if(homeGridview.getFooterViewCount() < 1)
            homeGridview.addFooterView(footerView);

        List<_anime.animeEpisode> episodelist = new ArrayList<>();
        homeListAdapter.anime adapter = new homeListAdapter.anime(act, episodelist);
        String post = "action=loadmore&type=home&page=";
        assert act != null;
        String source = new dbFiles(act).readSource(dbFiles.animeUpdateSource);
        it.executer(() -> {
            if (!source.isEmpty()) {
                episodelist.clear();
                episodelist.addAll(getAnime(source));
            } else {
                episodelist.clear();
                episodelist.addAll(getAnime(configAnime.apiLink, post + 0));
            }

            act.runOnUiThread(() -> {
                homeGridview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                it.animate(loading, false);
                it.animate(homeGridview, true);
            });
        });

        homeGridview.setOnItemClickListener((adapterView, view, i, l) ->
                new episodePreview(act, it, refreshLayout, episodelist.get(i)));

        AtomicInteger page = new AtomicInteger(1);
        loadMore.setOnClickListener(view -> {
            gridLoad.setVisibility(View.VISIBLE);
            loadMore.setVisibility(View.GONE);
            it.executer(() -> {
                List<_anime.animeEpisode> addAnime = getAnime(configAnime.apiLink, post + page.getAndIncrement());
                act.runOnUiThread(() -> {
                    episodelist.addAll(addAnime);
                    adapter.notifyDataSetChanged();
                    gridLoad.setVisibility(View.GONE);
                    loadMore.setVisibility(View.VISIBLE);
                });
            });
        });

        refreshLayout.setOnRefreshListener(() -> {
            episodelist.clear();
            episodelist.addAll(getAnime(configAnime.apiLink, post + 0));
            homeGridview.startLayoutAnimation();
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        });
    }

    private void mangaSetup(){
        List<_manga> mangaList = new ArrayList<>();
        it.executer(() -> {
            assert act != null;
            String source = new dbFiles(act).readSource(dbFiles.mangaUpdateSource);
//            mangaList.addAll(getManga(it.getRequest(configManga.update, null)));
            mangaList.addAll(getManga(source));
            act.runOnUiThread(() -> {
                homeListAdapter.manga mangaAdapter = new homeListAdapter.manga(act, mangaList);
                homeGridview.setAdapter(mangaAdapter);
                it.animate(homeGridview, true);
            });
        });
    }

    private List<_anime.animeEpisode> getAnime(String source) {
        List<_anime.animeEpisode> episodeList = new ArrayList<>();
        Matcher updateListM = JahrulnrLib.preg_match(source.replaceAll("\\Q.jpg?h=\\E([0-9]*)", ".jpg?h=1024").replaceAll("  +", " ")
                        .replaceAll("\\Q.jpg?h=\\E([0-9]*)", ".jpg?h=1024").replaceAll("  +", " "),
                configAnime.updatePattern);
        if (updateListM != null) {
            while (updateListM.find()) {
                _anime.animeEpisode al = new _anime.animeEpisode();
                al.episode = StringEscapeUtils.unescapeJava(updateListM.group(8));
                al.anime.img_link = StringEscapeUtils.unescapeJava(updateListM.group(6));
                al.link = updateListM.group(2);
                al.anime.status = updateListM.group(4);
                episodeList.add(al);
            }
        }

        return episodeList;
    }

    private List<_anime.animeEpisode> getAnime(String link, String post) {
        HashMap<String, String> p = new HashMap<>();
        p.put("Referer", "https://75.119.159.228/");
        return getAnime(JahrulnrLib.getRequest(link, post, p));
    }

    private List<_manga> getManga(String source){
        List<_manga> mangaList = new ArrayList<>();
        Matcher updateListM = JahrulnrLib.preg_match(source, configManga.updatePattern);
        if (updateListM != null)
            while (updateListM.find()) {
                _manga mc = new _manga();
                mc.manga = StringEscapeUtils.unescapeJava(updateListM.group(6));
                mc.img = StringEscapeUtils.unescapeJava(updateListM.group(3));
                mc.link = updateListM.group(1);
                mc.deskripsi = updateListM.group(8);
                mc.chapter.chapter = Integer.parseInt((updateListM.group(13) != null ? updateListM.group(13) : "0"));
                mc.chapter.link = updateListM.group(11);
                mangaList.add(mc);
            }
        return mangaList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}