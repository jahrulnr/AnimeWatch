package jahrulnr.animeWatch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import jahrulnr.animeWatch.Class.MessageEvent;
import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private JahrulnrLib it;
    View root;
    private ActivityMainBinding binding;
    private RelativeLayout splashContainer;
    private TextView splashProses;

    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity Created");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = binding.getRoot();
        it = new JahrulnrLib(this);
        splashContainer = root.findViewById(R.id.splashContainer);
        splashProses = root.findViewById(R.id.splash_proses);
        JahrulnrLib.checkNetwork(this);
        binding.navView.setVisibility(View.GONE);

        ImageView splashImg = splashContainer.findViewById(R.id.splash_image);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        try {
            String splash_path = "splash";
            String[] splash_list = getAssets().list(splash_path);
            String splash_img = splash_path + File.separator + splash_list[
                    new Random().nextInt(splash_list.length)];
            splashImg.setImageBitmap(
                    BitmapFactory.decodeStream(getAssets().open(splash_img)));
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }

        if(splashContainer.getVisibility()==View.VISIBLE) {
            AtomicInteger count = new AtomicInteger(1);
            it.timerExecuter(() ->
                    runOnUiThread(() -> {
                        if (count.getAndIncrement() % 15 == 0)
                            Toast.makeText(this, "Koneksi lambat, mohon bersabar", Toast.LENGTH_LONG).show();
                    }), 1000, 1000);

            it.executer(() -> {
                // anime source setup
                String animePost = "action=loadmore&type=home&page=0";
                String animeUpdateSource = JahrulnrLib.getRequest(configAnime.apiLink, animePost, null);
                runOnUiThread(() -> splashProses.setText("Mengunduh daftar anime terbaru"));
                String animeListSource = JahrulnrLib.getRequest(configAnime.list, null);
                runOnUiThread(() -> splashProses.setText("Mengunduh semua daftar anime"));

                // manga source setup
                String mangaUpdateSource = JahrulnrLib.getRequest(configManga.update, null);
                runOnUiThread(() -> splashProses.setText("Mengunduh daftar manga dan komik terbaru"));
                String mangaListSource = JahrulnrLib.getRequest(configManga.list, null);
                runOnUiThread(() -> splashProses.setText("Mengunduh semua daftar manga"));

                dbFiles db = new dbFiles(this);
                boolean success = db.writeSource(animeUpdateSource, dbFiles.animeUpdateSource)
                        && db.writeSource(animeListSource, dbFiles.animeListSource)
                        && db.writeSource(mangaUpdateSource, dbFiles.mangaUpdateSource)
                        && db.writeSource(mangaListSource, dbFiles.mangaListSource);

                runOnUiThread(() -> {
                    it.timerCancel();
                    if (success) {
                        actionBar.show();
                        binding.navView.setVisibility(View.VISIBLE);
                        it.animate(splashContainer, false);
                        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                                R.id.navigation_home, R.id.navigation_list, R.id.navigation_history)
                                .build();
                        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_activity_main);
                        NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);
                        NavigationUI.setupWithNavController(binding.navView, navController);
                        if(root.findViewById(R.id.homeGridview) != null)
                            it.animate(root.findViewById(R.id.homeGridview), true);

                        updateCheck();
                    } else {
                        Toast.makeText(this, "Tidak dapat memuat data.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private int activeFragment = 0;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        activeFragment = event.getId();
        System.out.println(activeFragment == R.id.fragment_list);
    }

    private void closeLayout(ViewGroup viewGroup, View view) {
        view.setVisibility(View.GONE);
        it.animate(viewGroup, true);
    }

    @Override
    public void onBackPressed() {
        if (splashContainer != null && splashContainer.getVisibility() == View.GONE) {
            if(mangaBackPressed() && animeBackPressed())
                new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi")
                    .setMessage("Ingin keluar dari aplikasi ini?")
                    .setPositiveButton("Tidak", null)
                    .setNegativeButton("Ya", (dialogInterface, i) -> finish()).show();
        }
    }

    private boolean animeBackPressed() {
        boolean canExit = true;
        RelativeLayout episode_preview = root.findViewById(R.id.episode_preview);
        if (activeFragment == R.id.fragment_home
                && episode_preview.getVisibility() == View.VISIBLE) {
            episode_preview.setVisibility(View.GONE);
            root.findViewById(R.id.pullRefresh).setVisibility(View.VISIBLE);
            it.animate(root.findViewById(R.id.homeGridview), true);
        } else if (activeFragment == R.id.fragment_list) {
            if (episode_preview.getVisibility() == View.VISIBLE) {
                closeLayout(root.findViewById(R.id.episode_view), episode_preview);
                canExit = false;
            } else {
                closeLayout(root.findViewById(R.id.animeListContainer),
                        root.findViewById(R.id.episode_view));
            }
        } else if (activeFragment == R.id.fragment_history
                && episode_preview.getVisibility() == View.VISIBLE)
            closeLayout(root.findViewById(R.id.historyContainer), episode_preview);
        return canExit;
    }

    private boolean mangaBackPressed() {
        boolean canExit = true;
        return canExit;
    }

    private void updateCheck(){
        new AppUpdater(this)
            .setUpdateFrom(UpdateFrom.GITHUB)
            .setGitHubUserAndRepo("jahrulnr", "AnimeWatch")
            .start();
    }
}