package jahrulnr.animeWatch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import jahrulnr.animeWatch.Class.dbFiles;
import jahrulnr.animeWatch.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private JahrulnrLib it;
    View root;
    private ActivityMainBinding binding;
    private RelativeLayout splashContainer;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        root = binding.getRoot();
        it = new JahrulnrLib(this);
        splashContainer = root.findViewById(R.id.splashContainer);
        JahrulnrLib.checkNetwork(this);
        binding.navView.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            e.printStackTrace();
        }

        if(splashContainer.getVisibility()==View.VISIBLE && root.findViewById(R.id.fragment_home) != null) {
            AtomicInteger count = new AtomicInteger(1);
            it.timerExecuter(() ->
                    runOnUiThread(() -> {
                        if (count.getAndIncrement() % 15 == 0)
                            Toast.makeText(this, "Koneksi lambat, mohon bersabar", Toast.LENGTH_LONG).show();
                    }), 1000, 1000);

            it.executer(() -> {
                String post = "action=loadmore&type=home&page=0";
                String updateSource = JahrulnrLib.getRequest(config.apiLink, post, null);
                String listSource = JahrulnrLib.getRequest(config.list, null);
                boolean success = new dbFiles(this).writeSource(updateSource, dbFiles.updateSource)
                        && new dbFiles(this).writeSource(listSource, dbFiles.listSource);
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
                        updateCheck();
                    } else {
                        onStart();
                    }
                });
            });
        }
    }

    private void closeLayout(ViewGroup viewGroup, View view) {
        view.setVisibility(View.GONE);
        it.animate(viewGroup, true);
    }

    @Override
    public void onBackPressed() {
        if (splashContainer != null && splashContainer.getVisibility() == View.GONE) {
            RelativeLayout episode_preview = root.findViewById(R.id.episode_preview);
            if (root.findViewById(R.id.fragment_home) != null
                    && episode_preview.getVisibility() == View.VISIBLE)
                closeLayout(root.findViewById(R.id.pullRefresh), episode_preview);
            else if (root.findViewById(R.id.fragment_list) != null) {
                if (episode_preview.getVisibility() == View.VISIBLE) {
                    closeLayout(root.findViewById(R.id.episode_view), episode_preview);
                } else {
                    closeLayout(root.findViewById(R.id.animeListContainer),
                            root.findViewById(R.id.episode_view));
                }
            } else if (root.findViewById(R.id.fragment_history) != null
                    && episode_preview.getVisibility() == View.VISIBLE)
                closeLayout(root.findViewById(R.id.historyContainer), episode_preview);
            else {
                new AlertDialog.Builder(this)
                        .setTitle("Konfirmasi")
                        .setMessage("Ingin keluar dari aplikasi ini?")
                        .setPositiveButton("Tidak", null)
                        .setNegativeButton("Ya", (dialogInterface, i) -> finish()).show();
            }
        }
    }

    private void updateCheck(){
        new AppUpdater(this)
            .setUpdateFrom(UpdateFrom.GITHUB)
            .setGitHubUserAndRepo("jahrulnr", "AnimeWatch")
            .start();
    }
}