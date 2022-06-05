package jahrulnr.animeWatch.Class;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.GridView;
import android.widget.ImageView;
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
    private RelativeLayout loading;
    private RelativeLayout animeDetailView;
    private episodePreview episodePreview = null;
    private boolean episodeClicked = false;

    public animeClick(){};

    public animeClick(Activity act, JahrulnrLib it, ViewGroup viewGroup, animeList animelist) {
        this.act = act;
        this.viewGroup = viewGroup;
        loading = act.findViewById(R.id.loadingContainer);
        animeDetailView = act.findViewById(R.id.episode_view);
        ImageView iv_cover = act.findViewById(R.id.animeClickCover);
        TextView tv_title = act.findViewById(R.id.animeClickTitle);
        TextView tv_genre = act.findViewById(R.id.animeGenre);
        TextView tv_studio = act.findViewById(R.id.animeStudio);
        TextView tv_rilis = act.findViewById(R.id.animeReleased);
        loading.setVisibility(View.VISIBLE);

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

            boolean pattern = false;
            List<episodeList> episodelist;
            Matcher getAnimeID = it.preg_match(h.replaceAll("\n", ""),
                    "\\Qname=\"series_id\" value=\"\\E([0-9]+?)\\Q\">\\E");
            if(getAnimeID.find()) {
                String p = "misha_number_of_results=100000" +
                        "&misha_order_by=date-DESC" +
                        "&action=mishafilter" +
                        "&series_id=" + getAnimeID.group(1);
                episodelist = getEpisode(p, JahrulnrLib.config.episode_pattern1, true);
                if(episodelist != null) pattern = true;
            }else {
                episodelist = getEpisode(h, JahrulnrLib.config.episode_pattern2, false);
                if(episodelist != null) pattern = true;
            }

            if(pattern){
                // Detail
                animeEpsListAdapter adapter = new animeEpsListAdapter(act, episodelist);
                act.runOnUiThread(() -> {
                    Animation animation = AnimationUtils.loadAnimation(act,R.anim.grid_animation);
                    LayoutAnimationController controller = new LayoutAnimationController(animation);
                    GridView eps = act.findViewById(R.id.episode_list);
                    Picasso.get().load(cover).into(iv_cover);
                    tv_title.setText(animelist.nama);
                    tv_genre.setText(": " + genreFinal);
                    tv_studio.setText(": " + studio);
                    tv_rilis.setText(": " + rilis);
                    eps.setAdapter(adapter);
                    eps.setOnItemClickListener((adapterView, view, i, l) -> {
                        episodeClicked = true;
                        episodeList epsPrev = new episodeList();
                        epsPrev.animeList = animelist;
                        epsPrev.animeList.img_link = cover;
                        epsPrev.link = episodelist.get(i).link;
                        episodePreview = new episodePreview(act, it, animeDetailView, epsPrev, this);
                    });
                    viewGroup.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    animeDetailView.setLayoutAnimation(controller);
                    animeDetailView.setVisibility(View.VISIBLE);
                    new onBackPress(animeDetailView, () -> close());
                });
            }
        });
    }

    public List<episodeList> getEpisode(@Nullable String post, String pattern, boolean useLink){
        HashMap<String, String> p = new HashMap<>();
        p.put("Referer", "https://75.119.159.228/");
        List<episodeList> episodelist = new ArrayList<>();

        String h;
        if(useLink == true) {
            h = JahrulnrLib.getRequest(JahrulnrLib.config.apiLink, post, p);
            JSONObject epsData = null;
            try {
                epsData = new JSONObject(h);
                h = epsData.getString("content");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            h = post;
        }
        Matcher episodeM = JahrulnrLib.preg_match(h, pattern);
        while (episodeM.find()) {
            episodeList al = new episodeList();
            al.episode = episodeM.group(2)
                    .replaceAll("(.*?) Episode( +)Sub Indo", "Episode ?")
                    .replaceAll("(.*?)\\QEpisode \\E([0-9]+)(\\sSub\\sIndo)?", "Episode $2");
            al.upload = "Diupload " + episodeM.group(3);
            al.link = episodeM.group(1);
            episodelist.add(al);
        }
        return episodelist;
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
            closeEpisodeView();
        animeDetailView.setVisibility(View.GONE);
        viewGroup.startLayoutAnimation();
        viewGroup.setVisibility(View.VISIBLE);
    }
}
