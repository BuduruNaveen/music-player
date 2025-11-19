package com.example.powerplayer;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class PlayerService extends Service {

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_TOGGLE = "ACTION_TOGGLE";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREV = "ACTION_PREV";
    public static final String EXTRA_URI = "EXTRA_URI";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_ARTIST = "EXTRA_ARTIST";

    private final IBinder binder = new LocalBinder();
    private ExoPlayer player;
    private MediaSessionCompat mediaSession;
    private PlayerNotificationManager playerNotificationManager;
    private static final String CHANNEL_ID = "power_player_channel";

    public class LocalBinder extends Binder {
        PlayerService getService() { return PlayerService.this; }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSessionCompat(this, "PowerPlayerSession");

        createNotificationChannel();
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                this, CHANNEL_ID, R.string.app_name, R.string.app_name,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public CharSequence getCurrentContentTitle(Player player) {
                        return currentTitle != null ? currentTitle : "Playing";
                    }
                    @Override
                    public CharSequence getCurrentContentText(Player player) { return currentArtist != null ? currentArtist : ""; }
                    @Override
                    public android.graphics.Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        return null;
                    }
                    @Override
                    public android.app.PendingIntent createCurrentContentIntent(Player player) { return null; }
                },
                new PlayerNotificationManager.NotificationListener() {
                    @Override public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {}
                    @Override public void onNotificationPosted(int notificationId, NotificationCompat.Builder notificationBuilder) {
                        startForeground(notificationId, notificationBuilder.build());
                    }
                }
        );
        playerNotificationManager.setPlayer(player);
    }

    private String currentTitle, currentArtist;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_PLAY.equals(intent.getAction())) {
            String uri = intent.getStringExtra(EXTRA_URI);
            currentTitle = intent.getStringExtra(EXTRA_TITLE);
            currentArtist = intent.getStringExtra(EXTRA_ARTIST);
            play(Uri.parse(uri));
        } else if (intent != null && ACTION_TOGGLE.equals(intent.getAction())) {
            toggle();
        }
        return START_STICKY;
    }

    private void play(Uri uri) {
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.play();
    }

    private void toggle() {
        if (player.isPlaying()) player.pause(); else player.play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            playerNotificationManager.setPlayer(null);
            player.release();
            player = null;
        }
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "Playback", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(ch);
        }
    }
}
