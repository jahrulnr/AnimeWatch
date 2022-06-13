package jahrulnr.animeWatch.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import jahrulnr.animeWatch.Class._manga;
import jahrulnr.animeWatch.R;

public class homeListAdapter {
    public static class anime extends BaseAdapter {

        private final List<_anime.animeEpisode> animehomelist;
        LayoutInflater inflter;

        public anime(Context context, List<_anime.animeEpisode> episodelist) {
            this.animehomelist = episodelist;
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

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.animelist_view, null);
            }

            _anime.animeEpisode item = animehomelist.get(i);
            TextView anime = view.findViewById(R.id.animeName);
            ImageView cover = view.findViewById(R.id.animeCover);
            TextView status = view.findViewById(R.id.status);

            try {
                if (item.episode != null) {
                    anime.setText(item.episode);
                    Picasso.get().load(StringEscapeUtils.unescapeJava(item.anime.img_link)).into(cover);
                    status.setText(StringEscapeUtils.unescapeJava(item.anime.status));
                }
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            }

            return view;
        }
    }
    public static class manga extends BaseAdapter {

        private List<_manga> mangaList;
        LayoutInflater inflter;

        public manga(Context context, List<_manga> mangaList) {
            this.mangaList = mangaList;
            inflter = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mangaList.size();
        }

        @Override
        public Object getItem(int i) {
            return mangaList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint({"InflateParams", "SetTextI18n"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.mangalist_view, null);
            }

            _manga item = mangaList.get(i);
            TextView manga = view.findViewById(R.id.mangaName);
            TextView chapter = view.findViewById(R.id.mangaChapter);
            ImageView cover = view.findViewById(R.id.mangaCover);

            try {
                if (item.chapter.link != null) {
                    manga.setText(item.manga);
                    chapter.setText("Chapter " +item.chapter.chapter);
                    Picasso.get().load(StringEscapeUtils.unescapeJava(item.img)).into(cover);
                }
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            }

            return view;
        }
    }
}
