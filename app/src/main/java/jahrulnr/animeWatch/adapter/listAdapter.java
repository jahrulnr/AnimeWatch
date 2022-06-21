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

import java.util.ArrayList;
import java.util.List;

import jahrulnr.animeWatch.Class._anime;
import jahrulnr.animeWatch.Class._manga;
import jahrulnr.animeWatch.R;

public class listAdapter {
    public static class animeListAdapter extends BaseAdapter {
        Context context;
        List<_anime> animelist, animelists_original;
        LayoutInflater inflter;

        public animeListAdapter(Context applicationContext, List<_anime> animelist_in) {
            this.context = applicationContext;
            this.animelists_original = animelist_in;
            this.animelist = animelists_original;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return animelist.size();
        }

        @Override
        public _anime getItem(int i) {
            return animelist.get(i);
        }

        public List<_anime> getItems() {
            return animelist;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.animeall_listview, null);
            }
            _anime item = animelist.get(i);
            TextView namaAnime = view.findViewById(R.id.animeName);

            namaAnime.setText(item.nama.replace("Sub Indonesia", ""));
            return view;
        }

        public void filter(String text) {
            String query = text.toLowerCase();
            animelist = new ArrayList<>();
            if (text.length() == 0) {
                animelist = animelists_original;
            } else {
                for (_anime m : animelists_original) {
                    if (m.nama.toLowerCase().contains(query)) {
                        animelist.add(m);
                    }
                }
            }
        }
    }

    public static class mangaListAdapter extends BaseAdapter {
        Context context;
        List<_manga> mangalist, mangalists_original;
        LayoutInflater inflter;

        public mangaListAdapter(Context applicationContext, List<_manga> mangaList_in) {
            this.context = applicationContext;
            this.mangalists_original = mangaList_in;
            this.mangalist = mangalists_original;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return mangalist.size();
        }

        @Override
        public _manga getItem(int i) {
            return mangalist.get(i);
        }

        public List<_manga> getItems() {
            return mangalist;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflter.inflate(R.layout.mangaall_listview, null);
            }
            _manga item = mangalist.get(i);
            ImageView mangaCover = view.findViewById(R.id.mangaCover);
            TextView namaManga = view.findViewById(R.id.mangaName);
            Picasso.get().load(item.img).into(mangaCover);
            namaManga.setText(item.manga);
            return view;
        }

        public void filter(String text) {
            String query = text.toLowerCase();
            mangalist = new ArrayList<>();
            if (text.length() == 0) {
                mangalist = mangalists_original;
            } else {
                for (_manga m : mangalists_original) {
                    if (m.manga.toLowerCase().contains(query)) {
                        mangalist.add(m);
                    }
                }
            }
        }
    }
}
