# Power Player (Full-feature Offline Music Player) - Java

## What is included
- Android Studio project (Java)
- Scans device media (MediaStore) for audio files
- RecyclerView list of songs
- ExoPlayer-based foreground PlayerService with notification support
- Play/Pause/Next/Prev control wiring (basic)
- Favorites manager (SharedPreferences stub)
- Sample audio file in `app/src/main/res/raw/sample.wav`
- Build files (Gradle) — open in Android Studio and build APK locally

## How to build APK (local)
1. Unzip the project and open the folder in **Android Studio** (File → Open).
2. Let Gradle sync and download dependencies.
3. Connect your Android device (enable USB debugging) or use an emulator.
4. To run on device: press the green **Run ▶** button and choose device.
5. To generate APK file: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
   The generated APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

## Notes & Next steps
- This project is a full-feature scaffold. For extra features (equalizer UI, waveform seekbar, lyrics, playlists saved in Room DB, album art caching with Glide), I can extend the code further.
- I cannot build the APK in this environment; follow the steps above to build locally or use GitHub Actions (I can add a workflow file if you want a CI-built APK).

## Troubleshooting
- If Run is disabled, ensure an emulator/device is connected and Gradle sync completed.
- If you get permission errors, grant storage/media permissions at runtime.
