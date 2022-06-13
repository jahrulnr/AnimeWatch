package jahrulnr.animeWatch.Class;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class dbFiles {
    public static String animeUpdateSource = "animeUpdateSource";
    public static String animeListSource = "animeListSource";
    public static String mangaUpdateSource = "mangaUpdateSource";
    public static String mangaListSource = "mangaListSource";
    private String db = "/db.json";
    private final Activity activity;
    private List<_anime.animeEpisode> epsList = new ArrayList<>();

    public dbFiles(Activity activity) {
        this.activity = activity;
        this.db = activity.getFilesDir().getPath() + db;
    }

    public String path() {
        return db;
    }

    public void add(_anime.animeEpisode episodelist) {
        if (epsList.isEmpty())
            epsList = getList(false);
        epsList.removeIf(epsList -> epsList.getLink().equals(episodelist.link));
        epsList.add(episodelist);
    }

    public void save() {
        ObjectOutput out;

        try {
            out = new ObjectOutputStream(new FileOutputStream(path()));
            out.writeObject(epsList);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<_anime.animeEpisode> getList() {
        return getList(true);
    }

    public List<_anime.animeEpisode> getList(boolean reverse) {
        List<_anime.animeEpisode> epslist = new ArrayList<>();

        if (new File(path()).exists()) {
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(new FileInputStream(path()));
                epslist = (List<_anime.animeEpisode>) in.readObject();
                if(reverse) Collections.reverse(epslist);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(e.toString());
            }
        }

        return epslist;
    }

    public boolean writeSource(String source, String filename) {
        boolean success = false;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    activity.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(source);
            outputStreamWriter.close();
            success = true;
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("dbFiles: File write failed: " + e);
        }

        return success;
    }

    public String readSource(String filename) {
        String ret = "";
        try {
            InputStream inputStream = activity.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            System.err.println("dbFiles: File not found: " + e);
        } catch (IOException e) {
            System.err.println("dbFiles: Can not read file: " + e);
        }

        return ret;
    }
}
