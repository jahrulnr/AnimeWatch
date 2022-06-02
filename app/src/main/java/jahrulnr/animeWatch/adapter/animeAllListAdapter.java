package jahrulnr.animeWatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.R;

public class animeAllListAdapter extends BaseAdapter {

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
