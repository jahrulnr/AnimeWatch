package jahrulnr.animeWatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JahrulnrLib {
    @SuppressLint("StaticFieldLeak")
    private static Activity activity;
    private static String text = "";
    public static config config = new config();

    public JahrulnrLib() {}

    public JahrulnrLib(Activity act) {
        activity = act;
    }

    public void animate(ViewGroup view, boolean start){
        if(start)
            view.animate()
                    .alpha(1)
                    .withStartAction(() -> {
                        view.setVisibility(View.VISIBLE);
                        view.startLayoutAnimation();
                    });
        else
            view.animate()
                .alpha(0)
                .setDuration(1000)
                .withEndAction(() -> view.setVisibility(View.GONE));
    }

    public static Matcher preg_match(String source, String pattern) {
        if (source != null) {
//            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Pattern p = Pattern.compile(pattern);
            Matcher m = null;
            try {
                m = p.matcher(source.replaceAll("\n", ""));
            } catch (IllegalStateException e) {
                Log.e("Preg_Match", e.toString());
            }
            return m;
        }
        return null;
    }

    public static boolean checkNetwork(Activity act) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
            Log.i("Internet", "Connected");
        } else {
            Toast.makeText(act.getApplicationContext(), "Internet tidak tersedia.", Toast.LENGTH_LONG).show();
            Log.i("Internet", "Not connected");
        }

        return connected;
    }

    public void executer(Runnable run) {
        Executors.newSingleThreadExecutor().execute(run);
    }

    private static HashMap<String, String> getRequestProperties(HashMap<String, String> properties){
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("User-Agent", config.userAgent);
//        requestProperties.put("X-Requested-With", "XMLHttpRequest");

        if(properties != null){
            requestProperties.putAll(properties);
        }

        return requestProperties;
    }

    // Without Data
    public static String getRequest(String link, HashMap<String, String> properties) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        text = "";
        URL url;
        try {
            url = new URL(link);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches( true );
            urlConnection.setDefaultUseCaches( true );
            urlConnection.setReadTimeout(60000);
            urlConnection.setConnectTimeout(5 * 1000);
            urlConnection.setRequestMethod("GET");
            urlConnection.addRequestProperty("User-Agent", config.userAgent);
            getRequestProperties(properties).forEach((s, s2) -> {
                urlConnection.addRequestProperty(s, s2);
            });
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String input;
            StringBuffer stringBuffer = new StringBuffer();
            while ((input = in.readLine()) != null) {
                stringBuffer.append(input);
            }
            in.close();
            urlConnection.disconnect();
            text = stringBuffer.toString();
        }catch (SocketTimeoutException e){
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.replaceAll("\n", "").replaceAll("  +", " ");
    }

    // With Data
    public static String getRequest(String link, String data, HashMap<String, String> properties) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        BufferedReader reader = null;
        URL url = null;
        try {
            // Defined URL  where to send data
            url = new URL(link);

            // Send POST data request
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(true);
            urlConnection.setDefaultUseCaches( true );
            urlConnection.setReadTimeout(60000);
            urlConnection.setConnectTimeout(5 * 1000);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("User-Agent", config.userAgent);
            getRequestProperties(properties).forEach((s, s2) -> {
                urlConnection.addRequestProperty(s, s2);
            });

            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line);
            }
            text = sb.toString();
        } catch (SocketTimeoutException e) {
            Log.e("SocketTimeoutException", e.toString());
            Toast.makeText(activity, "Koneksi timeout. Silakan refresh.", Toast.LENGTH_LONG).show();
            return "";
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportEncoding", e.toString());
            e.printStackTrace();
            Toast.makeText(activity, "Ada masalah, coba lagi.", Toast.LENGTH_LONG).show();
            return "";
        } catch (IOException e) {
            Log.e("IOException", e.toString());
            e.printStackTrace();
            Toast.makeText(activity, "Ada masalah, coba lagi.", Toast.LENGTH_LONG).show();
            return "";
        } catch (Exception ex) {
            Log.e("getRequest_1", ex.getMessage());
            ex.printStackTrace();
            Toast.makeText(activity, "Ada masalah, coba lagi.", Toast.LENGTH_LONG).show();
            return "";
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                Log.e("getRequest_2", ex.toString());
                Toast.makeText(activity, "Ada masalah. Silakan refresh.", Toast.LENGTH_LONG).show();
                return "";
            }
        }

        return text.replaceAll("\n", "").replaceAll("  +", " ");
    }
}
