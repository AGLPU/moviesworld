package com.aman.acer.amdbhd;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.aman.acer.amdbhd.adapters.MoviesSearchAdapter;
import com.aman.acer.amdbhd.models.Movie;
import com.aman.acer.amdbhd.Network.ApiService;
import com.aman.acer.amdbhd.Network.MovieResponse;
import com.aman.acer.amdbhd.Network.URLConstants;
import com.aman.acer.amdbhd.utils.EndlessRecyclerViewScrollListener;
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

public class SearchMovieActivity extends AppCompatActivity {
    private EndlessRecyclerViewScrollListener scrollListener;
    android.widget.SearchView searchView;
    ImageButton searchBack;
    ProgressBar progress;
    MoviesSearchAdapter recyclerAdapterSearchMovies;
    RecyclerView searchResults;
    ArrayList<Movie> mainSearchedmovies;

    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.widget.SearchView) findViewById(R.id.search_view);

        searchView.setQueryHint("Search Movies");
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        searchResults = (RecyclerView) findViewById(R.id.search_results);
        searchBack = (ImageButton) findViewById(R.id.searchback);

        mainSearchedmovies = new ArrayList<>();

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        searchResults.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        recyclerAdapterSearchMovies = new MoviesSearchAdapter(mainSearchedmovies, this);
        searchResults.setAdapter(recyclerAdapterSearchMovies);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        searchResults.setLayoutManager(gridLayoutManager);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                searchFor(query, 1);
                scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        interstitialAd = new InterstitialAd(SearchMovieActivity.this);
                        interstitialAd.setAdUnitId("ca-app-pub-3136435737091654/4062804604");

                        // Create ad request.
                        AdRequest adRequests = new AdRequest.Builder().build();

                        interstitialAd.loadAd(adRequests);

                        // Set the AdListener.
                        interstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // Log.d(LOG_TAG, "onAdLoaded");

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
                        searchFor(query, page);

                    }
                };
                searchResults.addOnScrollListener(scrollListener);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResults.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
                mainSearchedmovies.clear();
                return true;
            }
        });
    }

    private void searchFor(String query, int page) {
        if (page == 1)
            progress.setVisibility(View.VISIBLE);
        searchResults.setVisibility(View.VISIBLE);
        searchView.clearFocus();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLConstants.SEARCH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<MovieResponse> call = service.getSearchedMovies(URLConstants.API_KEY, query, page);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                progress.setVisibility(View.GONE);
                ArrayList<Movie> searchMovieList = response.body().getMovies();
                if (searchMovieList == null) {
                    return;
                }
                for (Movie obj : searchMovieList) {
                    mainSearchedmovies.add(obj);
                }

                recyclerAdapterSearchMovies.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {

            }
        });

        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                /*Intent intent = new Intent();
                intent.setClass(SearchMovieActivity.this, MainActivity.class);
                startActivity(intent);*/
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(query)) {
                searchView.setQuery(query, false);
                searchFor(query, 1);
            }

        }
    }

}
