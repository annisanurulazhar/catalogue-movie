package com.example.annisaazhar.cataloguemovie;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.DATE;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.DESCRIPTION;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.POSTER;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.TITLE;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.getColumnString;

public class FavAdapter extends CursorAdapter {

    public FavAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fav_movie, parent, false);
        return view;
    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null){
            TextView tvTitle = view.findViewById(R.id.tvTitle_2);
            TextView tvDate = view.findViewById(R.id.tvDate_2);
            TextView tvDescription = view.findViewById(R.id.tvDescription_2);
            ImageView ivPoster = view.findViewById(R.id.ivPoster_2);
            String image_url = BuildConfig.IMG_URL_BASE_PATH + getColumnString(cursor, POSTER);
            Picasso.get()
                    .load(image_url)
                    .resize(150, 150)
                    .centerInside()
                    .into(ivPoster);
            tvTitle.setText(getColumnString(cursor,TITLE));
            tvDescription.setText(getColumnString(cursor,DESCRIPTION));
            tvDate.setText(getColumnString(cursor,DATE));
        }
    }
}
