package jahrulnr.animeWatch.Class;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class _anime implements Serializable {
    private static final long serialVersionUID = 60599L;
    public String nama, link, img_link, status;

    public boolean isEmpty() {
        return nama == null;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama() {
        return nama;
    }


    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setImg_link(String img_link) {
        this.img_link = img_link;
    }

    public String getImg_link() {
        return img_link;
    }

    @NonNull
    @Override
    public String toString() {
        return "animeList{" +
                "nama='" + nama + '\'' +
                ", link='" + link + '\'' +
                ", img_link='" + img_link + '\'' +
                '}';
    }

    public static class animeEpisode implements Serializable {
        private static final long serialVersionUID = 61198L;
        public String episode;
        public String upload;
        public String link;
        public _anime anime = new _anime();

        public String getUpload() {
            return upload;
        }

        public void setUpload(String upload) {
            this.upload = upload;
        }

        public void setNama(String nama) {
            this.anime.nama = nama;
        }

        public String getNama() {
            return anime.nama;
        }

        public void setAnime_Link(String link) {
            this.anime.link = link;
        }

        public String getAnime_Link() {
            return anime.link;
        }

        public void setImg_link(String img_link) {
            this.anime.img_link = img_link;
        }

        public String getImg_link() {
            return anime.img_link;
        }

        public void setEpisode(String episode) {
            this.episode = episode;
        }

        public String getEpisode() {
            return episode;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @Override
        public String toString() {
            String animelist = anime.isEmpty() ? "" : anime.toString() + ", ";
            return "episodeList{" +
                    animelist +
                    "episode='" + episode + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }
}
