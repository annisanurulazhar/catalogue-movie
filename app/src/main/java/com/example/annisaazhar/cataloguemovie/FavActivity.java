package com.example.annisaazhar.cataloguemovie;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.CONTENT_URI;

public class FavActivity extends AppCompatActivity {

    private ListView lvContent;
    private FavAdapter favAdapter;
    private Cursor listFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Favourite");
        setSupportActionBar(toolbar);

        lvContent = findViewById(R.id.lvContent);
        favAdapter = new FavAdapter(this, null, false);
        lvContent.setAdapter(favAdapter);

        new LoadFavAsync().execute();
    }

    private class LoadFavAsync extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected Cursor doInBackground(Void... voids) {
            return getContentResolver().query(CONTENT_URI, null, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            listFav = cursor;
            favAdapter.swapCursor(listFav);

            if (listFav.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "Belum ada film favorit", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
