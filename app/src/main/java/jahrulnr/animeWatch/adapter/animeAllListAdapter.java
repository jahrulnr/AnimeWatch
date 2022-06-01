package jahrulnr.animeWatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.R;

public class animeAllListAdapter extends BaseAdapter {

    Context context;
    List<animeList> animelist;
    LayoutInflater inflter;

    public animeAllListAdapter(Context applicationContext, List<animeList> animelist) {
        this.context = applicationContext;
        this.animelist = animelist;
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

        namaAnime.setText(item.nama);
        return view;
    }
}
