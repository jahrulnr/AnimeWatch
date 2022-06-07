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
    private final File dbFile;
    private List<episodeList> epsList = new ArrayList<>();

    public dbFiles(Activity activity){
        this.activity = activity;
        this.db = activity.getFilesDir().getPath() + db;
        this.dbFile = new File(this.db);
        this.epsList = new ArrayList<>();
    }

    public boolean exists(){
        return dbFile.exists();
    }

    public String path() {
        return db;
    }

    public void add(episodeList episodelist){
        if(epsList.isEmpty())
            epsList = getList();
        epsList.removeIf(episodeList -> episodeList.getLink().equals(episodelist.link));
        epsList.add(episodelist);
    }

    public void save(){
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(new FileOutputStream(path()));
            out.writeObject((Object) epsList);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<episodeList> getList(){
        List<episodeList> epsList = new ArrayList<>();

        if(new File(path()).exists()){
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new FileInputStream(path()));
                epsList = (List<episodeList>) in.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Collections.reverse(epsList);
        }

        return epsList;
    }

    public episodeList getItem(int i){
        List<episodeList> epsList = getList();
        return epsList.get(i);
    }

    public boolean writeSource(String source, String filename){
        boolean success = false;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    activity.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(source);
            outputStreamWriter.close();
            success = true;
        }
        catch (IOException | IllegalArgumentException e) {
            System.err.println("dbFiles: File write failed: " + e);
        }

        return success;
    }

    public String readSource(String filename){
        String ret = "";
        try {
            InputStream inputStream = activity.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("dbFiles: File not found: " + e);
        } catch (IOException e) {
            System.err.println("dbFiles: Can not read file: " + e);
        }

        return ret;
    }
}
