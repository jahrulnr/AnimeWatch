package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import jahrulnr.animeWatch.Class.MessageEvent;
import jahrulnr.animeWatch.Class._anime;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.Class.episodePreview;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeHistoryAdapter;
import jahrulnr.animeWatch.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {

    private Activity act;
    private JahrulnrLib it;
    dbFiles dbFiles;
    private FragmentHistoryBinding binding;
    boolean animeClicked = false;
    animeHistoryAdapter adapter;
    List<_anime.animeEpisode> epsList = new ArrayList<>();
    View root;
    RelativeLayout loading;
    GridView gridView;
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.grid_animation);
        LayoutAnimationController animationController = new LayoutAnimationController(animation);
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        EventBus.getDefault().post(new MessageEvent(root.getId()));

        act = getActivity();
        it = new JahrulnrLib(act);
        root.findViewById(R.id.episode_preview).setVisibility(View.GONE);
        loading = root.findViewById(R.id.loadingContainer);
        container = root.findViewById(R.id.historyContainer);
        gridView = root.findViewById(R.id.historyWatch);
        textView = root.findViewById(R.id.emptyList);
        loading.setVisibility(View.VISIBLE);
        container.setLayoutAnimation(animationController);

        dbFiles = new dbFiles(act);
        epsList.clear();
        epsList.addAll(dbFiles.getList());
        if (!epsList.isEmpty()) {
            ViewGroup finalContainer = container;
            adapter = new animeHistoryAdapter(getActivity(), epsList);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                _anime.animeEpisode eps = epsList.get(i);
                animeClicked = true;
                new episodePreview(act, it, finalContainer, eps);
            });
            textView.setVisibility(View.GONE);
        } else {
            gridView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
        it.animate(loading, false);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(epsList.isEmpty()){
            epsList.addAll(dbFiles.getList());
            if(adapter != null)
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        epsList.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}