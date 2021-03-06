package com.aman.acer.amdbhd.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aman.acer.amdbhd.AboutMovieActivity;
import com.aman.acer.amdbhd.Network.URLConstants;
import com.aman.acer.amdbhd.utils.IntentConstants;
import com.aman.acer.amdbhd.models.Movie;
import com.aman.acer.amdbhd.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by KeshavAggarwal on 30/03/17.
 */

public class MoviesSearchAdapter extends RecyclerView.Adapter<MoviesSearchAdapter.ViewHolder> {
    Context mContext;
    private ArrayList<Movie> mSearchedMovies;

    public MoviesSearchAdapter(ArrayList<Movie> searchedMovies, Context context) {
        mContext = context;
        mSearchedMovies = searchedMovies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_movie_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mSearchedMovies != null) {
            holder.name.setText(mSearchedMovies.get(position).getTitle());
            Picasso.with(mContext).load(URLConstants.IMAGE_BASE_URL + mSearchedMovies.get(position).getPosterPath()).into(holder.thumbnailImage);
            if (mSearchedMovies.get(position).getDate().length() >= 5) {
                String date = mSearchedMovies.get(position).getDate().substring(0, 4);
                holder.releaseDate.setText(date);
            }
            String rating = Double.toString(mSearchedMovies.get(position).getRating());
            holder.rating.setText(rating);
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, holder.thumbnailImage, holder.thumbnailImage.getTransitionName()).toBundle();
                    intent.setClass(mContext, AboutMovieActivity.class);
                    intent.putExtra(IntentConstants.INTENT_KEY_MOVIE_ID, mSearchedMovies.get(position).getId());
                    intent.putExtra(IntentConstants.INTENT_KEY_POSTER_PATH, mSearchedMovies.get(position).getPosterPath());
                    intent.putExtra(IntentConstants.INTENT_KEY_MOVIE_NAME, mSearchedMovies.get(position).getTitle());
                    mContext.startActivity(intent, bundle);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mSearchedMovies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView thumbnailImage;
        TextView name;
        TextView releaseDate;
        TextView rating;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImageView);
            name = (TextView) itemView.findViewById(R.id.nameTextView);
            releaseDate = (TextView) itemView.findViewById(R.id.releaseDateTextView);
            rating = (TextView) itemView.findViewById(R.id.ratingTextView);
        }
    }

}
