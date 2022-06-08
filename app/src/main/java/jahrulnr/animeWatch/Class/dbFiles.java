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
    public static String updateSource = "updateSource";
    public static String listSource = "listSource";
    private String db = "/db.json";
    private final Activity activity;
    private List<episodeList> epsList;

    public dbFiles(Activity activity) {
        this.activity = activity;
        this.db = activity.getFilesDir().getPath() + db;
        this.epsList = new ArrayList<>();
    }

    public String path() {
        return db;
    }

    public void add(episodeList episodelist) {
        if (epsList.isEmpty())
            epsList = getList();
        epsList.removeIf(episodeList -> episodeList.getLink().equals(episodelist.link));
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

    public List<episodeList> getList() {
        List<episodeList> epsList = new ArrayList<>();

        if (new File(path()).exists()) {
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(new FileInputStream(path()));
                epsList = (List<episodeList>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            Collections.reverse(epsList);
        }

        return epsList;
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
