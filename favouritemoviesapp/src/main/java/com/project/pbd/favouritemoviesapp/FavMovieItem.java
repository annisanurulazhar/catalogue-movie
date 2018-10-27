package com.project.pbd.favouritemoviesapp;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import static android.provider.BaseColumns._ID;
import static com.project.pbd.favouritemoviesapp.DatabaseContract.getColumnInt;
import static com.project.pbd.favouritemoviesapp.DatabaseContract.getColumnString;

public class FavMovieItem implements Parcelable {

    private int _id;

    private int movieId;

    private String title;

    private String description;

    private String posterUrl;

    private String date;

    public FavMovieItem () {

    }

    public FavMovieItem (Cursor cursor){
        this._id = getColumnInt(cursor, _ID);
        this.movieId = getColumnInt(cursor, DatabaseContract.FavColumns.UNIQUEID);
        this.title = getColumnString(cursor, DatabaseContract.FavColumns.TITLE);
        this.description = getColumnString(cursor, DatabaseContract.FavColumns.DESCRIPTION);
        this.date = getColumnString(cursor, DatabaseContract.FavColumns.DATE);
        this.posterUrl = getColumnString(cursor, DatabaseContract.FavColumns.POSTER);
    }
    protected FavMovieItem(Parcel in) {
        _id = in.readInt();
        movieId = in.readInt();
        title = in.readString();
        description = in.readString();
        posterUrl = in.readString();
        date = in.readString();
    }

    public static final Creator<FavMovieItem> CREATOR = new Creator<FavMovieItem>() {
        @Override
        public FavMovieItem createFromParcel(Parcel in) {
            return new FavMovieItem(in);
        }

        @Override
        public FavMovieItem[] newArray(int size) {
            return new FavMovieItem[size];
        }
    };

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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
}
