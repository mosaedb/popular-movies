package com.example.mosaed.popular_movies_stage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mosaed.popular_movies_stage2.data.MovieContract.MovieEntry;

/**
 * Created by Mosaed on 26/02/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_Movie_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID      + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_POSTER_URL    + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TITLE         + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE  + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE  + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY    + " REAL NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW      + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE      + " INTEGER NOT NULL, " +
                "UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE_Movie_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
