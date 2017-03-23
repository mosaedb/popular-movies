package com.example.mosaed.popular_movies_stage2.movie.details;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mosaed on 13/02/16.
 */

public class Movie implements Parcelable {

    String mMovieId;
    String mMovieTitle;
    String mMovieReleaseDate;
    String mMoviePosterUrl;
    String mMovieVoteAverage;
    String mMovieOverview;
    int mMovieFavorite;

    public Movie(String movieId, String movieTitle, String movieReleaseDate, String moviePosterUrl,
                 String movieVoteAverage, String movieOverview, int movieFavorite) {
        mMovieId = movieId;
        mMovieTitle = movieTitle;
        mMovieReleaseDate = movieReleaseDate;
        mMoviePosterUrl = moviePosterUrl;
        mMovieVoteAverage = movieVoteAverage;
        mMovieOverview = movieOverview;
        mMovieFavorite = movieFavorite;
    }

    private Movie(Parcel in) {
        mMovieId = in.readString();
        mMovieTitle = in.readString();
        mMovieReleaseDate = in.readString();
        mMoviePosterUrl = in.readString();
        mMovieVoteAverage = in.readString();
        mMovieOverview = in.readString();
        mMovieFavorite = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mMovieId);
        parcel.writeString(mMovieTitle);
        parcel.writeString(mMovieReleaseDate);
        parcel.writeString(mMoviePosterUrl);
        parcel.writeString(mMovieVoteAverage);
        parcel.writeString(mMovieOverview);
        parcel.writeInt(mMovieFavorite);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
