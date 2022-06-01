package jahrulnr.animeWatch.Class;

import java.io.Serializable;

public class episodeList implements Serializable {
    private static final long serialVersionUID = 61198L;
    public String episode, link;
    public animeList animeList = new animeList();

    public void setAnimeList(animeList animeList) {
        this.animeList = animeList;
    }

    public animeList getAnimeList() {
        return animeList;
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
