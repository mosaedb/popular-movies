package com.example.mosaed.popular_movies_stage2.movie.main;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mosaed.popular_movies_stage2.movie.details.Movie;
import com.example.mosaed.popular_movies_stage2.R;
import com.example.mosaed.popular_movies_stage2.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mosaed on 13/02/16.
 */

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private View mEmptyView;

    final private ListItemClickListener mOnClickListener;

    interface ListItemClickListener {
        void onListItemClick(Movie movie);
    }

    MovieAdapter(@NonNull Context context, ListItemClickListener listener, View emptyView) {
        mContext = context;
        mOnClickListener = listener;
        mEmptyView = emptyView;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.movie_item_viewholder, parent, false);
        itemView.setFocusable(true);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        Picasso.with(mContext)
                .load(mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_POSTER_URL)))
                .into(holder.mMoviePosterView);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        @BindView(R.id.movie_poster)
        ImageView mMoviePosterView;

        MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mCursor.moveToPosition(clickedPosition);

            String movieID = mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
            String movieTitle = mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
            String movieReleaseDate = mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE));;
            String moviePosterUrl = mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_POSTER_URL));;
            String movieVoteAverage = mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE));
            String movieOverview = mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));;
            int movieFavorite = mCursor.getInt(mCursor.getColumnIndex(MovieEntry.COLUMN_FAVORITE));

            Movie currentMovie = new Movie(movieID, movieTitle, movieReleaseDate, moviePosterUrl,
                    movieVoteAverage, movieOverview, movieFavorite);

            mOnClickListener.onListItemClick(currentMovie);
        }
    }
}