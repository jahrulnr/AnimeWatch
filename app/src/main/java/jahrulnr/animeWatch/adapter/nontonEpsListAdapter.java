package jahrulnr.animeWatch.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import jahrulnr.animeWatch.Class.animeList;
import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.R;

public class nontonEpsListAdapter extends BaseAdapter {

    private final Activity activity;
    private final animeList animelist;
    private final List<episodeList> epsList;
    private final LayoutInflater layoutInflater;

    public nontonEpsListAdapter(Activity activity, animeList animelist, List<episodeList> epsList) {
        this.activity = activity;
        this.animelist = animelist;
        this.epsList = epsList;
        this.layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return epsList.size();
    }

    @Override
    public Object getItem(int i) {
        return epsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = layoutInflater.inflate(R.layout.nontoneps_list, null);

        TextView episode = view.findViewById(R.id.episode);
        episode.setText(epsList.get(i).episode);

        return view;
    }
}
