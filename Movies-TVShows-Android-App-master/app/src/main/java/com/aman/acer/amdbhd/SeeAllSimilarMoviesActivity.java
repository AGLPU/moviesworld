package com.aman.acer.amdbhd;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.aman.acer.amdbhd.adapters.RecyclerViewAdpterSeeAllActivity;
import com.aman.acer.amdbhd.models.Movie;
import com.aman.acer.amdbhd.Network.ApiService;
import com.aman.acer.amdbhd.Network.MovieResponse;
import com.aman.acer.amdbhd.Network.URLConstants;
import com.aman.acer.amdbhd.utils.EndlessRecyclerViewScrollListener;
import com.aman.acer.amdbhd.utils.IntentConstants;
import com.aman.acer.amdbhd.utils.SpacesItemDecoration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeeAllSimilarMoviesActivity extends AppCompatActivity {
    private EndlessRecyclerViewScrollListener scrollListener;
    RecyclerView recyclerView;
    ProgressBar progressBarSeeAllActivity;
    RecyclerViewAdpterSeeAllActivity recyclerViewAdpterSeeAllActivity;
    ArrayList<Movie> movies;
    int movie_id;
    String movieName;

    InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_all_activity_movie);

        progressBarSeeAllActivity = (ProgressBar) findViewById(R.id.progressBarSeeAllActivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Slide slide = new Slide(Gravity.BOTTOM);
        //getWindow().setEnterTransition(slide);
        //getWindow().setAllowEnterTransitionOverlap(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        progressBarSeeAllActivity.setVisibility(View.VISIBLE);
        //movies = (ArrayList<Movie>) intent.getSerializableExtra("ABCD");
        movie_id = intent.getIntExtra(IntentConstants.INTENT_KEY_MOVIE_ID,0);
        movieName = intent.getStringExtra("MOVIE_NAME");
        setTitle("Similar to " + movieName);
        movies = new ArrayList<>();


        recyclerView = (RecyclerView) findViewById(R.id.seeAllActivityRecyclerViewMovies);


        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        recyclerViewAdpterSeeAllActivity = new RecyclerViewAdpterSeeAllActivity(movies, this);
        recyclerView.setAdapter(recyclerViewAdpterSeeAllActivity);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        loadmoreData(1);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                interstitialAd = new InterstitialAd(SeeAllSimilarMoviesActivity.this);
                interstitialAd.setAdUnitId("ca-app-pub-3136435737091654/4062804604");

                // Create ad request.
                AdRequest adRequests = new AdRequest.Builder().build();

                interstitialAd.loadAd(adRequests);

                // Set the AdListener.
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // Log.d(LOG_TAG, "onAdLoaded");
                        final Handler handler = new Handler();
                        if (interstitialAd.isLoaded()) {


                                    interstitialAd.show();
                                }

                    }
                    @Override
                    public void onAdFailedToLoad(int errorCode) {

                    }
                });
                loadmoreData(page);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
    }

    private void loadmoreData(int page) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLConstants.MOVIE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<MovieResponse> call = service.getSimilarMovies(movie_id, URLConstants.API_KEY, page);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                progressBarSeeAllActivity.setVisibility(View.GONE);
                ArrayList<Movie> movieList = response.body().getMovies();
                if (movieList == null) {
                    return;
                }
                for (Movie obj : movieList) {
                    movies.add(obj);
                }
                recyclerViewAdpterSeeAllActivity.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                progressBarSeeAllActivity.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}