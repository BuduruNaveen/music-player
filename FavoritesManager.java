package com.example.powerplayer;

import android.content.Context;
import android.content.SharedPreferences;

public class FavoritesManager {
    private static final String PREFS = "power_prefs";
    private static final String KEY = "favorites";
    private SharedPreferences prefs;

    public FavoritesManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void addFavorite(String uri) {
        prefs.edit().putString(uri, uri).apply();
    }

    public void removeFavorite(String uri) {
        prefs.edit().remove(uri).apply();
    }

    public boolean isFavorite(String uri) {
        return prefs.contains(uri);
    }
}
