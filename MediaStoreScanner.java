package com.example.powerplayer;

import android.content.ContentUris;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class MediaStoreScanner {
    public static List<Song> queryAudioFiles(ContentResolver resolver) {
        List<Song> list = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION };
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String sort = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = resolver.query(uri, projection, selection, null, sort);
        if (cursor != null) {
            int idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int durCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idCol);
                String title = cursor.getString(titleCol) != null ? cursor.getString(titleCol) : "Unknown";
                String artist = cursor.getString(artistCol) != null ? cursor.getString(artistCol) : "Unknown";
                long duration = cursor.getLong(durCol);
                Uri contentUri = ContentUris.withAppendedId(uri, id);
                list.add(new Song(title, artist, duration, contentUri));
            }
            cursor.close();
        }
        return list;
    }
}
