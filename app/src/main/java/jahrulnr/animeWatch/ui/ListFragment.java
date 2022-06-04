package jahrulnr.animeWatch.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.Class.animeClick;
import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.databinding.FragmentListBinding;

public class ListFragment extends Fragment{

    private Activity act;
    private FragmentListBinding binding;
    private JahrulnrLib it;
    private ViewGroup container;
    private GridView gridView;
    private boolean animeClicked = false;
    private animeClick animeClick = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        act = getActivity();

        ((RelativeLayout) root.findViewById(R.id.episode_preview)).setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.grid_animation);
        RelativeLayout loading = root.findViewById(R.id.loadingContainer);
        LinearLayout linearLayout = root.findViewById(R.id.animeListContainer);
        gridView = root.findViewById(R.id.animeList);
        SearchView searchView = root.findViewById(R.id.searchAnime);
        loading.setVisibility(View.VISIBLE);

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
            String h = JahrulnrLib.getRequest(JahrulnrLib.config.list, null);
            Matcher m = JahrulnrLib.preg_match(h, JahrulnrLib.config.list_pattern);
            List<animeList> animelist = new ArrayList<>();
            if(m != null){
                while (m.find()) {
                    animeList al = new animeList();
                    al.nama = m.group(3);
                    al.link = m.group(2);
                    animelist.add(al);
                }
            }
            animeAllListAdapter adapter = new animeAllListAdapter(getContext(), animelist);
            act.runOnUiThread(() -> {
                GridLayoutAnimationController animationController = new GridLayoutAnimationController(animation, .0f, .025f);
                gridView.setLayoutAnimation(animationController);
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
                    animeClicked = true;
                    animeClick = new animeClick(act, it, linearLayout, adapter.getItems().get(i));
                });
                loading.setVisibility(View.GONE);
            });
        });
        return root;
    }

    class animeAllListAdapter extends BaseAdapter {

        Context context;
        List<animeList> animelist, animelists_original;
        LayoutInflater inflter;

        public animeAllListAdapter(Context applicationContext, List<animeList> animelist_in) {
            this.context = applicationContext;
            this.animelists_original = animelist_in;
            this.animelist = animelists_original;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return animelist.size();
        }

        @Override
        public animeList getItem(int i) {
            return animelist.get(i);
        }

        public List<animeList> getItems(){
            return animelist;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.animealllist_view, null);
            }
            animeList item = animelist.get(i);
            TextView namaAnime = view.findViewById(R.id.animeName);

            namaAnime.setText(item.nama.replace("Sub Indonesia", ""));
            return view;
        }

        public void filter(String text){
            String query = text.toLowerCase();
            animelist = new ArrayList<>();
            if(text.length() == 0){
                animelist = animelists_original;
            } else {
                for(animeList m : animelists_original){
                    if(m.nama.toLowerCase().contains(query)){
                        animelist.add(m);
                    }
                }
            }
        }
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
                if(animeClicked && animeClick.isEpisodeClicked()){
                    animeClick.closeEpisodeView();
                    return true;
                }
                else if(animeClicked && animeClick != null){
                    animeClick.close();
                    gridView.startLayoutAnimation();
                    animeClicked = false;
                    return true;
                }
            }
            return false;
        });
    }
}