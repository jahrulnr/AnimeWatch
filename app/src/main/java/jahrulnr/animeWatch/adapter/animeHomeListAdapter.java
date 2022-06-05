package jahrulnr.animeWatch.adapter;

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
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;

public class animeHomeListAdapter extends BaseAdapter {

    private final Context context;
    private final List<episodeList> animehomelist;
    private final JahrulnrLib it;
    LayoutInflater inflter;

    public animeHomeListAdapter(Context context, JahrulnrLib it, List<episodeList> animehomelist) {
        this.context = context;
        this.it = it;
        this.animehomelist = animehomelist;
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
        } catch (java.lang.NullPointerException e){
            e.printStackTrace();
        }

        return view;
    }
}
