package jahrulnr.animeWatch.Class;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.ui.nontonView;

public class episodePreview {

    private final RelativeLayout relativeLayout;
    private final RelativeLayout episode_preview;

    @SuppressLint("SetTextI18n")
    public episodePreview(Activity act, JahrulnrLib it, ViewGroup viewGroup, _anime.animeEpisode episodelist) {
        Animation animation = AnimationUtils.loadAnimation(act, R.anim.grid_animation);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        _anime animelist = episodelist.anime;
        relativeLayout = act.findViewById(R.id.loadingContainer);
        episode_preview = act.findViewById(R.id.episode_preview);
        episode_preview.setVisibility(View.GONE);
        ImageView iv_cover = act.findViewById(R.id.animeEpsCover);
        TextView tv_title = act.findViewById(R.id.animeTitle),
                tv_episode = act.findViewById(R.id.animeEpisode);
        episode_preview.setLayoutAnimation(controller);
        it.animate(relativeLayout, true);

        it.executer(() -> {
            Matcher coverM, namaM, animeLinkM, episodeM;
            String cover, nama, animeLink, episode;
            String h = JahrulnrLib.getRequest(episodelist.link, null).replaceAll("\n", "");

            episodeM = JahrulnrLib.preg_match(h, "\\\"episodeNumber\\\":\\\"?(.*?)\\\"");
            episode = episodeM.find() ? episodeM.group(1) : "";
            if (animelist.nama == null) {
                coverM = JahrulnrLib.preg_match(h, Pattern.quote("\"primaryImageOfPage\":{\"@id\":\"") + "?(.*?).(jpe?g|png)" + Pattern.quote("\"}"));
                namaM = JahrulnrLib.preg_match(h, Pattern.quote("\"caption\":\"") + "?(.*?)" + Pattern.quote("\","));
                animeLinkM = JahrulnrLib.preg_match(h, "\\Q\"url\":\"https://75.119.159.228/anime/\\E(.*?)\\Q\"},\"video\"\\E");

                cover = coverM.find() ? coverM.group(1) + "." + coverM.group(2) : "";
                nama = namaM.find() ? namaM.group(1) : "";
                animeLink = animeLinkM.find() ? "https://75.119.159.228/anime/" + animeLinkM.group(1) : "";
            } else {
                cover = animelist.img_link;
                nama = animelist.nama;
                animeLink = animelist.link;
            }

            List<episodeServerList> episodeServerLists = getServer(h);
            episodeServerAdapter adapter = new episodeServerAdapter(act, episodeServerLists);

            // Detail
            act.runOnUiThread(() -> {
                Picasso.get().load(StringEscapeUtils.unescapeJava(cover)).into(iv_cover);
                System.out.println(StringEscapeUtils.unescapeJava(cover));
                String epsPattern = "(.*?)\\s+Episode\\s+([0-9]+)?( ?Sub Indo)?";
                assert nama != null;
                String finalNama = StringEscapeUtils.unescapeJava(nama.replaceAll(epsPattern, "$1"));
                assert episode != null;
                String e = episode.replaceAll(epsPattern, "Episode $2");
                tv_title.setText(finalNama);
                tv_episode.setText(": " + e);
                viewGroup.setVisibility(View.GONE);
                it.animate(episode_preview, true);

                GridView sgv = act.findViewById(R.id.serverGridview);
                sgv.setAdapter(adapter);
                sgv.setOnItemClickListener((adapterView, view, i, l) -> {
                    Intent intent = new Intent(act, nontonView.class);
                    intent.putExtra("nama", finalNama);
                    intent.putExtra("img_link", cover);
                    intent.putExtra("anime_link", animeLink);
                    intent.putExtra("episode", "Episode " + e);
                    intent.putExtra("episode_link", episodelist.link);
                    intent.putExtra("server", episodeServerLists.get(i).server);
                    act.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                });
                it.animate(relativeLayout, false);
            });
        });
    }

    public static class episodeServerList {
        public String nama, server;

        @NonNull
        @Override
        public String toString() {
            return "{\"nama\":\"" + nama + "\", \"server\":\"" + server + "\"}";
        }
    }

    public static List<episodeServerList> getServer(String source) {
        Matcher server = JahrulnrLib.preg_match(source,
                Pattern.quote("id=\"player-option-") + "([0-9])" +
                        Pattern.quote("\" data-post=\"") + "([0-9]+)" +
                        Pattern.quote("\" data-type=\"") + "([a-zA-Z\\-]*)" +
                        Pattern.quote("\" data-nume=\"") + "([0-9]+)" +
                        Pattern.quote("\"")
        );

        List<episodeServerList> serverLists = new ArrayList<>();
        while (server.find()) {
            String p = "action=player_ajax&post=" + server.group(2) +
                    "&type=" + server.group(3) +
                    "&nume=" + server.group(4);
            episodeServerList epl = new episodeServerList();
            epl.nama = server.group(3);
            epl.server = p;
            serverLists.add(epl);
        }

        return serverLists;
    }

    public static class episodeServerAdapter extends BaseAdapter {
        Activity activity;
        List<episodeServerList> episodeServerLists;
        LayoutInflater inflater;

        public episodeServerAdapter(Activity activity, List<episodeServerList> episodeServerLists) {
            this.activity = activity;
            this.episodeServerLists = episodeServerLists;
            inflater = LayoutInflater.from(activity);
        }

        @Override
        public int getCount() {
            return episodeServerLists.size();
        }

        @Override
        public Object getItem(int i) {
            return episodeServerLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null)
                view = inflater.inflate(R.layout.episode_server_list, null);
            TextView textView = view.findViewById(R.id.serverName);
            textView.setText(episodeServerLists.get(i).nama);

            return view;
        }
    }
}

