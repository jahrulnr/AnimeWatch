package jahrulnr.animeWatch;

public class config {
    public static String home = "https://75.119.159.228/";
    public static String apiLink = home + "wp-admin/admin-ajax.php";
    public static String list = home + "anime/?mode=list";
    public static String userAgent = "Mozilla/5.0 (Linux; Android 9; SAMSUNG SM-A505F Build/PPR1.180610.011) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/9.4 Chrome/87.0.3396.87 Mobile Safari/537.36";

    // Home
    public static String update_pattern = "<article class=\"animeseries" +
            // trash
            "?(.*?)" +
            "<a href=\"" +
            // link 2
            "?(.*?)" +
            "\">" +
            // trash
            "?(.*?)" +
            "<span class=\"dashicons dashicons-clock\"></span> " +
            // status 4
            "?(.*?)" +
            "</span>" +
            // trash
            "?(.*?)" +
            "<img src=\"" +
            // cover 6
            "?(.*?)" +
            "\" alt" +
            // trash
            "?(.*?)" +
            "<h2 class=\"title less nlat entry-title\"><span>" +
            // Nama 8
            "?(.*?)" +
            "</span></h2>" +
            // trash
            "?(.*?)" +
            "</article>";

    // Dashboard
    public static String list_pattern = "\\Qclass=\"series\" id=\"\\E([0-9]+)\\Q\" href=\"\\E(.*?)\\Q\">\\E(.*?)\\Q</a>\\E";
    public static String img_pattern = "\\Q\"primaryImageOfPage\":{\"@id\":\"\\E(.*?)\\Q\"}\\E";
    public static String studio_pattern = "\\Q<b>Studios:</b> \\E(.*?)\\Q</span>\\E";
    public static String rilis_pattern = "\\Q<b>Aired:</b> \\E(.*?)\\Q</span>\\E";
    public static String genre_pattern = "\\Q<a href=\"https://75.119.159.228/genres/\\E(.*)\\Qrel=\"tag\">\\E(.*?)\\Q</a>\\E";
    public static String episode_pattern = "\\Q<span class=\"t1\"><a href=\"\\E" +
            // link 1
            "(.*?)" +
            "\\Q\">\\E" +
            // Episode 2
            "(.*?)" +
            "\\Q</a>\\E" +
            // trash
            "(.*?)" +
            "\\Qclass=\"t3\">\\E" +
            // Upload 4
            "(.*?)" +
            "\\Q</span>\\E";
}
