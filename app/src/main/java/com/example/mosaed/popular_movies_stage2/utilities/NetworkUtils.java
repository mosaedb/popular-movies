package com.example.mosaed.popular_movies_stage2.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Mosaed on 13/02/16.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION = "3";
    private static final String PATH = "movie";
    private static final String QUERY_PARAMETER = "api_key";

    // TODO: Replacing the {api_key} with your own API key.
    private static final String apiKey = "";

    public static HttpUrl buildBasicUrl(String sortType) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(AUTHORITY)
                .addPathSegment(API_VERSION)
                .addPathSegment(PATH)
                .addPathSegment(sortType)
                .addQueryParameter(QUERY_PARAMETER, apiKey)
                .build();

        Log.v(TAG, "Built URI: " + url);
        return url;
    }

    public static HttpUrl buildRelatedUrl(String id, String path) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(AUTHORITY)
                .addPathSegment(API_VERSION)
                .addPathSegment(PATH)
                .addPathSegment(id)
                .addPathSegment(path)
                .addQueryParameter(QUERY_PARAMETER, apiKey)
                .build();

        Log.v(TAG, "Built Trailers or Reviews URI: " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(HttpUrl url) throws IOException{
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        try {
            return response.body().string();
        } finally {
            if (response != null) response.close();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
