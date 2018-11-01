package com.example.annisaazhar.cataloguemovie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.CONTENT_URI;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private List<Bitmap> mWidgetItems = new ArrayList<>();
    private Context mContext;
    private int mAppWidgetId;
    private Cursor cursor;


    public StackRemoteViewsFactory(Context mContext) {
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
       retrieveFavMovie();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }
        long identityToken = Binder.clearCallingIdentity();
        retrieveFavMovie();
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (cursor == null) {
            return;
        }
        cursor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void retrieveFavMovie() {
        cursor = mContext.getContentResolver().query(CONTENT_URI, null, null, null, null, null);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Movie favs = getMovieAt(position);
        String image_url = BuildConfig.IMG_URL_BASE_PATH + favs.getPosterUrl();
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        try {
            Bitmap bitmapMoviePoster = Glide.with(mContext).
                    load(image_url).
                    asBitmap().
                    error(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryDark)))
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();

            rv.setImageViewBitmap(R.id.imageView, bitmapMoviePoster);
        } catch (InterruptedException | ExecutionException e){
            Log.d("Glide: ", e.getMessage());
        }

        Bundle extras = new Bundle();
        extras.putInt(MovieWidget.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return cursor.moveToPosition(position) ? cursor.getLong(0) : position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private Movie getMovieAt(int position) {
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("IllegalStateException");
        }
        return new Movie(cursor);
    }
}
