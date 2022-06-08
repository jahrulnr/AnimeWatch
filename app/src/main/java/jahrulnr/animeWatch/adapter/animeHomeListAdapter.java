package jahrulnr.animeWatch.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.R;

public class animeHomeListAdapter extends BaseAdapter {

    private final List<episodeList> animehomelist;
    LayoutInflater inflter;

    public animeHomeListAdapter(Context context, List<episodeList> episodelist) {
        this.animehomelist = episodelist;
        inflter = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return animehomelist.size();
    }

    @Override
    public Object getItem(int i) {
        return animehomelist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflter.inflate(R.layout.animelist_view, null);
        }

        episodeList item = animehomelist.get(i);
        TextView anime = view.findViewById(R.id.animeName);
        ImageView cover = view.findViewById(R.id.animeCover);
        TextView status = view.findViewById(R.id.status);

        try {
            if (item.episode != null) {
                anime.setText(item.episode);
                Picasso.get().load(item.animeList.img_link).into(cover);
                status.setText(item.animeList.status);
            }
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
        }

        return view;
    }
}
