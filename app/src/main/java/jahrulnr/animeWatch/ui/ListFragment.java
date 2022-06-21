package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.MessageEvent;
import jahrulnr.animeWatch.Class._anime;
import jahrulnr.animeWatch.Class._manga;
import jahrulnr.animeWatch.Class.animeClick;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.listAdapter;
import jahrulnr.animeWatch.configManga;
import jahrulnr.animeWatch.databinding.FragmentListBinding;

public class ListFragment extends Fragment {

    private Activity act;
    private FragmentListBinding binding;
    private JahrulnrLib it;
    private RelativeLayout loading;
    private LayoutAnimationController animationController;
    private GridLayoutAnimationController gridAnimationController;
    private SearchView searchView;
    private LinearLayout listContainer;
    private GridView gridView;
    private animeClick animeClick = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        EventBus.getDefault().post(new MessageEvent(root.getId()));
        act = getActivity();

        ((RelativeLayout) root.findViewById(R.id.episode_preview)).setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.grid_animation);
        animationController = new LayoutAnimationController(animation);
        gridAnimationController = new GridLayoutAnimationController(animation, 0.2f, 0.1f);
        loading = root.findViewById(R.id.loadingContainer);
        listContainer = root.findViewById(R.id.listContainer);
        gridView = root.findViewById(R.id.itemGridView);
        searchView = root.findViewById(R.id.searchAnime);
        listContainer.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        ImageView iconSV = searchView.findViewById(
                searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null));
        TextView textView = searchView.findViewById(
                searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        ImageView iconCSV = searchView.findViewById(
                searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null));
        iconSV.setColorFilter(getResources().getColor(R.color.white));
        iconCSV.setColorFilter(getResources().getColor(R.color.white));

        textView.setHint(R.string.searchHint);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setHintTextColor(getResources().getColor(R.color.gray_600));

        it = new JahrulnrLib(this.getActivity());
        setupAnimeList();
        listContainer.setVisibility(View.VISIBLE);
        Button btnAnime = root.findViewById(R.id.btnAnime);
        Button btnManga = root.findViewById(R.id.btnManga);
        btnAnime.setOnClickListener(v -> setupAnimeList());
        btnManga.setOnClickListener(v -> setupMangaList());

        return root;
    }

    private void setupAnimeList() {
        gridView.setNumColumns(2);
        List<_anime> animelist = new ArrayList<>();
        listAdapter.animeListAdapter adapter = new listAdapter.animeListAdapter(act, animelist);
        String h = new dbFiles(act).readSource(dbFiles.animeListSource);
        it.executer(() -> {
            Matcher m;
            if (!h.isEmpty())
                m = JahrulnrLib.preg_match(h, JahrulnrLib.configAnime.list_pattern);
            else
                m = JahrulnrLib.preg_match(JahrulnrLib.getRequest(JahrulnrLib.configAnime.list, null),
                        JahrulnrLib.configAnime.list_pattern);
            if (m != null) {
                while (m.find()) {
                    _anime al = new _anime();
                    al.nama = m.group(3);
                    al.link = m.group(2);
                    animelist.add(al);
                }
            }
            act.runOnUiThread(() -> {
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                it.animate(loading, false);
                gridView.setLayoutAnimation(gridAnimationController);
            });
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                adapter.filter(newText);
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.filter(query);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            animeClick = new animeClick(act, it, listContainer, adapter.getItems().get(i));
        });
    }

    private void setupMangaList() {
        gridView.setNumColumns(3);
        List<_manga> mangalist = new ArrayList<>();
        listAdapter.mangaListAdapter adapter = new listAdapter.mangaListAdapter(act, mangalist);
        String h = new dbFiles(act).readSource(dbFiles.mangaListSource);
        it.executer(() -> {
            Matcher m;
            if (!h.isEmpty())
                m = JahrulnrLib.preg_match(h, configManga.listPattern);
            else
                m = JahrulnrLib.preg_match(JahrulnrLib.getRequest(configManga.list, null),
                        configManga.listPattern);
            if (m != null) {
                while (m.find()) {
                    _manga al = new _manga();
                    al.manga = m.group(6);
                    al.img = m.group(3);
                    al.link = m.group(1);
                    mangalist.add(al);
                }
            }
            act.runOnUiThread(() -> {
                gridView.setAdapter(adapter);
                it.animate(loading, false);
                gridView.setLayoutAnimation(gridAnimationController);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}