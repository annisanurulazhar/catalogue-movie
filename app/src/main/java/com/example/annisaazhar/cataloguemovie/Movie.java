package com.example.annisaazhar.cataloguemovie;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.annisaazhar.cataloguemovie.provider.DatabaseContract;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.getColumnInt;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.getColumnString;

public class Movie implements Parcelable {

    private int _id;

    private boolean isFav;

    @SerializedName("id")
    private int movieId;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String description;

    @SerializedName("poster_path")
    private String posterUrl;

    @SerializedName("release_date")
    private String date;

    @SerializedName("genres")
    private List<Genre> genres = new ArrayList();

    @SerializedName("vote_average")
    private String rating;

    @SerializedName("runtime")
    private String duration;

    public Movie() {

    }

    public Movie(String title, String description, String posterUrl, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.posterUrl = posterUrl;
    }

    public Movie(Cursor cursor){
        this._id = getColumnInt(cursor, _ID);
        this.movieId = getColumnInt(cursor, DatabaseContract.FavColumns.UNIQUEID);
        this.title = getColumnString(cursor, DatabaseContract.FavColumns.TITLE);
        this.description = getColumnString(cursor, DatabaseContract.FavColumns.DESCRIPTION);
        this.date = getColumnString(cursor, DatabaseContract.FavColumns.DATE);
        this.posterUrl = getColumnString(cursor, DatabaseContract.FavColumns.POSTER);
    }

    protected Movie(Parcel in) {
        _id = in.readInt();
        movieId = in.readInt();
        title = in.readString();
        description = in.readString();
        posterUrl = in.readString();
        date = in.readString();
        rating = in.readString();
        duration = in.readString();
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

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.date);
        dest.writeString(this.posterUrl);
    }

    class Genre {
        private int id;
        private String name;

        public Genre(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
