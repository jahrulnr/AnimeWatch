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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLProtocolException;

public class JahrulnrLib {
    @SuppressLint("StaticFieldLeak")
    private static Activity activity;
    public static configAnime configAnime = new configAnime();

    private Timer T;

    public JahrulnrLib() {
    }
    public JahrulnrLib(Activity act) {
        activity = act;
    }

    public void timerExecuter(Runnable execute, int delay, int period) {
        T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                execute.run();
            }
        }, delay, period);
    }

    public void timerCancel() {
        if (T != null) T.cancel();
    }

    public void animate(ViewGroup view, boolean start) {
        if (start)
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

    public static void checkNetwork(Activity act) {
        ConnectivityManager connectivityManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            Log.i("Internet", "Connected");
        } else {
            act.runOnUiThread(() -> Toast.makeText(act.getApplicationContext(), "Internet tidak tersedia.", Toast.LENGTH_LONG).show());
            Log.i("Internet", "Not connected");
        }
    }

    public void executer(Runnable run) {
        Executors.newSingleThreadExecutor().execute(run);
    }

    private static HashMap<String, String> getRequestProperties(HashMap<String, String> properties) {
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("User-Agent", configAnime.userAgent);
//        requestProperties.put("X-Requested-With", "XMLHttpRequest");

        if (properties != null) {
            requestProperties.putAll(properties);
        }

        return requestProperties;
    }

    // Without Data
    public static String getRequest(String link, HashMap<String, String> properties) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String text = "";
        URL url;
        try {
            url = new URL(link);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(true);
            urlConnection.setDefaultUseCaches(true);
            urlConnection.setReadTimeout(60000);
            urlConnection.setConnectTimeout(5 * 1000);
            urlConnection.setRequestMethod("GET");
            getRequestProperties(properties).forEach(urlConnection::addRequestProperty);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String input;
            StringBuilder stringBuffer = new StringBuilder();
            while ((input = in.readLine()) != null) {
                stringBuffer.append(input);
            }
            in.close();
            urlConnection.disconnect();
            text = stringBuffer.toString();
        } catch (Exception e) {
            System.err.println("JahrulnrLib.getRequest(get): " + e.toString());
            activity.runOnUiThread(() -> Toast.makeText(activity, "Koneksi bermasalah. Silakan refresh.", Toast.LENGTH_LONG).show());
        }
        return text.replaceAll("\n", "").replaceAll("\\s\\s+", " ");
    }

    // With Data
    public static String getRequest(String link, String data, HashMap<String, String> properties) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url;
        String text = "";
        try {
            // Defined URL  where to send data
            url = new URL(link);

            // Send POST data request
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(true);
            urlConnection.setDefaultUseCaches(true);
            urlConnection.setReadTimeout(60000);
            urlConnection.setConnectTimeout(5 * 1000);
            urlConnection.setRequestMethod("POST");
            getRequestProperties(properties).forEach(urlConnection::addRequestProperty);

            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line);
            }
            reader.close();
            text = sb.toString();
        }
        catch (Exception e) {
            System.err.println("JahrulnrLib.getRequest(post): " + e.toString());
            activity.runOnUiThread(() -> Toast.makeText(activity, "Koneksi bermasalah. Silakan refresh.", Toast.LENGTH_LONG).show());
        }

        return text.replaceAll("\n", "").replaceAll("\\s\\s+", " ");
    }
}
