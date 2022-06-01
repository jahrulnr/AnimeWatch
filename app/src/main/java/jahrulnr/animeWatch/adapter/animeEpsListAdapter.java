package jahrulnr.animeWatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.R;

public class animeEpsListAdapter extends BaseAdapter {

    Context context;
    List<episodeList> episodelist;
    LayoutInflater inflter;

    public animeEpsListAdapter(Context context, List<episodeList> episodelist) {
        this.context = context;
        this.episodelist = episodelist;
        inflter = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return episodelist.size();
    }

    @Override
    public Object getItem(int i) {
        return episodelist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflter.inflate(R.layout.episode_list, null);
        }
        episodeList item = episodelist.get(i);
        TextView episode = view.findViewById(R.id.episode);
        if (item.episode != null)
            episode.setText(item.episode.replace("Subtitle Indonesia", ""));

        return view;
    }
}
