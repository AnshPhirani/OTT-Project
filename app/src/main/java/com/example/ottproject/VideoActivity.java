package com.example.ottproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VideoActivity extends AppCompatActivity {

    private ObjectMapper mapper;
    private ExoPlayer exoPlayer;
    private Runnable runnable;
    private Handler handler2;

    private String videoUrl;
    private String videoId;
    private List<Advertisement> advertisements;
    private boolean killThread;

    private StyledPlayerView playerView;
    private FrameLayout frameLayout;
    private ImageView addImageView;
    private TextView addTitleView;
    private LinearLayout addView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // getting data from intent
        videoUrl = getIntent().getExtras().getString("videoUrl");
        videoId = getIntent().getExtras().getString("videoId");

        killThread = false;

        playerView = findViewById(R.id.exoPlayer);
        frameLayout = findViewById(R.id.framelayout);
        addImageView = findViewById(R.id.addImage);
        addTitleView = findViewById(R.id.addText);
        addView = findViewById(R.id.addView);

        // setting up the videoplayer
        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();

        ImageView fullscreenButton = findViewById(R.id.fullscreen);
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onClick(View view) {

                int orientation = VideoActivity.this.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


                } else {
                    Toast.makeText(VideoActivity.this, "Land", Toast.LENGTH_SHORT).show();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }


            }
        });

        // getting adds data from json
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    advertisements = mapper.readValue(new URL("https://storage.googleapis.com/publicmaxtap-prod.appspot.com/data/ads_data_assignment.json"), new TypeReference<List<Advertisement>>() {
                    });                                 // read from url
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("ansh", "run: " + advertisements);
                        }
                    });
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                    Log.d("ansh", "run: " + e.getMessage());
                }
            }
        }).start();


        final int[] idx = {0};
        // displaying adds using a new thread
        handler2 = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("ansh", "runnable: " + (exoPlayer.getCurrentPosition() / 1000));

                long curTimeInSecond = exoPlayer.getCurrentPosition() / 1000;
                while (advertisements != null && idx[0] < advertisements.size() && !advertisements.get(idx[0]).getVideoId().equals(videoId)) {
                    idx[0]++;
                }
                if (advertisements != null && idx[0] < advertisements.size()) {
                    long addStartTime = advertisements.get(idx[0]).getAdStartTime();
                    long addEndTime = advertisements.get(idx[0]).getAdEndTime();
                    String imageUrl = advertisements.get(idx[0]).getAdImage();
                    String productUrl = advertisements.get(idx[0]).getOnClick();

                    if (curTimeInSecond >= addStartTime && curTimeInSecond <= addEndTime) {
                        Glide.with(VideoActivity.this).load(imageUrl).centerCrop().placeholder(R.drawable.ic_baseline_broken_image_24).into(addImageView);
                        addView.setVisibility(View.VISIBLE);
                        addView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(productUrl));
                                startActivity(i);
                            }
                        });

                    }
                    if (curTimeInSecond >= addEndTime) {
                        addView.setVisibility(View.INVISIBLE);
                        idx[0]++;
                    }
                }

                if (!killThread)
                    handler2.postDelayed(this, 1000);

            }

        };
        handler2.post(runnable);

        exoPlayer.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onPlaybackStateChanged(EventTime eventTime, int state) {
                AnalyticsListener.super.onPlaybackStateChanged(eventTime, state);
                idx[0] = 0;
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        killThread = false;
        handler2.post(runnable);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            exoPlayer.stop();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        killThread = true;

    }


}