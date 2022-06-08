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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.Class.episodePreview;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeHomeListAdapter;
import jahrulnr.animeWatch.config;
import jahrulnr.animeWatch.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Activity act;
    private JahrulnrLib it;
    private TextView loadMore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.grid_animation);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SwipeRefreshLayout refreshLayout = root.findViewById(R.id.pullRefresh);
        root.findViewById(R.id.episode_preview).setVisibility(View.GONE);
        RelativeLayout loading = root.findViewById(R.id.loadingContainer);
        GridViewWithHeaderAndFooter gridView = root.findViewById(R.id.animeHome);
        @SuppressLint("InflateParams") View footerView = LayoutInflater.from(getContext()).inflate(R.layout.more_text, null, false);
        ProgressBar gridLoad = footerView.findViewById(R.id.loadData);
        gridLoad.setVisibility(View.GONE);
        loadMore = footerView.findViewById(R.id.loadMore);
        loading.setVisibility(View.VISIBLE);

        GridLayoutAnimationController gridLayoutAnimationController = new GridLayoutAnimationController(animation, .1f, .2f);
        gridView.setLayoutAnimation(gridLayoutAnimationController);
        gridView.addFooterView(footerView);

        act = getActivity();
        it = new JahrulnrLib(this.getActivity());
        List<episodeList> episodelist = new ArrayList<>();
        animeHomeListAdapter adapter = new animeHomeListAdapter(act, episodelist);
        String post = "action=loadmore&type=home&page=";
        String source = new dbFiles(act).readSource(dbFiles.updateSource);
        it.executer(() -> {
            if (!source.isEmpty()) {
                episodelist.clear();
                episodelist.addAll(getAnime(source));
            } else {
                episodelist.clear();
                episodelist.addAll(getAnime(config.apiLink, post + 0));
            }

            act.runOnUiThread(() -> {
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                it.animate(loading, false);
            });
        });

        gridView.setOnItemClickListener((adapterView, view, i, l) ->
                new episodePreview(act, it, refreshLayout, episodelist.get(i)));

        AtomicInteger page = new AtomicInteger(1);
        loadMore.setOnClickListener(view -> {
            gridLoad.setVisibility(View.VISIBLE);
            loadMore.setVisibility(View.GONE);
            it.executer(() -> {
                List<episodeList> addAnime = getAnime(config.apiLink, post + page.getAndIncrement());
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
            episodelist.addAll(getAnime(config.apiLink, post + 0));
            gridView.startLayoutAnimation();
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        });

        return root;
    }

    private List<episodeList> getAnime(String source) {
        List<episodeList> episodeLists = new ArrayList<>();
        Matcher updateListM = JahrulnrLib.preg_match(source.replaceAll("\\Q.jpg?h=\\E([0-9]*)", ".jpg?h=1024").replaceAll("  +", " ")
                        .replaceAll("\\Q.jpg?h=\\E([0-9]*)", ".jpg?h=1024").replaceAll("  +", " "),
                config.update_pattern);
        if (updateListM != null) {
            while (updateListM.find()) {
                episodeList al = new episodeList();
                al.episode = updateListM.group(8);
                al.animeList.img_link = updateListM.group(6);
                al.link = updateListM.group(2);
                al.animeList.status = updateListM.group(4);
                episodeLists.add(al);
            }
        }

        return episodeLists;
    }

    private List<episodeList> getAnime(String link, String post) {
        HashMap<String, String> p = new HashMap<>();
        p.put("Referer", "https://75.119.159.228/");
        return getAnime(JahrulnrLib.getRequest(link, post, p));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}