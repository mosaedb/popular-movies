package com.example.mosaed.popular_movies_stage2.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.mosaed.popular_movies_stage2.data.MovieContract.MovieEntry;
import com.example.mosaed.popular_movies_stage2.utilities.JsonUtils;
import com.example.mosaed.popular_movies_stage2.utilities.NetworkUtils;

import okhttp3.HttpUrl;

/**
 * Created by Mosaed on 06/03/17.
 */

class MovieSyncTask {

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";

    synchronized static void syncMovie(Context context) {

        try {
            HttpUrl popularMoviesRequestUrl = NetworkUtils.buildBasicUrl(POPULAR);
            HttpUrl topMoviesRequestUrl = NetworkUtils.buildBasicUrl(TOP_RATED);

            String jsonPopularMoviesResponse = NetworkUtils.getResponseFromHttpUrl(popularMoviesRequestUrl);
            String jsonTopMoviesResponse = NetworkUtils.getResponseFromHttpUrl(topMoviesRequestUrl);

            ContentValues[] popularMoviesValuesFromJson = JsonUtils.getMoviesContentValuesFromJson(jsonPopularMoviesResponse);
            ContentValues[] topMoviesValuesFromJson = JsonUtils.getMoviesContentValuesFromJson(jsonTopMoviesResponse);

            ContentResolver movieContentResolver = context.getContentResolver();

            if (popularMoviesValuesFromJson != null && popularMoviesValuesFromJson.length != 0) {
                String selection = MovieEntry.COLUMN_FAVORITE + "=?";
                String[] selectionArgs = {"0"};

                movieContentResolver.delete(
                        MovieEntry.CONTENT_URI,
                        selection,
                        selectionArgs);

                movieContentResolver.bulkInsert(
                        MovieEntry.CONTENT_URI,
                        popularMoviesValuesFromJson);
            }

            if (topMoviesValuesFromJson != null && topMoviesValuesFromJson.length != 0) {
                movieContentResolver.bulkInsert(
                        MovieEntry.CONTENT_URI,
                        topMoviesValuesFromJson);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
