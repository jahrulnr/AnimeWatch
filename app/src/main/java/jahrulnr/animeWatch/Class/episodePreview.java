package jahrulnr.animeWatch.Class;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jahrulnr.animeWatch.JahrulnrLib;
import jahrulnr.animeWatch.R;
import jahrulnr.animeWatch.ui.nontonView;

public class episodePreview {

    private RelativeLayout relativeLayout;
    private ViewGroup viewGroup;
    private RelativeLayout episode_preview;
    private animeClick ac;

    public episodePreview(Activity act, JahrulnrLib it, ViewGroup viewGroup, episodeList episodelist, @Nullable animeClick ac) {
        this.ac = ac;
        this.viewGroup = viewGroup;
        animeList animelist = episodelist.animeList;
        relativeLayout = act.findViewById(R.id.loadingContainer);
        episode_preview = act.findViewById(R.id.episode_preview);
        ImageView iv_cover = act.findViewById(R.id.animeEpsCover);
        TextView tv_title = act.findViewById(R.id.animeTitle),
                      tv_episode = act.findViewById(R.id.animeEpisode);

        relativeLayout.setVisibility(View.VISIBLE);
        it.executer(() -> {
            String h = JahrulnrLib.getRequest(episodelist.link, null).replaceAll("\n", "");
            Matcher coverM, namaM, animeLinkM, episodeM;
            String cover, nama, animeLink, episode;

            episodeM = JahrulnrLib.preg_match(h, "\\\"episodeNumber\\\":\\\"?(.*?)\\\"");
            episode = episodeM.find() ? episodeM.group(1) : "";
            if(animelist.nama == null) {
                coverM = JahrulnrLib.preg_match(h, Pattern.quote("\"primaryImageOfPage\":{\"@id\":\"") + "?(.*?).(jpe?g|png)" + Pattern.quote("\"}"));
                namaM = JahrulnrLib.preg_match(h, Pattern.quote("\"caption\":\"") + "?(.*?)" + Pattern.quote("\","));
                animeLinkM = JahrulnrLib.preg_match(h, "\\Q\"url\":\"https://75.119.159.228/anime/\\E(.*?)\\Q\"},\"video\"\\E");

                cover = coverM.find() ? coverM.group(1) + "." + coverM.group(2) : "";
                nama = namaM.find() ? namaM.group(1) : "";
                animeLink = animeLinkM.find() ? "https://75.119.159.228/anime/" + animeLinkM.group(1) : "";
                System.out.println("animeLink1" +animeLink);
            }else{
                cover = animelist.img_link;
                nama = animelist.nama;
                animeLink = animelist.link;
                System.out.println("animeLink2" +animeLink);
            }

            List<episodeServerList> episodeServerLists = getServer(h);
            episodeServerAdapter adapter = new episodeServerAdapter(act, episodeServerLists);

            // Detail
            act.runOnUiThread(() -> {
                Animation animation = AnimationUtils.loadAnimation(act,R.anim.grid_animation);
                LayoutAnimationController controller = new LayoutAnimationController(animation);
                Picasso.get().load(cover).into(iv_cover);
                String epsPattern = "(.*?)\\s+Episode\\s+([0-9]+)?( ?Sub Indo)?";
                String spacePattern = "  +";
                String finalNama = nama.replaceAll(spacePattern,  "").replaceAll(epsPattern, "$1");
                String e = episode.replaceAll(spacePattern,  "").replaceAll(epsPattern, "Episode $2");
                tv_title.setText(finalNama);
                tv_episode.setText(": " + e);
                viewGroup.setVisibility(View.GONE);
                episode_preview.setLayoutAnimation(controller);
                episode_preview.setVisibility(View.VISIBLE);

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
                    act.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                });
                relativeLayout.setVisibility(View.GONE);
                new onBackPress(episode_preview, () -> {
                    this.close();
                });
            });
        });
    }

    public static class episodeServerList {
        public String nama, server;

        @Override
        public String toString() {
            return "{\"nama\":\"" + nama + "\", \"server\":\"" + server + "\"}";
        }
    }

    public static List<episodeServerList> getServer(String source){
        Matcher server = JahrulnrLib.preg_match(source,
                Pattern.quote("id=\"player-option-") + "([0-9])" +
                        Pattern.quote("\" data-post=\"") + "([0-9]+)" +
                        Pattern.quote("\" data-type=\"") + "([a-zA-Z\\-]*)" +
                        Pattern.quote("\" data-nume=\"") + "([0-9]+)" +
                        Pattern.quote("\"")
        );

        List<episodeServerList> serverLists = new ArrayList<>();
        while (server.find()) {
            String p = "action=player_ajax&post="+server.group(2)+
                    "&type=" + server.group(3) +
                    "&nume=" + server.group(4);
//            p = JahrulnrLib.getRequest(JahrulnrLib.config.apiLink, p, null);
            episodeServerList epl = new episodeServerList();
            epl.nama = server.group(3);
            epl.server = p;
            serverLists.add(epl);
        }
        return serverLists;
    }

    public static class episodeServerAdapter extends BaseAdapter{

        Activity activity;
        List<episodeServerList> episodeServerLists;
        LayoutInflater inflater;
        public episodeServerAdapter(Activity activity, List<episodeServerList> episodeServerLists){
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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null)
                view = inflater.inflate(R.layout.episode_server_list, null);

            TextView textView = view.findViewById(R.id.serverName);
            textView.setText(episodeServerLists.get(i).nama);
            return view;
        }
    }

    public void close(){
        episode_preview.setVisibility(View.GONE);
        viewGroup.startLayoutAnimation();
        viewGroup.setVisibility(View.VISIBLE);
        if(ac != null)
            new onBackPress(viewGroup, () -> {
                ac.close();
            });
    }
}

