package com.example.mosaed.popular_movies_stage2.movie.details;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mosaed.popular_movies_stage2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mosaed on 24/02/17.
 */

class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> mItems;
    private Context mContext;

    private static final int VIEW_TYPE_MOVIE_ITEM = 0;
    private static final int VIEW_TYPE_MOVIE_TRAILER = 1;
    private static final int VIEW_TYPE_MOVIE_REVIEW = 2;

    final private ListItemClickListener mOnClickListener;

    interface ListItemClickListener {
        void onListItemClick(Object item);
    }

    MovieDetailAdapter(List<Object> items, ListItemClickListener listener) {
        this.mItems = items;
        mOnClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return this.mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof Movie) {
            return VIEW_TYPE_MOVIE_ITEM;
        } else if (mItems.get(position) instanceof Trailer) {
            return VIEW_TYPE_MOVIE_TRAILER;
        } else if (mItems.get(position) instanceof Review) {
            return VIEW_TYPE_MOVIE_REVIEW;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_MOVIE_ITEM:
                View viewMovieItem = inflater.inflate(R.layout.movie_item_viewholder, parent, false);
                viewHolder = new MovieItemViewHolder(viewMovieItem);
                break;
            case VIEW_TYPE_MOVIE_TRAILER:
                View viewMovieTrailer = inflater.inflate(R.layout.movie_trailer_viewholder, parent, false);
                viewHolder = new MovieTrailerViewHolder(viewMovieTrailer);
                break;
            case VIEW_TYPE_MOVIE_REVIEW:
                View viewMovieReview = inflater.inflate(R.layout.movie_review_viewholder, parent, false);
                viewHolder = new MovieReviewViewHolder(viewMovieReview);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MOVIE_ITEM:
                MovieItemViewHolder movieItemViewHolder = (MovieItemViewHolder) holder;
                configureMovieItemViewHolder(movieItemViewHolder, position);
                break;
            case VIEW_TYPE_MOVIE_TRAILER:
                MovieTrailerViewHolder trailerViewHolder = (MovieTrailerViewHolder) holder;
                configureMovieTrailerViewHolder(trailerViewHolder, position);
                break;
            case VIEW_TYPE_MOVIE_REVIEW:
                MovieReviewViewHolder reviewViewHolder = (MovieReviewViewHolder) holder;
                configureMovieReviewViewHolder(reviewViewHolder, position);
        }
    }

    private void configureMovieItemViewHolder(MovieItemViewHolder viewHolder, int position) {
        Movie movie = (Movie) mItems.get(position);
        Picasso.with(mContext)
                .load(movie.mMoviePosterUrl)
                .into(viewHolder.mMoviePosterView);
    }

    private void configureMovieTrailerViewHolder(MovieTrailerViewHolder viewHolder, int position) {
        Trailer trailer = (Trailer) mItems.get(position);
        if (trailer != null) {
            String thumbnail = "http://img.youtube.com/vi/" + trailer.mTrailerKey + "/mqdefault.jpg";
            Picasso.with(mContext)
                    .load(thumbnail)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(viewHolder.mMovieTrailerThumbnail);
        }
    }

    private void configureMovieReviewViewHolder(MovieReviewViewHolder viewHolder, int position) {
        Review review = (Review) mItems.get(position);
        if (review != null) {
            viewHolder.mReviewAuthor.setText(review.mReviewAuthor);
            viewHolder.mReviewContent.setText(review.mReviewContent);
        }
    }

    class MovieItemViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        @BindView(R.id.movie_poster)
        ImageView mMoviePosterView;

        MovieItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Movie currentMovie = (Movie) mItems.get(clickedPosition);
            mOnClickListener.onListItemClick(currentMovie);
        }
    }

    class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        @BindView(R.id.iv_trailer_thumbnail)
        ImageView mMovieTrailerThumbnail;

        MovieTrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Trailer currentTrailer = (Trailer) mItems.get(clickedPosition);
            mOnClickListener.onListItemClick(currentTrailer);
        }
    }

    class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_review_author)
        TextView mReviewAuthor;

        @BindView(R.id.tv_review_content)
        TextView mReviewContent;

        MovieReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
