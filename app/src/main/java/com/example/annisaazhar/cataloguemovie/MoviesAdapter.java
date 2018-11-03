package com.example.annisaazhar.cataloguemovie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private List<Movie> movieList;
    private LayoutInflater inflater;

    public MoviesAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.movieList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_movie, parent, false);
        MoviesAdapter.MovieViewHolder movieViewHolder = new MoviesAdapter.MovieViewHolder(view);
        movieViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        String image_url = BuildConfig.IMG_URL_BASE_PATH + movie.getPosterUrl();
        Picasso.get()
                .load(image_url)
                .resize(120, 120)
                .centerInside()
                .into(holder.ivPoster);
        holder.tvTitle.setText(movie.getTitle());
        holder.tvDescription.setText(movie.getDescription());
        holder.tvDate.setText(movie.getDate());

    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList.clear();
        this.movieList.addAll(movieList);

        notifyDataSetChanged();
    }

    public void clearAdapter() {
        this.movieList.clear();

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (movieList == null) {
            return 0;

        } else {
            return movieList.size();
        }
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView ivPoster;
        public TextView tvTitle, tvDescription, tvDate;
        public MovieViewHolder(View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
