package jahrulnr.animeWatch.Class;

import android.view.KeyEvent;
import android.view.View;

public class onBackPress {
    public onBackPress(View view, Runnable runnable){
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                runnable.run();
                return true;
            }
            return false;
        });
    }
}
