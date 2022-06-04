package jahrulnr.animeWatch.Class;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeEpsListAdapter;

public class animeClick {

    private Activity act;
    private ViewGroup viewGroup;
    private RelativeLayout animeDetailView;
    private episodePreview episodePreview = null;
    private boolean episodeClicked = false;

    public animeClick(){};

    public animeClick(Activity act, JahrulnrLib it, ViewGroup viewGroup, animeList animelist) {
        this.act = act;
        this.viewGroup = viewGroup;
        animeDetailView = act.findViewById(R.id.episode_view);
        ImageView iv_cover = act.findViewById(R.id.animeClickCover);
        TextView tv_title = act.findViewById(R.id.animeClickTitle);
        TextView tv_genre = act.findViewById(R.id.animeGenre);
        TextView tv_studio = act.findViewById(R.id.animeStudio);
        TextView tv_rilis = act.findViewById(R.id.animeReleased);
        it.executer(() -> {
            String h = JahrulnrLib.getRequest(animelist.link, null);
            Matcher coverM = JahrulnrLib.preg_match(h, JahrulnrLib.config.img_pattern),
                    studioM = JahrulnrLib.preg_match(h, JahrulnrLib.config.studio_pattern),
                    rilisM = JahrulnrLib.preg_match(h, JahrulnrLib.config.rilis_pattern),
                    genre = JahrulnrLib.preg_match(h, JahrulnrLib.config.genre_pattern);

            String cover = coverM.find() ? coverM.group(1) : "",
                    studio = studioM.find() ? studioM.group(1) : "",
                    rilis = rilisM.find() ? rilisM.group(1) : "";

            String genreText = "";
            while (genre.find()) {
                genreText += genre.group(2) + ", ";
            }
            genreText = genreText.length() > 1 ? genreText.substring(0, genreText.length() - 2) : genreText;
            String genreFinal = genreText;

            Matcher getAnimeID = it.preg_match(h.replaceAll("\n", ""),
                    "\\Qname=\"series_id\" value=\"\\E([0-9]+?)\\Q\">\\E");
            if(getAnimeID.find()) {
                String p = "misha_number_of_results=100000" +
                        "&misha_order_by=date-DESC" +
                        "&action=mishafilter" +
                        "&series_id=" + getAnimeID.group(1);
                List<episodeList> episodelist = getEpisode(p);
                animeEpsListAdapter adapter = new animeEpsListAdapter(act, episodelist);

                // Detail
                act.runOnUiThread(() -> {
                    GridView eps = act.findViewById(R.id.episode_list);
                    Picasso.get().load(cover).into(iv_cover);
                    tv_title.setText(animelist.nama);
                    tv_genre.setText(": " + genreFinal);
                    tv_studio.setText(": " + studio);
                    tv_rilis.setText(": " + rilis);
                    eps.setAdapter(adapter);
                    eps.setOnItemClickListener((adapterView, view, i, l) -> {
                        episodeClicked = true;
                        animeList epsPrev = animelist;
                        epsPrev.img_link = cover;
                        epsPrev.link = episodelist.get(i).link;
                        episodePreview = new episodePreview(act, it, animeDetailView, epsPrev, this);
                    });
                    viewGroup.setVisibility(View.GONE);
                    animeDetailView.setVisibility(View.VISIBLE);
                    new onBackPress(animeDetailView, () -> close());
                });
            }
        });
    }

    public List<episodeList> getEpisode(String post){
        HashMap<String, String> p = new HashMap<>();
        p.put("Referer", "https://75.119.159.228/");
        List<episodeList> episodelist = new ArrayList<>();
        String h = JahrulnrLib.getRequest(JahrulnrLib.config.apiLink, post, p);
        JSONObject epsData = null;
        try {
            epsData = new JSONObject(h);
            h = epsData.getString("content");
            Matcher episodeM = JahrulnrLib.preg_match(h, JahrulnrLib.config.episode_pattern);
            while (episodeM.find()) {
                episodeList al = new episodeList();
                al.episode = episodeM.group(2).replaceAll("(.*?)\\QEpisode \\E([0-9]+)(\\sSub\\sIndo)", "Episode $2");
                al.upload = "Diupload " + episodeM.group(3);
                al.link = episodeM.group(1);
                episodelist.add(al);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return episodelist;
    }

    public boolean isEpisodeClicked(){
        return episodeClicked;
    }

    public void closeEpisodeView(){
        if(episodeClicked){
            episodePreview.close();
            episodeClicked = false;
        }
    }

    public void close(){
        episodeClicked = false;
        if(episodePreview != null)
            episodePreview.close();
        animeDetailView.setVisibility(View.GONE);
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.startLayoutAnimation();
    }
}
