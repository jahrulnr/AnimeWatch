package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import jahrulnr.animeWatch.Class.IOnBackPressed;
import jahrulnr.animeWatch.adapter.animeHistory;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment implements IOnBackPressed {

    private FragmentHistoryBinding binding;
    private GridView gridView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Activity act = getActivity();
        JahrulnrLib it = new JahrulnrLib(act);
        gridView = root.findViewById(R.id.historyWatch);
        TextView textView = root.findViewById(R.id.emptyList);

        dbFiles dbFiles = new dbFiles(act);
        List<episodeList> epsList = dbFiles.getList();
        if(!epsList.isEmpty()) {
            animeHistory adapter = new animeHistory(getActivity(), epsList);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                episodeList eps = epsList.get(i);
                Intent intent = new Intent(act, nontonView.class);
                intent.putExtra("link", eps.link);
                act.startActivity(intent);
            });
            textView.setVisibility(View.GONE);
        }
        else {
            gridView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }

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