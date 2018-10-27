package com.example.annisaazhar.cataloguemovie;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.annisaazhar.cataloguemovie.provider.DatabaseContract.CONTENT_URI;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final String KEY_PAGETYPE = "pageType";
    public static final String TAG_UPCOMING = "upcoming";
    public static final String TAG_NOW_PLAYING = "nowPlaying";
    private String searchKeyword = "";
    private RecyclerView rvContentSearch;
    private View ChildView;
    private MoviesAdapter moviesAdapter;
    private List<Movie> movieList = new ArrayList<>();

    private static final String TAG = MainActivity.class.getSimpleName();
    private static Retrofit retrofit;
    private int recyclerViewItemPosition;
    public static final String MOVIE_ID = "movie_id";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

    private Cursor listFav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        setupDrawer();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        rvContentSearch = findViewById(R.id.rvContentSearch);
        setupRecyclerView();

        new LoadFavAsync().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                rvContentSearch.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                searchKeyword = query.trim();
                if (!searchKeyword.isEmpty()) {
                    searchMovieByKeyword(searchKeyword);
                } else {
                    Toast.makeText(getApplicationContext(), "Masukkan keyword", Toast.LENGTH_LONG).show();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    if (moviesAdapter!= null) {
                        moviesAdapter.clearAdapter();
                    }
                    rvContentSearch.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (moviesAdapter != null) {
                    moviesAdapter.clearAdapter();
                }
                rvContentSearch.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setQueryHint(getString(R.string.film_search_hint));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toggle != null) {
            toggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (toggle != null) {
            toggle.onConfigurationChanged(newConfig);
        }
    }

    private void setupFragment(MovieListFragment movieListFragment, String type) {
        if (type.equals(TAG_NOW_PLAYING)) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_PAGETYPE, TAG_NOW_PLAYING);
            movieListFragment.setArguments(bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_PAGETYPE, TAG_UPCOMING);
            movieListFragment.setArguments(bundle);
        }

    }

    private void setupDrawer() {
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.text_nav_open, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getString(R.string.text_nav_open));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        MovieListFragment nowPlayingFragment = new MovieListFragment();
        MovieListFragment upcomingFragment = new MovieListFragment();

        setupFragment(nowPlayingFragment, TAG_NOW_PLAYING);
        setupFragment(upcomingFragment, TAG_UPCOMING);

        adapter.addFragment(nowPlayingFragment, getString(R.string.movie_title_now_playing));
        adapter.addFragment(upcomingFragment, getString(R.string.movie_title_upcoming));

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
        }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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

    private void searchMovieByKeyword(final String query) {
        setupRetrofit();
        MovieApiService movieApiService = retrofit.create(MovieApiService.class);
        Call<MovieResponse> call = movieApiService.searchMovie(BuildConfig.API_KEY, query, 1);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.body() != null) {
                    if (response.body().getResults().size() > 0) {
                        movieList.clear();
                        movieList = response.body().getResults();
                        moviesAdapter = new MoviesAdapter(getApplicationContext());
                        rvContentSearch.setAdapter(moviesAdapter);
                        moviesAdapter.setMovieList(movieList);
                        setOnItemClickListener();
                        Log.d(TAG, "Number of movies received: " + movieList.size());
                    } else {
                        moviesAdapter.clearAdapter();
                        Toast.makeText(getApplicationContext(), "Pencarian film dengan keyword '" + query + "' tidak ditemukan", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString());
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupRecyclerView() {
        rvContentSearch.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvContentSearch.addItemDecoration(itemDecor);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setOnItemClickListener() {

        rvContentSearch.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                ChildView = rvContentSearch.findChildViewUnder(e.getX(), e.getY());

                if (ChildView != null && gestureDetector.onTouchEvent(e)) {

                    recyclerViewItemPosition = rvContentSearch.getChildAdapterPosition(ChildView);

                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    int movieID = movieList.get(recyclerViewItemPosition).getMovieId();
                    int id = movieList.get(recyclerViewItemPosition).get_id();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DetailsActivity.REQUEST_CODE) {
            if (resultCode == DetailsActivity.RESULT_ADD) {
                new LoadFavAsync().execute();
                Toast.makeText(getApplicationContext(), "Berhasil menambahkan ke favorit", Toast.LENGTH_LONG).show();
            } else if (resultCode == DetailsActivity.RESULT_DELETE) {
                new LoadFavAsync().execute();
                Toast.makeText(getApplicationContext(), "Berhasil menghapus dari favorit", Toast.LENGTH_LONG).show();
            }
        }
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

            if (listFav.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "Belum ada film favorit", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
