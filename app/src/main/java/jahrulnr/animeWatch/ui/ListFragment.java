package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.IOnBackPressed;
import jahrulnr.animeWatch.Class.animeClick;
import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeAllListAdapter;
import jahrulnr.animeWatch.databinding.FragmentListBinding;

public class ListFragment extends Fragment implements IOnBackPressed {

    private Activity act;
    private FragmentListBinding binding;
    private JahrulnrLib it;
    private ViewGroup container;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        act = getActivity();

        GridView gridView = root.findViewById(R.id.animeList);
        it = new JahrulnrLib(this.getActivity());
        it.executer(() -> {
            String h = JahrulnrLib.getRequest(JahrulnrLib.config.list);
            Matcher m = JahrulnrLib.preg_match(h, JahrulnrLib.config.list_pattern);
            List<animeList> animelist = new ArrayList<>();
            while (m.find()) {
                animeList al = new animeList();
                al.nama = m.group(3);
                al.link = m.group(2);
                animelist.add(al);
            }
            animeAllListAdapter adapter = new animeAllListAdapter(getContext(), animelist);
            act.runOnUiThread(() -> {
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                    gridView.setVisibility(View.GONE);
                    new animeClick(act, it, ((GridView) act.findViewById(R.id.animeList)), animelist.get(i));
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
    public boolean onBackPressed() {
        return false;
    }
}