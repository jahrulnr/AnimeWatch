package jahrulnr.animeWatch.Class;

public class MessageEvent {
    private int id;
    private boolean canExit;

    public int getId() {
        return id;
    }

    public MessageEvent(int id) {
        this.id = id;
    }

    public MessageEvent(boolean canExit) {
        this.canExit = canExit;
    }
}
