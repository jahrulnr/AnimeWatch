package jahrulnr.animeWatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JahrulnrLib {
    @SuppressLint("StaticFieldLeak")
    private static Activity activity;
    private static String text = "";

    public JahrulnrLib(){}
    public JahrulnrLib(Activity act){
        activity = act;
    }

    public static class config {
        public static String userAgent = "Mozilla/5.0 (Linux; Android 9; SAMSUNG SM-A505F Build/PPR1.180610.011) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/9.4 Chrome/87.0.3396.87 Mobile Safari/537.36";
        public static String host = "https://oploverz.asia/";
        public static String updateList = "anime/?order=update#";
        public static String homeList = "anime/list-mode/#";
        public static String list = "anime/list-mode/#";

        // Home
        public static String update_pattern = "div class=\"bsx\"> <a href=\"" +
                "?(.*?)" + // url 1
                "\" itemprop=\"url\" title" +
                "?(.*?)" + // trash 2
                "</div> <img src=\"" +
                "?(.*?)" + // cover 3
                "\" class=\"ts-post-image wp-post-image attachment-medium_large size-medium_large\" loading=\"lazy" +
                "?(.*?)" + // trash 4
                "<div class=\"tt\"> " +
                "?(.*?)" + // title 5
                "<h2 itemprop=\"headline\"";

        // Dashboard
        public static String list_pattern = "class=\"tip series\" rel=\"?(.*?)\" href=\"" +
                "?(.*?)\">?(.*?)</a>";
        public static String img_pattern = "ImageObject\"> <img src=\"?(.*?)\" " +
                "class=\"ts-post-image wp-post-image attachment-medium_large size-medium_large\" " +
                "loading=\"lazy\"";
        public static String status_pattern = "<b>Status:</b> ?(.*?)</span>";
        public static String studio_pattern = "<b>Studio:</b> ?(.*?)</span> ";
        public static String rilis_pattern = "<b>Released:</b> ?(.*?)</span>";
        public static String season_pattern = "<b>Season:</b> <a href=\"?(.*?)\" rel=\"tag\">?(.*?)</a></span>";
        public static String genre_pattern = "<div class=\"genxed\">" +
                "<a href=\"?(.*?)\" rel=\"tag\">?(.*?)</a>";
        public static String episode_pattern = "<li data-index=\"?(.*?)\"> " +
                "<a href=\"?(.*?)\">" +
                "<div class=\"epl-num\">?(.*?)</div>" +
                "<div class=\"epl-title\">?(.*?)</div>";
    }

    public static Matcher preg_match(String source, String pattern){
        if(source != null) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = null;
            try {
                m = p.matcher(source);
            }catch (IllegalStateException e){
                Log.e("Preg_Match", e.toString());
            }
            return m;
        }
        return null;
    }

    public static boolean checkNetwork(Activity act){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)act.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
            Log.i("Internet", "Connected");
        }else{
            Toast.makeText(act.getApplicationContext(), "Internet tidak tersedia.", Toast.LENGTH_LONG).show();
            Log.i("Internet", "Not connected");
        }

        return connected;
    }

    public void executer(Runnable run){
        Executors.newSingleThreadExecutor().execute(run);
    }

    // Without Data
    public static String getUniversalRequest(String link) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        text = "";
        URL url;
        try {
            url = new URL(link);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("User-Agent", config.userAgent);
            urlConnection.addRequestProperty("Referer", config.host);
            urlConnection.setReadTimeout(10000);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String input;
            StringBuffer stringBuffer = new StringBuffer();
            while ((input = in.readLine()) != null) {
                stringBuffer.append(input);
            }
            in.close();
            text = stringBuffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    // Without Data
    public static String getRequest(String api) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        config conf = new config();
        BufferedReader reader=null;
        URL url = null;
        URLConnection conn = null;

        try{
            // Defined URL  where to send data
            url = new URL(config.host + api);

            // Send POST data request
            conn = url.openConnection();
            conn.setConnectTimeout(5*1000);
            conn.setDoOutput(true);

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null){
                // Append server response in string
                sb.append(line + "\n");
            }

            if(!sb.toString().isEmpty())
                text = sb.substring(0, sb.length()-1);
            else
                text = sb.toString();
        }
        catch (SocketTimeoutException e){
            Log.e("SocketTimeoutException", e.toString());
            return null;
        }
        catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            Log.e("IOException", e.toString());
            e.printStackTrace();
            return null;
        }
        catch(Exception ex){
            Log.e("getRequest_1", ex.getMessage());
            ex.printStackTrace();
            return null;
        }

        finally{
            try{
                reader.close();
            }catch(Exception ex) {
                Log.e("getRequest_2", ex.toString());
                return null;
            }
        }

        // return response
//        Log.d("getRequest_3", text);
        return text;
    }

    // With Data
    public static String getRequest(String api, String data) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        config conf = new config();
        BufferedReader reader=null;
        URL url = null;
        URLConnection conn = null;

        try{
            // Defined URL  where to send data
            url = new URL(config.host + api);

            // Send POST data request
            conn = url.openConnection();
            conn.setConnectTimeout(5*1000);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null){
                // Append server response in string
                sb.append(line + "\n");
            }

            if(!sb.toString().isEmpty())
                text = sb.substring(0, sb.length()-1);
            else
                text = sb.toString();
        }
        catch (SocketTimeoutException e){
            Log.e("SocketTimeoutException", e.toString());
            Toast.makeText(activity, "Koneksi timeout. Silakan refresh.", Toast.LENGTH_LONG).show();
            return null;
        }
        catch (UnsupportedEncodingException e) {
            Log.e("UnsupportEncoding", e.toString());
            e.printStackTrace();
            Toast.makeText(activity, "Ada masalah, coba lagi.", Toast.LENGTH_LONG).show();
            return null;
        }
        catch (IOException e) {
            Log.e("IOException", e.toString());
            e.printStackTrace();
            Toast.makeText(activity, "Ada masalah, coba lagi.", Toast.LENGTH_LONG).show();
            return null;
        }
        catch(Exception ex){
            Log.e("getRequest_1", ex.getMessage());
            ex.printStackTrace();
            Toast.makeText(activity, "Ada masalah, coba lagi.", Toast.LENGTH_LONG).show();
            return null;
        }

        finally{
            try{
                reader.close();
            }catch(Exception ex) {
                Log.e("getRequest_2", ex.toString());
                Toast.makeText(activity, "Ada masalah. Silakan refresh.", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        return text;
    }
}
