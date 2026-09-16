package com.example.login;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);
        ImageButton btnCloseVideo = findViewById(R.id.btnCloseVideo);

        hideSystemBars();

        String videoUrl =
                getIntent().getStringExtra("videoUrl");

        if (videoUrl == null ||
                videoUrl.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Video unavailable",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // ==========================================
        // CLOSE
        // ==========================================

        btnCloseVideo.setOnClickListener(
                v -> finish()
        );

        // ==========================================
        // VIDEO
        // ==========================================

        Uri videoUri =
                Uri.parse(videoUrl);

        videoView.setVideoURI(videoUri);

        // Built-in Android controls
        MediaController mediaController =
                new MediaController(this);

        mediaController.setAnchorView(
                videoView
        );

        videoView.setMediaController(
                mediaController
        );

        // ==========================================
        // PREPARED
        // ==========================================

        videoView.setOnPreparedListener(
                mediaPlayer -> {

                    mediaPlayer.setLooping(false);

                    videoView.start();
                }
        );

        // ==========================================
        // COMPLETED
        // ==========================================

        videoView.setOnCompletionListener(
                mediaPlayer -> {

                    // Video complete hone ke baad
                    // wahi screen par rukega.
                    // User replay kar sakta hai.
                }
        );

        // ==========================================
        // ERROR
        // ==========================================

        videoView.setOnErrorListener(
                (mediaPlayer, what, extra) -> {

                    Toast.makeText(
                            this,
                            "Unable to play this video",
                            Toast.LENGTH_SHORT
                    ).show();

                    return true;
                }
        );
    }

    // ==========================================
    // FULL SCREEN
    // ==========================================

    private void hideSystemBars() {

        Window window =
                getWindow();

        if (android.os.Build.VERSION.SDK_INT >= 30) {

            WindowInsetsController controller =
                    window.getInsetsController();

            if (controller != null) {

                controller.hide(
                        WindowInsets.Type.statusBars() |
                                WindowInsets.Type.navigationBars()
                );

                controller.setSystemBarsBehavior(
                        WindowInsetsController
                                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                );
            }

        } else {

            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        hideSystemBars();

        if (videoView != null &&
                videoView.getDuration() > 0 &&
                !videoView.isPlaying()) {

            videoView.start();
        }
    }
}