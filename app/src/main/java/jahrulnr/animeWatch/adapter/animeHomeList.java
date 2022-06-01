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

import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;

public class animeHomeList extends BaseAdapter {

    private final Context context;
    private final List<animeList> animehomelist;
    private final JahrulnrLib it;
    LayoutInflater inflter;

    public animeHomeList(Context context, JahrulnrLib it, List<animeList> animehomelist) {
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

        animeList item = animehomelist.get(i);
        TextView anime = view.findViewById(R.id.animeName);
        ImageView cover = view.findViewById(R.id.animeCover);

        if (item.nama != null) {
            anime.setText(item.nama);
            Picasso.get().load(item.img_link).into(cover);
        }

        return view;
    }
}
