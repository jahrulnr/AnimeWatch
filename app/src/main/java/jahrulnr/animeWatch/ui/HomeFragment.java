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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.animeClick;
import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeHomeList;
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
        act = getActivity();
        it = new JahrulnrLib(this.getActivity());
        it.executer(() -> {
            String h = JahrulnrLib.getRequest(JahrulnrLib.config.updateList);
            Matcher updateListM = JahrulnrLib.preg_match(h, JahrulnrLib.config.update_pattern);

            List<animeList> animelist = new ArrayList<>();
            while (updateListM.find()) {
                animeList al = new animeList();
                al.nama = updateListM.group(6);
                al.img_link = updateListM.group(4);
                al.link = updateListM.group(1);
                al.status = updateListM.group(3);
                animelist.add(al);
            }

            animeHomeList adapter = new animeHomeList(getContext(), it, animelist);
            act.runOnUiThread(() -> {
                GridView gridView = root.findViewById(R.id.animeHome);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                    animeClicked = true;
                    animeClick = new animeClick(act, it, gridView, animelist.get(i));
                });
            });
        });

        return root;
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