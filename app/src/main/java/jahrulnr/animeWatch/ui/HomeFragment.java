package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.Class.episodePreview;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeHomeListAdapter;
import jahrulnr.animeWatch.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Activity act;
    private JahrulnrLib it;
    private boolean animeClicked = false;
    private episodePreview episodePreview = null;
    private TextView loadMore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.grid_animation);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ((RelativeLayout) root.findViewById(R.id.episode_preview)).setVisibility(View.GONE);
        RelativeLayout loading = root.findViewById(R.id.loadingContainer);
        GridViewWithHeaderAndFooter gridView = root.findViewById(R.id.animeHome);
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.more_text, null, false);
        ProgressBar gridLoad = footerView.findViewById(R.id.loadData);
        gridLoad.setVisibility(View.GONE);
        loadMore = footerView.findViewById(R.id.loadMore);
        loading.setVisibility(View.VISIBLE);

        act = getActivity();
        it = new JahrulnrLib(this.getActivity());
        AtomicInteger page = new AtomicInteger();
        String post = "action=loadmore&type=home&page=";
                it.executer(() -> {
            List<episodeList> episodelist = getAnime(JahrulnrLib.config.apiLink, post+page.getAndIncrement());

            animeHomeListAdapter adapter = new animeHomeListAdapter(act, it, episodelist);
            act.runOnUiThread(() -> {
                GridLayoutAnimationController gridLayoutAnimationController = new GridLayoutAnimationController(animation, .1f, .2f);
                gridView.setLayoutAnimation(gridLayoutAnimationController);
                gridView.addFooterView(footerView);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                    animeClicked = true;
                    episodePreview = new episodePreview(act, it, gridView, episodelist.get(i), null);
                });

                loadMore.setOnClickListener(view -> {
                    gridLoad.setVisibility(View.VISIBLE);
                    loadMore.setVisibility(View.GONE);
                    it.executer(() -> {
                        List<episodeList> addAnime = getAnime(JahrulnrLib.config.apiLink, post+page.getAndIncrement());
                        act.runOnUiThread(() -> {
                            episodelist.addAll(addAnime);
                            adapter.notifyDataSetChanged();
                            gridLoad.setVisibility(View.GONE);
                            loadMore.setVisibility(View.VISIBLE);
                        });
                    });
                });
                loading.setVisibility(View.GONE);
            });
        });

        return root;
    }

    private List<episodeList> getAnime(String link, String post){
        HashMap<String, String> p = new HashMap<>();
        p.put("Referer", "https://75.119.159.228/");
        List<episodeList> episodeLists = new ArrayList<>();
        String h = it.getRequest(link, post, p);
        h = h.replaceAll("\\Q.jpg?h=\\E([0-9]*)", ".jpg?h=1024").replaceAll("  +", " ");
        Matcher updateListM = JahrulnrLib.preg_match(h, JahrulnrLib.config.update_pattern);

        if(updateListM != null) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                if(animeClicked && episodePreview != null){
                    episodePreview.close();
                    animeClicked = false;
                    return true;
                }
            }
            return false;
        });
    }
}