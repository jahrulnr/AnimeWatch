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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.animeClick;
import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeAllListAdapter;
import jahrulnr.animeWatch.databinding.FragmentListBinding;

public class ListFragment extends Fragment{

    private Activity act;
    private FragmentListBinding binding;
    private JahrulnrLib it;
    private ViewGroup container;
    private boolean animeClicked = false;
    private animeClick animeClick = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        act = getActivity();

        LinearLayout linearLayout = root.findViewById(R.id.animeListContainer);
        GridView gridView = root.findViewById(R.id.animeList);
        SearchView searchView = root.findViewById(R.id.searchAnime);
        int iconId = searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        int iconCloseId = searchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        int textViewId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(textViewId);
        ImageView iconSV = searchView.findViewById(iconId);
        ImageView iconCSV = searchView.findViewById(iconCloseId);
        iconSV.setColorFilter(getResources().getColor(R.color.white));
        iconCSV.setColorFilter(getResources().getColor(R.color.white));
        textView.setHint(R.string.searchHint);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setHintTextColor(getResources().getColor(R.color.gray_600));
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
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String newText) {
                        adapter.filter(newText);
                        gridView.setAdapter(adapter);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        adapter.filter(query);
                        gridView.setAdapter(adapter);
                        return false;
                    }
                });
                gridView.setOnItemClickListener((adapterView, view, i, l) -> {
                    linearLayout.setVisibility(View.GONE);
                    animeClicked = true;
                    animeClick = new animeClick(act, it, ((GridView) act.findViewById(R.id.animeList)), animelist.get(i));
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