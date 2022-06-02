package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.animeClick;
import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeHomeListAdapter;
import jahrulnr.animeWatch.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Activity act;
    private JahrulnrLib it;
    private boolean animeClicked = false;
    private animeClick animeClick = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RelativeLayout loading = root.findViewById(R.id.loadingContainer);
        loading.setVisibility(View.VISIBLE);
        act = getActivity();
        it = new JahrulnrLib(this.getActivity());
        it.executer(() -> {
            List<animeList> animelist = getAnime(JahrulnrLib.config.updateList);
            animelist.addAll(getAnime(JahrulnrLib.config.updateList + "page=2"));
            animelist.addAll(getAnime(JahrulnrLib.config.updateList + "page=3"));
            animeHomeListAdapter adapter = new animeHomeListAdapter(getContext(), it, animelist);
            act.runOnUiThread(() -> {
                GridView gridView = root.findViewById(R.id.animeHome);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                    animeClicked = true;
                    animeClick = new animeClick(act, it, gridView, animelist.get(i));
                });
                loading.setVisibility(View.GONE);
            });
        });

        return root;
    }

    private List<animeList> getAnime(String link){
        String h = JahrulnrLib.getRequest(link);
        Matcher updateListM = JahrulnrLib.preg_match(h, JahrulnrLib.config.update_pattern);
        List<animeList> animelist = new ArrayList<>();

        if(updateListM != null){
            while (updateListM.find()) {
                animeList al = new animeList();
                al.nama = updateListM.group(6);
                al.img_link = updateListM.group(4);
                al.link = updateListM.group(1);
                al.status = updateListM.group(3);
                animelist.add(al);
            }
        }

        return animelist;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                if(animeClicked && animeClick != null){
                    animeClick.close();
                    animeClicked = false;
                    return true;
                }
            }
            return false;
        });
    }
}