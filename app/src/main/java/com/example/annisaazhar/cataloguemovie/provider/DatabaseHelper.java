package com.example.annisaazhar.cataloguemovie.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.DATE;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.DESCRIPTION;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.POSTER;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.TITLE;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.UNIQUEID;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.TABLE_FAV;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "dbmovie";

    private static final int DATABASE_VERSION = 1;

    public static String CREATE_TABLE_FAV = "create table "+TABLE_FAV+
            " ("+_ID+" integer primary key autoincrement, " +
            UNIQUEID+" text not null, "+
            TITLE+" text not null, " +
            DESCRIPTION+" text not null, "+
            DATE+" text not null, "+
            POSTER+" text not null);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAV);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FAV);
        onCreate(db);
    }
}
