package com.example.mosaed.popular_movies_stage2.movie.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mosaed.popular_movies_stage2.movie.details.DetailActivity;
import com.example.mosaed.popular_movies_stage2.movie.details.Movie;
import com.example.mosaed.popular_movies_stage2.R;
import com.example.mosaed.popular_movies_stage2.data.MovieContract.MovieEntry;
import com.example.mosaed.popular_movies_stage2.sync.MovieSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITE = "favorite";

    private static final int MOST_POPULAR_MOVIES_ID = 1;
    private static final int TOP_RATED_MOVIES_ID = 2;
    private static final int FAVORITE_MOVIES_ID = 3;

    public final static String KEY_PREF_SORT_MOVIE = "pref_sort_movie";

    private String mSortType;

    LayoutManager mLayoutManager;
    MovieAdapter mMovieAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    @BindView(R.id.rv_movie)
    RecyclerView mMovieRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mLoadingIndicator.setVisibility(View.VISIBLE);

        MovieSyncUtils.initialize(this);

        mLayoutManager = new GridLayoutManager(this, 2);
        mMovieRecyclerView.setLayoutManager(mLayoutManager);
        mMovieRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);
        mMovieRecyclerView.setAdapter(mMovieAdapter);

        setupSharedPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_most_popular:
                mSortType = getResources().getString(R.string.sort_type_popular);
                editSharedPreferences();
                return true;
            case R.id.action_top_rated:
                mSortType = getResources().getString(R.string.sort_type_top_rated);
                editSharedPreferences();
                return true;
            case R.id.action_my_favorite:
                mSortType = getResources().getString(R.string.sort_type_favorite);
                editSharedPreferences();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMoviesData(String sortType) {
        switch (sortType) {
            case POPULAR:
                initMoviesLoader(MOST_POPULAR_MOVIES_ID);
                break;
            case TOP_RATED:
                initMoviesLoader(TOP_RATED_MOVIES_ID);
                break;
            case FAVORITE:
                initMoviesLoader(FAVORITE_MOVIES_ID);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMoviesData(mSortType);
    }

    private void initMoviesLoader(int loaderId) {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieLoader = loaderManager.getLoader(loaderId);
        if (movieLoader == null) {
            Log.d(TAG, "onLoadFinished() MovieLoader is null, call initLoader");
            loaderManager.initLoader(loaderId, null, this).forceLoad();
        } else {
            Log.d(TAG, "onLoadFinished() MovieLoader call restartLoader");
            loaderManager.restartLoader(loaderId, null, this).forceLoad();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        Uri movieQueryUri = MovieEntry.CONTENT_URI;
        String sortOrder;

        switch (loaderId) {
            case MOST_POPULAR_MOVIES_ID:
                sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC LIMIT 20"; // For popular movies
                return new CursorLoader(this,
                        movieQueryUri,
                        null,
                        null,
                        null,
                        sortOrder);

            case TOP_RATED_MOVIES_ID:
                sortOrder = MovieEntry.COLUMN_VOTE_AVERAGE + " DESC LIMIT 20"; // For top movies
                return new CursorLoader(this,
                        movieQueryUri,
                        null,
                        null,
                        null,
                        sortOrder);

            case FAVORITE_MOVIES_ID:
                String selection = MovieEntry.COLUMN_FAVORITE + ">?";
                String[] selectionArgs = {"0"};
                return new CursorLoader(this,
                        movieQueryUri,
                        null,
                        selection,
                        selectionArgs,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mMovieRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) {
            showMovieDataView();
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(Movie currentMovie) {
        Intent intentDetailMovie = new Intent(MainActivity.this, DetailActivity.class);
        intentDetailMovie.putExtra(getString(R.string.key_intent_movie), currentMovie);
        if (intentDetailMovie.resolveActivity(getPackageManager()) != null) {
            startActivity(intentDetailMovie);
        }
    }

    private void showMovieDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMovieRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSortType = sharedPreferences.getString(KEY_PREF_SORT_MOVIE, getResources().getString(R.string.sort_type_top_rated));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void editSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PREF_SORT_MOVIE, mSortType);
        editor.apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_SORT_MOVIE)) {
            mSortType = sharedPreferences.getString(KEY_PREF_SORT_MOVIE, getResources().getString(R.string.sort_type_top_rated));
            loadMoviesData(mSortType);
        }
    }
}