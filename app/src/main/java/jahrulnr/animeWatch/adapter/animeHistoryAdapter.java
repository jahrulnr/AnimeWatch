package jahrulnr.animeWatch.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

import jahrulnr.animeWatch.Class._anime;
import jahrulnr.animeWatch.R;

public class animeHistoryAdapter extends BaseAdapter {

    private final List<_anime.animeEpisode> epsList;
    private final LayoutInflater layoutInflater;

    public animeHistoryAdapter(Activity activity, List<_anime.animeEpisode> epsList) {
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = layoutInflater.inflate(R.layout.history_list, null);

        ImageView imageView = view.findViewById(R.id.animeCover);
        TextView anime = view.findViewById(R.id.anime);
        TextView episode = view.findViewById(R.id.episode);
        if (epsList.get(i).getImg_link() != null)
            Picasso.get().load(StringEscapeUtils.unescapeJava(epsList.get(i).getImg_link())).into(imageView);
        anime.setText(StringEscapeUtils.unescapeJava(epsList.get(i).getNama()));
        episode.setText(epsList.get(i).episode);

        return view;
    }
}
