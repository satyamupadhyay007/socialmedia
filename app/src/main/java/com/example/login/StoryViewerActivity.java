package com.example.login;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class StoryViewerActivity extends AppCompatActivity {

    private ImageView storyImage;
    private VideoView storyVideo;

    private TextView storyUsername;

    private ImageButton btnClose;

    private ProgressBar progressBar;

    private ArrayList<String> mediaUrls;
    private ArrayList<String> mediaTypes;
    private ArrayList<String> usernames;
    private ArrayList<String> profileImages;

    private int currentIndex = 0;

    private final Handler handler =
            new Handler(
                    Looper.getMainLooper()
            );

    private final Runnable nextRunnable =
            () -> {

                if (currentIndex <
                        mediaUrls.size() - 1) {

                    currentIndex++;

                    showCurrentStory();

                } else {

                    finish();
                }
            };

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_story_viewer
        );

        storyImage =
                findViewById(
                        R.id.storyImage
                );

        storyVideo =
                findViewById(
                        R.id.storyVideo
                );

        storyUsername =
                findViewById(
                        R.id.storyUsername
                );

        btnClose =
                findViewById(
                        R.id.btnCloseStory
                );

        progressBar =
                findViewById(
                        R.id.storyProgress
                );

        mediaUrls =
                getIntent().getStringArrayListExtra(
                        "mediaUrls"
                );

        mediaTypes =
                getIntent().getStringArrayListExtra(
                        "mediaTypes"
                );

        usernames =
                getIntent().getStringArrayListExtra(
                        "usernames"
                );

        profileImages =
                getIntent().getStringArrayListExtra(
                        "profileImages"
                );

        if (mediaUrls == null ||
                mediaUrls.isEmpty()) {

            finish();

            return;
        }

        btnClose.setOnClickListener(
                v -> finish()
        );

        storyImage.setOnClickListener(
                v -> goNext()
        );

        storyVideo.setOnClickListener(
                v -> goNext()
        );

        showCurrentStory();
    }

    private void showCurrentStory() {

        handler.removeCallbacks(
                nextRunnable
        );

        storyImage.setVisibility(
                View.GONE
        );

        storyVideo.setVisibility(
                View.GONE
        );

        progressBar.setProgress(
                0
        );

        String url =
                mediaUrls.get(
                        currentIndex
                );

        String type =
                mediaTypes != null &&
                        currentIndex <
                                mediaTypes.size()
                        ? mediaTypes.get(
                        currentIndex
                )
                        : "image";

        String username =
                usernames != null &&
                        currentIndex <
                                usernames.size()
                        ? usernames.get(
                        currentIndex
                )
                        : "User";

        storyUsername.setText(
                username
        );

        if ("video".equalsIgnoreCase(type)) {

            showVideo(
                    url
            );

        } else {

            showImage(
                    url
            );
        }
    }

    private void showImage(
            String url
    ) {

        storyImage.setVisibility(
                View.VISIBLE
        );

        Glide.with(this)
                .load(url)
                .into(storyImage);

        progressBar.setMax(
                100
        );

        progressBar.setProgress(
                0
        );

        final long duration =
                5000;

        final long start =
                System.currentTimeMillis();

        Runnable progressRunnable =
                new Runnable() {

                    @Override
                    public void run() {

                        long elapsed =
                                System.currentTimeMillis()
                                        - start;

                        int progress =
                                (int)
                                        (
                                                Math.min(
                                                        100,
                                                        (
                                                                elapsed *
                                                                        100
                                                        ) /
                                                                duration
                                                )
                                        );

                        progressBar.setProgress(
                                progress
                        );

                        if (elapsed <
                                duration) {

                            handler.postDelayed(
                                    this,
                                    50
                            );

                        } else {

                            goNext();
                        }
                    }
                };

        handler.post(
                progressRunnable
        );
    }

    private void showVideo(
            String url
    ) {

        storyVideo.setVisibility(
                View.VISIBLE
        );

        storyVideo.setVideoURI(
                Uri.parse(
                        url
                )
        );

        storyVideo.setOnPreparedListener(
                mp -> {

                    mp.setLooping(
                            false
                    );

                    int duration =
                            mp.getDuration();

                    progressBar.setMax(
                            100
                    );

                    progressBar.setProgress(
                            0
                    );

                    storyVideo.start();

                    updateVideoProgress(
                            duration
                    );
                }
        );

        storyVideo.setOnCompletionListener(
                mp -> goNext()
        );

        storyVideo.setOnErrorListener(
                (mp, what, extra) -> {

                    goNext();

                    return true;
                }
        );

        storyVideo.start();
    }

    private void updateVideoProgress(
            int duration
    ) {

        handler.postDelayed(
                new Runnable() {

                    @Override
                    public void run() {

                        if (!storyVideo.isPlaying()) {
                            return;
                        }

                        int position =
                                storyVideo.getCurrentPosition();

                        if (duration > 0) {

                            int progress =
                                    (
                                            position *
                                                    100
                                    ) /
                                            duration;

                            progressBar.setProgress(
                                    progress
                            );
                        }

                        handler.postDelayed(
                                this,
                                100
                        );
                    }
                },
                100
        );
    }

    private void goNext() {

        handler.removeCallbacks(
                nextRunnable
        );

        if (currentIndex <
                mediaUrls.size() - 1) {

            currentIndex++;

            showCurrentStory();

        } else {

            finish();
        }
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(
                null
        );

        super.onDestroy();
    }
}