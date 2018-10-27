package com.project.pbd.favouritemoviesapp;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.project.pbd.favouritemoviesapp.DatabaseContract.CONTENT_URI;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    ListView lvFavMovie;
    FavMovieAdapter favMovieAdapter;
    private final int LOAD_FAVES_ID = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvFavMovie = findViewById(R.id.lvContent);

        favMovieAdapter = new FavMovieAdapter(this, null, true);
        lvFavMovie.setAdapter(favMovieAdapter);
        lvFavMovie.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(LOAD_FAVES_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOAD_FAVES_ID, null, this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(LOAD_FAVES_ID);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        favMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        favMovieAdapter.swapCursor(null);
    }
}
