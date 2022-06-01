package jahrulnr.animeWatch.adapter;

import android.app.Activity;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import jahrulnr.animeWatch.Class.episodeList;
import jahrulnr.animeWatch.R;

public class animeHistory extends BaseAdapter {

    private Activity activity;
    private List<episodeList> epsList;
    private LayoutInflater layoutInflater;

    public animeHistory(Activity activity, List<episodeList> epsList) {
        this.activity = activity;
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
        if(view == null)
            view = layoutInflater.inflate(R.layout.history_list, null);

        TextView eps = view.findViewById(R.id.episode_list);
        eps.setText(epsList.get(i).episode);
        Log.e("historyList", epsList.get(i).toString());

        return view;
    }
}
