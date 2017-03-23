package com.example.mosaed.popular_movies_stage2.movie.details;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mosaed.popular_movies_stage2.R;
import com.example.mosaed.popular_movies_stage2.data.MovieContract.MovieEntry;
import com.example.mosaed.popular_movies_stage2.utilities.JsonUtils;
import com.example.mosaed.popular_movies_stage2.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;

public class DetailActivity extends AppCompatActivity implements
        MovieDetailAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<List<Object>> {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final int TRAILER_LOADER_ID = 22;
    private static final int REVIEW_LOADER_ID = 33;

    String mMovieId;
    int mMovieFavoriteValue;

    private Uri mCurrentMovieUri;

    LayoutManager mTrailerLayoutManager;
    LayoutManager mReviewLayoutManager;

    MovieDetailAdapter mTrailerAdapter;
    MovieDetailAdapter mReviewAdapter;

    @BindView(R.id.rv_movie_trailer)
    RecyclerView mTrailerRecyclerView;

    @BindView(R.id.rv_movie_review)
    RecyclerView mReviewRecyclerView;

    @BindView(R.id.iv_movie_poster_detail)
    ImageView mMoviePosterImageView;

    @BindView(R.id.tv_movie_title)
    TextView mMovieTitleTextView;

    @BindView(R.id.tv_movie_release_date)
    TextView mMovieReleaseDateTextView;

    @BindView(R.id.tv_movie_vote_average)
    TextView mMovieVoteAverageTextView;

    @BindView(R.id.tv_movie_overview)
    TextView mMovieOverviewTextView;

    @BindView(R.id.tv_review_title)
    TextView mReviewTitleTextView;

    @BindView(R.id.tv_trailer_title)
    TextView mTrailerTitleTextView;

    @BindView(R.id.pb_loading_indicator_detail)
    ProgressBar mLoadingIndicatorDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Intent intentFromMainActivity = getIntent();
        if (intentFromMainActivity != null) {
            if (intentFromMainActivity.hasExtra(getString(R.string.key_intent_movie))) {
                Movie currentMovie = intentFromMainActivity.getParcelableExtra(getString(R.string.key_intent_movie));
                configureMovieDetail(currentMovie);
            }

            if (NetworkUtils.isOnline(this)) {
                loadMovieExtraDetail(TRAILER_LOADER_ID);
                loadMovieExtraDetail(REVIEW_LOADER_ID);
            }
        }

        mCurrentMovieUri = MovieEntry.buildMovieUriWithId(mMovieId);

        mTrailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecyclerView.setLayoutManager(mTrailerLayoutManager);
        mTrailerRecyclerView.setHasFixedSize(true);

        mReviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(mReviewLayoutManager);
        mReviewRecyclerView.setNestedScrollingEnabled(false);
        mReviewRecyclerView.setHasFixedSize(true);

        invalidateOptionsMenu();
    }

    @Override
    public void onListItemClick(Object item) {
        Trailer trailer = (Trailer) item;
        Intent intentTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.mTrailerUrl));
        startActivity(intentTrailer);
    }

    private void configureMovieDetail(Movie currentMovie) {
        mMovieId = currentMovie.mMovieId;
        mMovieFavoriteValue = currentMovie.mMovieFavorite;

        Picasso.with(this)
                .load(currentMovie.mMoviePosterUrl)
                .into(mMoviePosterImageView);
        mMovieTitleTextView.setText(currentMovie.mMovieTitle);
        mMovieReleaseDateTextView.setText(currentMovie.mMovieReleaseDate);
        mMovieVoteAverageTextView.setText(currentMovie.mMovieVoteAverage);
        mMovieOverviewTextView.setText(currentMovie.mMovieOverview);
    }

    private void loadMovieExtraDetail(int loaderId) {
        getSupportLoaderManager().initLoader(loaderId, null, this);
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int loaderId, Bundle args) {

        AsyncTaskLoader<List<Object>> loader = null;

        switch (loaderId) {
            case TRAILER_LOADER_ID:
                loader = new AsyncTaskLoader<List<Object>>(this) {

                    List<Object> mObjectList = null;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        if (mObjectList != null) {
                            deliverResult(mObjectList);
                        } else {
                            mLoadingIndicatorDetail.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public List<Object> loadInBackground() {
                        HttpUrl trailersRequestUrl = NetworkUtils.buildRelatedUrl(mMovieId, getString(R.string.path_trailers));
                        try {
                            String jsonTrailersResponse = NetworkUtils.getResponseFromHttpUrl(trailersRequestUrl);
                            return JsonUtils.getTrailersInfoFromJson(jsonTrailersResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public void deliverResult(List<Object> data) {
                        mObjectList = data;
                        super.deliverResult(data);
                    }
                };
                break;

            case REVIEW_LOADER_ID:
                loader = new AsyncTaskLoader<List<Object>>(this) {

                    List<Object> mObjectList = null;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        if (mObjectList != null) {
                            deliverResult(mObjectList);
                        } else {
                            mLoadingIndicatorDetail.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public List<Object> loadInBackground() {
                        HttpUrl reviewsRequestUrl = NetworkUtils.buildRelatedUrl(mMovieId, getString(R.string.path_reviews));
                        try {
                            String jsonReviewsResponse = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
                            return JsonUtils.getReviewsInfoFromJson(jsonReviewsResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public void deliverResult(List<Object> data) {
                        mObjectList = data;
                        super.deliverResult(data);
                    }
                };
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        mLoadingIndicatorDetail.setVisibility(View.INVISIBLE);
        switch (loader.getId()) {
            case TRAILER_LOADER_ID:
                if (data != null && data.size() > 0) {
                    showTrailerTitle();
                    updateTrailerUi(data);
                } else {
                    hideTrailerTitle();
                }
                break;

            case REVIEW_LOADER_ID:
                if (data != null && data.size() > 0) {
                    showReviewTitle();
                    updateReviewUi(data);
                } else {
                    hideReviewTitle();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        /**
         * We required to Override this method to implement
         * the LoaderCallbacks<List<Object>> interface
         */
    }

    private void updateTrailerUi(List<Object> trailers) {
        mTrailerAdapter = new MovieDetailAdapter(trailers, this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
    }

    private void updateReviewUi(List<Object> reviews) {
        mReviewAdapter = new MovieDetailAdapter(reviews, this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
    }

    private void showReviewTitle() {
        mReviewTitleTextView.setVisibility(View.VISIBLE);
    }

    private void hideReviewTitle() {
        mReviewTitleTextView.setVisibility(View.GONE);
    }

    private void showTrailerTitle() {
        mTrailerTitleTextView.setVisibility(View.VISIBLE);
    }

    private void hideTrailerTitle() {
        mTrailerTitleTextView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_favorite:
                item.setIcon(R.drawable.ic_star);
                favorite();
                invalidateOptionsMenu();
                return true;
            case R.id.action_unfavorite:
                item.setIcon(R.drawable.ic_empty_star);
                unFavorite();
                invalidateOptionsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem;
        if (isFavorite()) {
            menuItem = menu.findItem(R.id.action_favorite);
            menuItem.setVisible(false);
        } else {
            menuItem = menu.findItem(R.id.action_unfavorite);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void favorite() {
        ContentValues updateValues = new ContentValues();
        updateValues.put(MovieEntry.COLUMN_FAVORITE, 1);

        String selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {mMovieId};

        int rowUpdated = getContentResolver().update(
                mCurrentMovieUri,
                updateValues,
                selection,
                selectionArgs);

        if (rowUpdated > 0) {
            Toast.makeText(this, getString(R.string.msg_favorite_movie), Toast.LENGTH_LONG).show();
        }
    }

    private void unFavorite() {
        ContentValues mUpdateValues = new ContentValues();
        mUpdateValues.put(MovieEntry.COLUMN_FAVORITE, 0);

        String mSelection = MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] mSelectionArgs = {mMovieId};

        int rowUpdated = getContentResolver().update(
                mCurrentMovieUri,
                mUpdateValues,
                mSelection,
                mSelectionArgs);

        if (rowUpdated > 0) {
            Toast.makeText(this, getString(R.string.msg_unfavorite_movie), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isFavorite() {
        String[] projection = new String[]{MovieEntry.COLUMN_FAVORITE};
        String selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = {mMovieId};
        Cursor cursor = getContentResolver().query(
                MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            mMovieFavoriteValue = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE));
            cursor.close();
        }

        return mMovieFavoriteValue > 0;
    }
}
