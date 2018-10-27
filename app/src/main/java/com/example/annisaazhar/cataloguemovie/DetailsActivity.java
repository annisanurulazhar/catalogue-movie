package com.example.annisaazhar.cataloguemovie;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.annisaazhar.cataloguemovie.provider.DatabaseContract;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.annisaazhar.cataloguemovie.MovieListFragment.MOVIE_ID;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.CONTENT_URI;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.DATE;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.DESCRIPTION;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.POSTER;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.TITLE;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.FavColumns.UNIQUEID;

public class DetailsActivity extends AppCompatActivity {
    private static Retrofit retrofit;
    public static int REQUEST_CODE = 100;
    public static int RESULT_ADD = 101;
    public static int RESULT_DELETE = 102;

    TextView tvTitleDetail, tvDuration, tvGenre, tvDateReleased, tvRating, tvOverview;
    ImageView ivPosterDetail;
    CheckBox btnFavourite;
    Movie movie;

    public UIUtils uiUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.text_movie_details);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        uiUtils = new UIUtils(getApplicationContext());
        int movieId = getIntent().getIntExtra(MOVIE_ID, 0);
        btnFavourite = findViewById(R.id.btnFav);
        final String[] args = {Integer.toString(movieId)};
        final String selection = DatabaseContract.FavColumns.UNIQUEID + " == ? ";
        final Cursor cursor = getContentResolver().query(CONTENT_URI, null, selection, args, null);
        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(UNIQUEID, movie.getMovieId());
                values.put(TITLE, movie.getTitle());
                values.put(DESCRIPTION, movie.getDescription());
                values.put(DATE, movie.getDate());
                values.put(POSTER, movie.getPosterUrl());
                if (cursor.getCount() == 0) {
                    getContentResolver().insert(CONTENT_URI, values);
                    btnFavourite.setChecked(true);
                    setResult(RESULT_ADD);
                    finish();
                } else {
                    Uri uri = Uri.parse(CONTENT_URI + "/" + Integer.toString(movie.get_id()));
                    getContentResolver().delete(uri, DatabaseContract.FavColumns.UNIQUEID, args);
                    btnFavourite.setChecked(false);
                    setResult(RESULT_DELETE);
                    finish();
                }
            }
        });

        if (cursor.getCount() != 0) {
            btnFavourite.setChecked(true);
            cursor.close();
        }

        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        tvDuration = findViewById(R.id.tvDuration);
        tvGenre = findViewById(R.id.tvGenre);
        tvDateReleased = findViewById(R.id.tvDateReleased);
        tvRating = findViewById(R.id.tvRating);
        tvOverview = findViewById(R.id.tvOverview);
        ivPosterDetail = findViewById(R.id.ivPosterDetail);
        setupRetrofit();
        getMovieDetails(movieId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    private void getMovieDetails(int movieId) {
        MovieApiService movieApiService = retrofit.create(MovieApiService.class);
        Call<Movie> call = movieApiService.getMovieDetails(movieId, BuildConfig.API_KEY);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                if (response.isSuccessful()) {
                    movie = response.body();
                    if (movie != null) {
                        renderMovieDetails(movie);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {

            }
        });
    }

    private void renderMovieDetails(Movie movie) {
        String image_url = BuildConfig.IMG_URL_BASE_PATH + movie.getPosterUrl();
        Picasso.get()
                .load(image_url)
                .resize(300, 300)
                .centerInside()
                .into(ivPosterDetail);
        tvTitleDetail.setText(movie.getTitle());
        tvDuration.setText(getString(R.string.text_minutes, movie.getDuration()));
        tvDateReleased.setText(uiUtils.formatDate(movie.getDate()));
        tvOverview.setText(movie.getDescription());
        List<Movie.Genre> genres = movie.getGenres();
        tvGenre.setText(uiUtils.formatGenre(genres));
        tvRating.setText(String.format("%s/10", movie.getRating()));
    }
}
