package jahrulnr.animeWatch;

public class configManga {
    public static String home = "https://data.komiku.id/";
    public static String update = home + "pustaka/";
    public static String list = home + "daftar-komik/";

    public static String updatePattern = "class=\\\"bgei\\\"> " +
            // link 1
            "<a href=\\\"(.*?)\\\"> " +
            // img 3
            "<img src=\\\"(.*?)\\\" data-src=\\\"(.*?)\\\" class(.*?)class=\\\"kan\\\"> " +
            // link 5
            "<a href=\\\"(.*?)\\\">" +
            // Judul 6
            "<h3> (.*?) </h3>(.*?) " +
            // deskripsi 8
            "<p> (.*?) </p>(.*?)new1\\\"> <a href=\\\"(.*?)new1\\\"> " +
            // link chapter 11
            "<a href=\\\"(.*?) title(.*?)" +
            // no. chapter 13
            "<span>Chapter ([0-9]+)</span>";
}
