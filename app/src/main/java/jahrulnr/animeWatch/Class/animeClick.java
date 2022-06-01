package jahrulnr.animeWatch.Class;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.adapter.animeEpsListAdapter;
import jahrulnr.animeWatch.ui.nontonView;

public class animeClick {

    public animeClick(Activity act, JahrulnrLib it, ViewGroup viewGroup, animeList animelist) {
        it.executer(() -> {
            String h = JahrulnrLib.getUniversalRequest(animelist.link);
            Matcher coverM = JahrulnrLib.preg_match(h, JahrulnrLib.config.img_pattern),
                    statusM = JahrulnrLib.preg_match(h, JahrulnrLib.config.status_pattern),
                    studioM = JahrulnrLib.preg_match(h, JahrulnrLib.config.studio_pattern),
                    rilisM = JahrulnrLib.preg_match(h, JahrulnrLib.config.rilis_pattern),
                    seasonM = JahrulnrLib.preg_match(h, JahrulnrLib.config.season_pattern),
                    genre = JahrulnrLib.preg_match(h, JahrulnrLib.config.genre_pattern);

            String cover = coverM.find() ? coverM.group(1) : "",
                    status = statusM.find() ? statusM.group(1) : "",
                    studio = studioM.find() ? studioM.group(1) : "",
                    rilis = rilisM.find() ? rilisM.group(1) : "",
                    season = seasonM.find() ? seasonM.group(2) : "";

            String genreText = "";
            while (genre.find()) {
                genreText += genre.group(2) + ", ";
            }
            genreText = genreText.length() > 1 ? genreText.substring(0, genreText.length() - 2) : genreText;
            String genreFinal = genreText;

            Matcher studioMatch = JahrulnrLib.preg_match(studio, "<a href=\"https://oploverz.asia/studio/?(.*?)\" rel=\"tag\">?(.*?)</a>");
            String studioText = "";
            while (studioMatch.find()) {
                studioText += studioMatch.group(2) + ", ";
            }
            studioText = studioText.length() > 1 ? studioText.substring(0, studioText.length() - 2) : studioText;

            // Detail
            String finalStudioText = studioText;
            act.runOnUiThread(() -> {
                RelativeLayout animeDetailView = act.findViewById(R.id.episode_view);
                ImageView iv_cover = act.findViewById(R.id.animeEpsCover);
                TextView tv_title = act.findViewById(R.id.animeTitle);
                TextView tv_status = act.findViewById(R.id.animeStatus);
                TextView tv_genre = act.findViewById(R.id.animeGenre);
                TextView tv_studio = act.findViewById(R.id.animeStudio);
                TextView tv_rilis = act.findViewById(R.id.animeReleased);
                TextView tv_season = act.findViewById(R.id.animeSeason);

                Picasso.get().load(cover).into(iv_cover);
                tv_title.setText(animelist.nama);
                tv_status.setText(": " + status);
                tv_genre.setText(": " + genreFinal);
                tv_studio.setText(": " + finalStudioText);
                tv_rilis.setText(": " + rilis);
                tv_season.setText(": " + season);
                viewGroup.setVisibility(View.GONE);
                animeDetailView.setVisibility(View.VISIBLE);
            });

            Matcher episodes = JahrulnrLib.preg_match(h, JahrulnrLib.config.episode_pattern);
            List<episodeList> episodelist = new ArrayList<>();
            while (episodes.find()) {
                episodeList al = new episodeList();
                al.episode = episodes.group(4)
                        .replace("&#8211;", "-");
                al.link = episodes.group(2);
                episodelist.add(al);
            }
            animeEpsListAdapter adapter = new animeEpsListAdapter(act, episodelist);
            act.runOnUiThread(() -> {
                GridView eps = act.findViewById(R.id.episode_list);
                eps.setAdapter(adapter);
                eps.setOnItemClickListener((adapterView, view, i, l) -> {
                    Intent intent = new Intent(act, nontonView.class);
                    intent.putExtra("nama", animelist.nama);
                    intent.putExtra("img_link", animelist.img_link);
                    intent.putExtra("anime_link", animelist.link);
                    intent.putExtra("episode", episodelist.get(i).episode);
                    intent.putExtra("eps_link", episodelist.get(i).link);
                    act.startActivity(intent);
                });
            });
        });
    }
}
