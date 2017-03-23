package com.example.mosaed.popular_movies_stage2.utilities;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.example.mosaed.popular_movies_stage2.movie.details.Review;
import com.example.mosaed.popular_movies_stage2.movie.details.Trailer;
import com.example.mosaed.popular_movies_stage2.data.MovieContract.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mosaed on 13/02/16.
 */

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w342";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    private static final String JSON_DATA_ARRAY = "results";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_POSTER_PATH = "poster_path";
    private static final String MOVIE_VOTE_AVERAGE = "vote_average";
    private static final String MOVIE_POPULARITY = "popularity";
    private static final String MOVIE_OVERVIEW = "overview";

    private static final String TRAILERS_KEY = "key";
    private static final String TRAILERS_NAME = "name";

    private static final String REVIEWS_AUTHOR = "author";
    private static final String REVIEWS_CONTENT = "content";

    public static ContentValues[] getMoviesContentValuesFromJson(String moviesJsonString){
        if (TextUtils.isEmpty(moviesJsonString)) {
            return null;
        }

        ContentValues[] moviesContentValues = null;

        try {
            JSONObject moviesJsonResponse = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJsonResponse.getJSONArray(JSON_DATA_ARRAY);
            moviesContentValues = new ContentValues[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                int movieID;
                String movieTitle;
                String movieReleaseDate;
                String posterPath;
                String moviePosterUrl;
                double movieVoteAverage;
                double moviePopularity;
                String movieOverview;

                JSONObject movieObject = moviesArray.getJSONObject(i);
                movieID = movieObject.getInt(MOVIE_ID);
                movieTitle = movieObject.getString(MOVIE_TITLE);
                movieReleaseDate = movieObject.getString(MOVIE_RELEASE_DATE);
                posterPath = movieObject.getString(MOVIE_POSTER_PATH);
                moviePosterUrl = BASE_POSTER_URL + POSTER_SIZE + posterPath;
                movieVoteAverage = movieObject.getDouble(MOVIE_VOTE_AVERAGE);
                moviePopularity = movieObject.getDouble(MOVIE_POPULARITY);
                movieOverview = movieObject.getString(MOVIE_OVERVIEW);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, movieID);
                movieValues.put(MovieEntry.COLUMN_TITLE, movieTitle);
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
                movieValues.put(MovieEntry.COLUMN_POSTER_URL, moviePosterUrl);
                movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, movieVoteAverage);
                movieValues.put(MovieEntry.COLUMN_POPULARITY, moviePopularity);
                movieValues.put(MovieEntry.COLUMN_OVERVIEW, movieOverview);
                movieValues.put(MovieEntry.COLUMN_FAVORITE, 0);

                moviesContentValues[i] = movieValues;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing movie JSON results");
        }

        return moviesContentValues;
    }

    public static ArrayList<Object> getTrailersInfoFromJson(String trailersJsonString) {

        if (TextUtils.isEmpty(trailersJsonString)) {
            return null;
        }

        ArrayList<Object> trailersList = new ArrayList<>();

        try {
            JSONObject trailersJsonResponse = new JSONObject(trailersJsonString);

            JSONArray trailersArray = trailersJsonResponse.getJSONArray(JSON_DATA_ARRAY);

            for (int i = 0; i < trailersArray.length(); i++) {
                String trailerKey;
                String trailerUrl;

                JSONObject trailerObject = trailersArray.getJSONObject(i);
                trailerKey = trailerObject.getString(TRAILERS_KEY);
                trailerUrl = BASE_YOUTUBE_URL + trailerKey;

                Trailer trailer = new Trailer(trailerKey, trailerUrl);
                trailersList.add(trailer);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Problem parsing trailer JSON results");
        }

        return trailersList;
    }

    public static ArrayList<Object> getReviewsInfoFromJson(String reviewsJsonString) {

        if (TextUtils.isEmpty(reviewsJsonString)) {
            return null;
        }

        ArrayList<Object> reviewsList = new ArrayList<>();

        try {
            JSONObject reviewsJsonResponse = new JSONObject(reviewsJsonString);

            JSONArray reviewsArray = reviewsJsonResponse.getJSONArray(JSON_DATA_ARRAY);

            for (int i = 0; i < reviewsArray.length(); i++) {
                String reviewAuthor;
                String reviewContent;

                JSONObject trailerObject = reviewsArray.getJSONObject(i);
                reviewAuthor = trailerObject.getString(REVIEWS_AUTHOR);
                reviewContent = trailerObject.getString(REVIEWS_CONTENT);

                Review review = new Review(reviewAuthor, reviewContent);
                reviewsList.add(review);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Problem parsing review JSON results");
        }

        return reviewsList;
    }
}
