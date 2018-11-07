package com.example.annisaazhar.cataloguemovie;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.annisaazhar.cataloguemovie.MainActivity.KEY_PAGETYPE;
import static com.example.annisaazhar.cataloguemovie.MainActivity.TAG_NOW_PLAYING;
import static com.example.annisaazhar.cataloguemovie.MainActivity.TAG_UPCOMING;
import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.CONTENT_URI;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {

    private RecyclerView recyclerView;
    private View ChildView;
    private ArrayList<Movie> movieListNowPlaying = new ArrayList<>();
    private ArrayList<Movie> movieListUpcoming = new ArrayList<>();
    private String pageType;
    MoviesAdapter moviesAdapter;

    private static final String TAG = MovieListFragment.class.getSimpleName();
    private static Retrofit retrofit;
    private int recyclerViewItemPosition;
    public static final String MOVIE_ID = "movie_id";

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageType = getArguments().getString(KEY_PAGETYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        recyclerView = view.findViewById(R.id.rvContent);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRetrofit();

        moviesAdapter = new MoviesAdapter(getContext());
        recyclerView.setAdapter(moviesAdapter);
        if (pageType.equals(TAG_NOW_PLAYING)) {
            if (savedInstanceState == null) {
                fetchNowPlayingMovies();
            } else {
                movieListNowPlaying = savedInstanceState.getParcelableArrayList("movies_np");
                moviesAdapter.setMovieList(movieListNowPlaying);
                setOnItemClickListener(movieListNowPlaying);
            }

        } else if (pageType.equals(TAG_UPCOMING)) {
            if (savedInstanceState == null) {
                fetchUpcomingMovies();
            } else {
                movieListUpcoming = savedInstanceState.getParcelableArrayList("movies_upcoming");
                moviesAdapter.setMovieList(movieListUpcoming);
                setOnItemClickListener(movieListUpcoming);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("movies_np", movieListNowPlaying);
        outState.putParcelableArrayList("movies_upcoming", movieListUpcoming);
        super.onSaveInstanceState(outState);
    }

    private void setOnItemClickListener(final List<Movie> movieList) {
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                ChildView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (ChildView != null && gestureDetector.onTouchEvent(e)) {

                    recyclerViewItemPosition = recyclerView.getChildAdapterPosition(ChildView);

                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                    int movieID = movieList.get(recyclerViewItemPosition).getMovieId();
                    int id = movieList.get(recyclerViewItemPosition).get_id();
                    Uri uri = Uri.parse(CONTENT_URI+"/"+id);
                    intent.setData(uri);
                    intent.putExtra(MOVIE_ID, movieID);
                    startActivityForResult(intent, DetailsActivity.REQUEST_CODE);
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DetailsActivity.REQUEST_CODE) {
            if (resultCode == DetailsActivity.RESULT_ADD) {
                Toast.makeText(getContext(), "Berhasil menambahkan ke favorit", Toast.LENGTH_LONG).show();
            } else if (resultCode == DetailsActivity.RESULT_DELETE) {
                Toast.makeText(getContext(), "Berhasil menghapus dari favorit", Toast.LENGTH_LONG).show();
            }
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


    private void fetchNowPlayingMovies() {
        MovieApiService movieApiService = retrofit.create(MovieApiService.class);
        Call<MovieResponse> call = movieApiService.getNowPlayingMovies(BuildConfig.API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                onRetrieveMovies(response, movieListNowPlaying);
                Log.d("size", Integer.toString(movieListNowPlaying.size()));
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void fetchUpcomingMovies() {
        MovieApiService movieApiService = retrofit.create(MovieApiService.class);
        Call<MovieResponse> call = movieApiService.getUpcomingMovies(BuildConfig.API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                onRetrieveMovies(response, movieListUpcoming);
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void onRetrieveMovies(Response<MovieResponse> movieResponse, List<Movie> movieList) {
        movieList.clear();
        if (movieResponse.body() != null) {
            movieList.clear();
            movieList.addAll(movieResponse.body().getResults());
        }
        moviesAdapter.setMovieList(movieList);
        setOnItemClickListener(movieList);
        Log.d(TAG, "Number of movies received: " + movieList.size());
    }
}
