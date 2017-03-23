package com.example.mosaed.popular_movies_stage2.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.mosaed.popular_movies_stage2.data.MovieContract;

/**
 * Created by Mosaed on 06/03/17.
 */

public class MovieSyncUtils {

    private static boolean sInitialized;

    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized) return;

        sInitialized = true;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;

                String[] projection = {MovieContract.MovieEntry._ID};

                Cursor cursor = context.getContentResolver().query(
                        movieQueryUri,
                        projection,
                        null,
                        null,
                        null);

                WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if (cursor == null || cursor.getCount() == 0 || wifi.isWifiEnabled()) {
                    startImmediateSync(context);
                }

                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
        }.execute();
    }

    private static void startImmediateSync(final Context context) {
        Intent intentToSyncImmediate = new Intent(context, MovieSyncIntentService.class);
        context.startService(intentToSyncImmediate);
    }

}
