package jahrulnr.animeWatch.Class;

import java.io.Serializable;

public class animeList implements Serializable {
    private static final long serialVersionUID = 60599L;
    public String nama, link, img_link, status;

    public boolean isEmpty(){
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

    @Override
    public String toString() {
        return "animeList{" +
                "nama='" + nama + '\'' +
                ", link='" + link + '\'' +
                ", img_link='" + img_link + '\'' +
                '}';
    }
}
