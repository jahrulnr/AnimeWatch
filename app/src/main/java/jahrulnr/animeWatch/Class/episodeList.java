package jahrulnr.animeWatch.Class;

import java.io.Serializable;

public class episodeList implements Serializable {
    private static final long serialVersionUID = 61198L;
    public String episode, link;
    public animeList animeList = new animeList();

    public void setNama(String nama) {
        this.animeList.nama = nama;
    }

    public String getNama() {
        return animeList.nama;
    }

    public void setAnime_Link(String link) {
        this.animeList.link = link;
    }

    public String getAnime_Link() {
        return animeList.link;
    }

    public void setImg_link(String img_link) {
        this.animeList.img_link = img_link;
    }

    public String getImg_link() {
        return animeList.img_link;
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
        return "episodeList{" +
                animeList.toString() +
                ", episode='" + episode + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
