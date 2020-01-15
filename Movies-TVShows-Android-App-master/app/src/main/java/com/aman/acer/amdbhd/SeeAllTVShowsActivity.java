package com.aman.acer.amdbhd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.widget.Toast;

import com.aman.acer.amdbhd.adapters.RecyclerAdapterSeeAllTvshows;
import com.aman.acer.amdbhd.models.TVShow;
import com.aman.acer.amdbhd.Network.ApiService;
import com.aman.acer.amdbhd.Network.TVShowResponse;
import com.aman.acer.amdbhd.Network.URLConstants;
import com.aman.acer.amdbhd.utils.EndlessRecyclerViewScrollListener;
import com.aman.acer.amdbhd.utils.SpacesItemDecoration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeeAllTVShowsActivity extends AppCompatActivity implements RewardedVideoAdListener {
    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this,"Congratulations! you have umlock the movie trailer", Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {

    }

    private EndlessRecyclerViewScrollListener scrollListener;
    RecyclerView recyclerView;
    RecyclerAdapterSeeAllTvshows recyclerAdapterSeeAllTvshows;
    ArrayList<TVShow> tvShows;
    String tvShowType;

    InterstitialAd interstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_all_activity_tvshows);
        MobileAds.initialize(this,
                "ca-app-pub-3136435737091654~3899153078");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Slide slide = new Slide(Gravity.BOTTOM);
        getWindow().setEnterTransition(slide);
        getWindow().setAllowEnterTransitionOverlap(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        tvShows = (ArrayList<TVShow>) intent.getSerializableExtra("ABCD");
        tvShowType = intent.getStringExtra("TVSHOW_TYPE");

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }

        setTitle(tvShowType);

        recyclerView = (RecyclerView) findViewById(R.id.seeAllActivityRecyclerViewTVShows);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        recyclerAdapterSeeAllTvshows = new RecyclerAdapterSeeAllTvshows(tvShows, this);
        recyclerView.setAdapter(recyclerAdapterSeeAllTvshows);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                interstitialAd = new InterstitialAd(SeeAllTVShowsActivity.this);
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

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3136435737091654/4062804604",
                new AdRequest.Builder().build());
    }
    private void loadmoreData(int page) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLConstants.TVSHOW_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        if (tvShowType.equals("Airing Today")) {

            Call<TVShowResponse> call = service.getAiringToday(URLConstants.API_KEY, page);
            call.enqueue(new Callback<TVShowResponse>() {
                @Override
                public void onResponse(Call<TVShowResponse> call, Response<TVShowResponse> response) {
                    //Log.i("ABC2", "FUN");
                    ArrayList<TVShow> tvShowList = response.body().getTvShows();
                    if (tvShowList == null) {
                        return;
                    }
                    for (TVShow obj : tvShowList) {
                        tvShows.add(obj);
                    }
                    recyclerAdapterSeeAllTvshows.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<TVShowResponse> call, Throwable t) {

                }
            });
        } else if (tvShowType.equals("On Air")) {
            Call<TVShowResponse> call = service.getOnAir(URLConstants.API_KEY, page);

            call.enqueue(new Callback<TVShowResponse>() {
                @Override
                public void onResponse(Call<TVShowResponse> call, Response<TVShowResponse> response) {
                    ArrayList<TVShow> tvShowList = response.body().getTvShows();
                    if (tvShowList == null) {
                        return;
                    }
                    for (TVShow obj : tvShowList) {
                        tvShows.add(obj);
                    }
                    recyclerAdapterSeeAllTvshows.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<TVShowResponse> call, Throwable t) {

                }
            });
        } else if (tvShowType.equals("Popular Shows")) {
            Call<TVShowResponse> call = service.getPopular(URLConstants.API_KEY, page);

            call.enqueue(new Callback<TVShowResponse>() {
                @Override
                public void onResponse(Call<TVShowResponse> call, Response<TVShowResponse> response) {
                    //Log.i("ABC2", "FUN");
                    ArrayList<TVShow> tvShowList = response.body().getTvShows();
                    if (tvShowList == null) {
                        return;
                    }
                    for (TVShow obj : tvShowList) {
                        tvShows.add(obj);
                    }
                    recyclerAdapterSeeAllTvshows.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<TVShowResponse> call, Throwable t) {

                }
            });
        } else if (tvShowType.equals("Top Rated Shows")) {
            Call<TVShowResponse> call = service.getTopRated(URLConstants.API_KEY, page);

            call.enqueue(new Callback<TVShowResponse>() {
                @Override
                public void onResponse(Call<TVShowResponse> call, Response<TVShowResponse> response) {
                    //Log.i("ABC2", "FUN");
                    ArrayList<TVShow> tvShowList = response.body().getTvShows();
                    if (tvShowList == null) {
                        return;
                    }
                    for (TVShow obj : tvShowList) {
                        tvShows.add(obj);
                    }
                    recyclerAdapterSeeAllTvshows.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<TVShowResponse> call, Throwable t) {

                }
            });

        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}





