package jahrulnr.animeWatch.Class;

import android.view.View;

public class MessageEvent {
    private int id;
    private boolean canExit;
    View view;

    public int getId() {
        return id;
    }

    public MessageEvent(int id) {
        this.id = id;
    }

    public MessageEvent(boolean canExit) {
        this.canExit = canExit;
    }

    public MessageEvent(View view) {
        this.view = view;
    }
}
