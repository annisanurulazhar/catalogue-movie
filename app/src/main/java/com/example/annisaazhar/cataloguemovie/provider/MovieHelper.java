package com.example.annisaazhar.cataloguemovie.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.annisaazhar.cataloguemovie.Movie;

import java.sql.SQLException;
import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.DATE;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.DESCRIPTION;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.POSTER;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.TITLE;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.UNIQUEID;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.TABLE_FAV;

public class MovieHelper {
    private static String DATABASE_TABLE = TABLE_FAV;
    private Context context;
    private DatabaseHelper dataBaseHelper;

    private SQLiteDatabase database;

    public MovieHelper(Context context){
        this.context = context;
    }

    public MovieHelper open() throws SQLException {
        dataBaseHelper = new DatabaseHelper(context);
        database = dataBaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dataBaseHelper.close();
    }

    public ArrayList<Movie> query(){
        ArrayList<Movie> arrayList = new ArrayList<Movie>();
        Cursor cursor = database.query(DATABASE_TABLE
                ,null
                ,null
                ,null
                ,null
                ,null,_ID +" ASC"
                ,null);
        cursor.moveToFirst();
        Movie movie;
        if (cursor.getCount()>0) {
            do {

                movie = new Movie();
                movie.set_id(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                movie.setMovieId(cursor.getInt(cursor.getColumnIndexOrThrow(UNIQUEID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                movie.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                movie.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                movie.setPosterUrl(cursor.getString(cursor.getColumnIndexOrThrow(POSTER)));

                arrayList.add(movie);
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public long insert(Movie movie){
        ContentValues initialValues =  new ContentValues();
        initialValues.put(UNIQUEID, movie.getMovieId());
        initialValues.put(TITLE, movie.getTitle());
        initialValues.put(DESCRIPTION, movie.getDescription());
        initialValues.put(DATE, movie.getDate());
        initialValues.put(POSTER, movie.getPosterUrl());
        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    public int update(Movie movie){
        ContentValues args = new ContentValues();
        args.put(UNIQUEID, movie.getMovieId());
        args.put(TITLE, movie.getTitle());
        args.put(DESCRIPTION, movie.getDescription());
        args.put(DATE, movie.getDate());
        args.put(POSTER, movie.getPosterUrl());
        return database.update(DATABASE_TABLE, args, _ID + "= '" + movie.get_id() + "'", null);
    }

    public int delete(int id){
        return database.delete(TABLE_FAV, _ID + " = '"+id+"'", null);
    }

    public Cursor queryByIdProvider(String id){
        return database.query(DATABASE_TABLE,null
                ,_ID + " = ?"
                ,new String[]{id}
                ,null
                ,null
                ,null
                ,null);
    }
    public Cursor queryProvider(String selection,
                                String[] selectionArgs){
        return database.query(DATABASE_TABLE
                ,null
                ,selection
                ,selectionArgs
                ,null
                ,null
                ,_ID + " DESC");
    }
    public long insertProvider(ContentValues values){
        return database.insert(DATABASE_TABLE,null,values);
    }
    public int updateProvider(String id,ContentValues values){
        return database.update(DATABASE_TABLE,values,_ID +" = ?",new String[]{id} );
    }
    public int deleteProvider(String id){
        return database.delete(DATABASE_TABLE,UNIQUEID + " = ?", new String[]{id});
    }
}
