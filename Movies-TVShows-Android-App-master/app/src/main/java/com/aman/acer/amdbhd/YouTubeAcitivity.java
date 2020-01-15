package com.aman.acer.amdbhd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.aman.acer.amdbhd.Network.URLConstants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YouTubeAcitivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    String key;

    InterstitialAd interstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_acitivity);

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youTubePlayer);
        youTubePlayerView.initialize(URLConstants.YOUTUBE_VIDEO_PLAYER_API_KEY, this);

        interstitialAd = new InterstitialAd(YouTubeAcitivity.this);
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
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms


                            interstitialAd.show();
                        }


                    }, 5000);
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        });
        Intent intent = getIntent();
        key = intent.getStringExtra("VIDEO_KEY");
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);

        if (!b) {
            youTubePlayer.cueVideo(key);
        }

    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
