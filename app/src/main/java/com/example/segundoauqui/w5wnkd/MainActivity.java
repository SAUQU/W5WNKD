package com.example.segundoauqui.w5wnkd;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.flurry.android.FlurryAgent;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {


    private static final String FLURRY_API_KEY = "CSXP3MH6GX7RWM4ZMQ8V";
    SimpleExoPlayerView exoPlayer;
    private SimpleExoPlayer player;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;


    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();
    private ComponentListener componentListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        componentListener = new ComponentListener();
        exoPlayer = (SimpleExoPlayerView) findViewById(R.id.exoPlayer);
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, FLURRY_API_KEY);
        Fabric.with(this, new Answers(), new Crashlytics());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Tweet")
                .putContentType("Video")
                .putContentId("1234")
                .putCustomAttribute("Favorites Count", 20)
                .putCustomAttribute("Screen Orientation", "Landscape"));
    }



    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            player.setVideoListener(null);
            player.setVideoDebugListener(null);
            player.setAudioDebugListener(null);
            player.release();
            player = null;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        exoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void initializePlayer() {
        if (player == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory),
                    new DefaultLoadControl());
            player.addListener(componentListener);
            player.setVideoDebugListener(componentListener);
            player.setAudioDebugListener(componentListener);
            exoPlayer.setPlayer(player);

            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
            Uri uri = Uri.parse("https://www.youtube.com/watch?v=Amq-qlqbjYA");
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);

        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER);
        DashChunkSource.Factory dashChunkSourceFactory =
                new DefaultDashChunkSource.Factory(dataSourceFactory);

        return new DashMediaSource(uri, dataSourceFactory,
                dashChunkSourceFactory, null, null);
    }

    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.btnFlurry:
                Answers.getInstance().logCustom(new CustomEvent("Flurry test")
                        .putCustomAttribute("Category", "Comedy")
                        .putCustomAttribute("Length", 350));
                Map<String, String> eventParams = new HashMap<String, String>();
                eventParams.put("event", "click");
                eventParams.put("value", "Flurry test");
                FlurryAgent.logEvent("Button clicked for Flurry test", eventParams);
                Toast.makeText(this, "Flurry Event Tracked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCrash:
                throw new RuntimeException("This is a crash");
        }
    }


}
