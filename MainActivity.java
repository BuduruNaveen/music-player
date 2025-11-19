package com.example.powerplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private List<Song> songs = new ArrayList<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongAdapter(songs, song -> {
            // start service to play
            Intent intent = new Intent(this, PlayerService.class);
            intent.setAction(PlayerService.ACTION_PLAY);
            intent.putExtra(PlayerService.EXTRA_URI, song.getUri().toString());
            intent.putExtra(PlayerService.EXTRA_TITLE, song.getTitle());
            intent.putExtra(PlayerService.EXTRA_ARTIST, song.getArtist());
            ContextCompat.startForegroundService(this, intent);
        });
        recyclerView.setAdapter(adapter);

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<java.util.Map<String,Boolean>>() {
            @Override
            public void onActivityResult(java.util.Map<String,Boolean> result) {
                boolean granted = false;
                for (Boolean b : result.values()) { if (b) { granted = true; break; } }
                if (granted) loadSongs();
                else showPermissionDialog();
            }
        });

        checkPermissionsAndLoad();
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission required")
                .setMessage("Storage permission is required to read local music files. Please grant it from settings.")
                .setPositiveButton("Open Settings", (d, w) -> {
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(i);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkPermissionsAndLoad() {
        List<String> perms = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) perms.add(Manifest.permission.READ_MEDIA_AUDIO);
        else perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> needed = new ArrayList<>();
        for (String p : perms) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) needed.add(p);
        }
        if (!needed.isEmpty()) {
            permissionLauncher.launch(needed.toArray(new String[0]));
        } else {
            loadSongs();
        }
    }

    private void loadSongs() {
        executor.execute(() -> {
            List<Song> list = MediaStoreScanner.queryAudioFiles(getContentResolver());
            runOnUiThread(() -> {
                songs.clear();
                songs.addAll(list);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
