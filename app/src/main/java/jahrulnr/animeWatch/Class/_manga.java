package jahrulnr.animeWatch.Class;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class _manga implements Serializable {
    private static final long serialVersionUID = 6598L;
    public String manga, img, deskripsi;
    public String link;
    public mangaChapter chapter = new mangaChapter();

    public String getManga() {
        return manga;
    }

    public void setManga(String manga) {
        this.manga = manga;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public static class mangaChapter implements Serializable {
        private static final long serialVersionUID = 61199L;
        public int chapter;
        public String link;

        public int getChapter() {
            return chapter;
        }

        public void setChapter(int chapter) {
            this.chapter = chapter;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @NonNull
        @Override
        public String toString() {
            return "mangaChapter{" +
                    "chapter=" + chapter +
                    ", link='" + link + '\'' +
                    '}';
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "_manga{" +
                "manga='" + manga + '\'' +
                ", img='" + img + '\'' +
                ", deskripsi='" + deskripsi + '\'' +
                ", link='" + link + '\'' +
                ", chapter=" + chapter.toString() +
                '}';
    }
}
