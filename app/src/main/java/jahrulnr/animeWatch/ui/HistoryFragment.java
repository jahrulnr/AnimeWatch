package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.Class.episodePreview;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeHistoryAdapter;
import jahrulnr.animeWatch.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {

    private Activity act;
    private JahrulnrLib it;
    private GridView gridView;
    private ViewGroup container;
    private FragmentHistoryBinding binding;
    private episodePreview episodePreview = null;
    boolean animeClicked = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.grid_animation);
        LayoutAnimationController animationController = new LayoutAnimationController(animation);
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        act = getActivity();
        it = new JahrulnrLib(act);
        ((RelativeLayout) root.findViewById(R.id.episode_preview)).setVisibility(View.GONE);
        RelativeLayout loading = root.findViewById(R.id.loadingContainer);
        container = root.findViewById(R.id.historyContainer);
        gridView = root.findViewById(R.id.historyWatch);
        TextView textView = root.findViewById(R.id.emptyList);
        loading.setVisibility(View.VISIBLE);
        container.setLayoutAnimation(animationController);

        dbFiles dbFiles = new dbFiles(act);
        List<episodeList> epsList = dbFiles.getList();
        if(!epsList.isEmpty()) {
            ViewGroup finalContainer = container;
            animeHistoryAdapter adapter = new animeHistoryAdapter(getActivity(), epsList);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                episodeList eps = epsList.get(i);
                animeClicked = true;
                episodePreview = new episodePreview(act, it, finalContainer, eps, null);
            });
            textView.setVisibility(View.GONE);
        }
        else {
            gridView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
        loading.setVisibility(View.GONE);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}