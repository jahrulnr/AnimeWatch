package jahrulnr.animeWatch.Class;

import android.app.Activity;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class dbFiles {
    private String db = "/db.json";
    private Activity activity;
    private File dbFile;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            epsList.removeIf(episodeList -> episodeList.getLink().equals(episodelist.link));
        }
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
        return epsList;
    }

    public episodeList getItem(int i){
        List<episodeList> epsList = getList();
        return epsList.get(i);
    }
}
