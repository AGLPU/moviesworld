package com.aman.acer.amdbhd.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aman.acer.amdbhd.models.Movie;
import com.aman.acer.amdbhd.OnRecyclerViewitemClicklistener;
import com.aman.acer.amdbhd.R;
import com.aman.acer.amdbhd.Network.URLConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by KeshavAggarwal on 06/01/17.
 */

public class Recycler_View_Adapter extends RecyclerView.Adapter<Recycler_View_Adapter.ViewHolder> {
    private ArrayList<Movie> mMovies;
    Context mContext;
    private OnRecyclerViewitemClicklistener listener;
    private int verticalPosition;

    public Recycler_View_Adapter(ArrayList<Movie> movies, Context context) {
        mMovies = movies;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_cardview_movies, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mMovies != null) {
            holder.movieName.setText(mMovies.get(position).getTitle());
            Picasso.with(mContext).load(URLConstants.IMAGE_BASE_URL + mMovies.get(position).getPosterPath()).into(holder.movieThumbnailImage);
            if (mMovies.get(position).getDate().length() >= 5) {
                String date = mMovies.get(position).getDate().substring(0, 4);
                holder.movieReleaseDate.setText(date);
            }
            String rating = Double.toString(mMovies.get(position).getRating());
            holder.rating.setText(rating);
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onRecyclerViewItemClicked(verticalPosition, position, holder.movieThumbnailImage);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mMovies.size();
    }


    public void setOnItemClickListener(OnRecyclerViewitemClicklistener listener, int verticalPosition) {
        this.listener = listener;
        this.verticalPosition = verticalPosition;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView movieThumbnailImage;
        TextView movieName;
        TextView movieReleaseDate;
        TextView rating;

        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            movieThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImageView);
            movieName = (TextView) itemView.findViewById(R.id.nameTextView);
            movieReleaseDate = (TextView) itemView.findViewById(R.id.releaseDateTextView);
            rating = (TextView) itemView.findViewById(R.id.ratingTextView);

        }
    }

}
