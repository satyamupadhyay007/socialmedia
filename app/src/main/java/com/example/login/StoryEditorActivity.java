package com.example.login;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoryEditorActivity extends AppCompatActivity {

    private FrameLayout editorRoot;
    private FrameLayout editorCanvas;

    private ImageView storyPreviewImage;
    private VideoView storyPreviewVideo;

    // Visible draggable text overlay
    private EditText storyCaption;

    // Bottom/utility UI
    private TextView selectedSongText;
    private TextView btnText;
    private TextView btnMusic;
    private TextView btnDoneText;

    private LinearLayout textToolbar;
    private LinearLayout bottomPanel;
    private Button btnShareStory;
    private SeekBar textSizeSeek;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String mediaUrl = "";
    private String mediaType = "image";
    private Uri localMediaUri;

    private Uri selectedSongUri;
    private String selectedSongName = "";
    private MediaPlayer musicPlayer;

    private static final String UPLOAD_PRESET = "frenzo_media";

    // ------------------------------------------------------------
    // PHOTO TRANSFORM
    // ------------------------------------------------------------

    private final Matrix imageMatrix = new Matrix();
    private float imageScale = 1f;
    private float minImageScale = 1f;
    private float maxImageScale = 4f;

    private float imageLastX;
    private float imageLastY;
    private boolean imageDragging;

    private float lastFocusX;
    private float lastFocusY;
    private float lastPinchDistance;
    private boolean pinchMode;

    // ------------------------------------------------------------
    // TEXT DRAG
    // ------------------------------------------------------------

    private float textLastX;
    private float textLastY;
    private boolean textMoved;

    // ------------------------------------------------------------
    // TEXT STYLE
    // ------------------------------------------------------------

    private float textSizeSp = 30f;
    private int textStyle = 0; // 0 classic, 1 bold, 2 modern
    private int textColor = Color.WHITE;

    // ------------------------------------------------------------
    // MUSIC PICKER
    // ------------------------------------------------------------

    private final ActivityResultLauncher<String> songPicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri == null) return;

                        selectedSongUri = uri;

                        String name = uri.getLastPathSegment();
                        if (name == null || name.trim().isEmpty()) {
                            name = "Selected music";
                        }

                        // Remove common encoded path noise
                        name = name.replace("%20", " ");
                        selectedSongName = name;

                        selectedSongText.setText(
                                "♫  " + selectedSongName
                        );
                        selectedSongText.setVisibility(View.VISIBLE);

                        prepareMusicPreview(uri);
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_story_editor);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        bindViews();
        applySystemBarInsets();
        readIntent();
        showPreview();
        setupClicks();
        setupTextDrag();
        setupPhotoGestures();
    }

    private void bindViews() {
        editorRoot = findViewById(R.id.editorRoot);
        editorCanvas = findViewById(R.id.editorCanvas);

        storyPreviewImage = findViewById(R.id.storyPreviewImage);
        storyPreviewVideo = findViewById(R.id.storyPreviewVideo);

        storyCaption = findViewById(R.id.storyCaption);

        selectedSongText = findViewById(R.id.selectedSongText);

        btnText = findViewById(R.id.btnStoryText);
        btnMusic = findViewById(R.id.btnStoryMusic);
        btnDoneText = findViewById(R.id.btnDoneText);

        textToolbar = findViewById(R.id.textToolbar);
        textSizeSeek = findViewById(R.id.textSizeSeek);

        bottomPanel = findViewById(R.id.bottomPanel);
        btnShareStory = findViewById(R.id.btnShareStory);
    }

    // ------------------------------------------------------------
    // ANDROID 14/15/16 EDGE-TO-EDGE SAFE AREAS
    // ------------------------------------------------------------

    private void applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
                editorRoot,
                (v, insets) -> {

                    Insets bars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    View topBar = findViewById(R.id.storyTopBar);
                    if (topBar != null) {
                        topBar.setPadding(
                                dp(10),
                                bars.top + dp(8),
                                dp(10),
                                dp(8)
                        );
                    }

                    if (bottomPanel != null) {
                        bottomPanel.setPadding(
                                dp(18),
                                dp(8),
                                dp(18),
                                bars.bottom + dp(10)
                        );
                    }

                    return insets;
                }
        );
    }

    // ------------------------------------------------------------
    // INTENT
    // ------------------------------------------------------------

    private void readIntent() {

        mediaUrl = getIntent().getStringExtra("mediaUrl");
        mediaType = getIntent().getStringExtra("mediaType");

        String localUriString =
                getIntent().getStringExtra("localMediaUri");

        String initialCaption =
                getIntent().getStringExtra("initialCaption");

        if (mediaUrl == null) {
            mediaUrl = "";
        }

        if (mediaType == null || mediaType.isEmpty()) {
            mediaType = "image";
        }

        if (localUriString != null &&
                !localUriString.isEmpty()) {

            localMediaUri = Uri.parse(localUriString);
        }

        if (initialCaption != null &&
                !initialCaption.trim().isEmpty()) {

            storyCaption.setText(initialCaption);
            storyCaption.setVisibility(View.VISIBLE);
            applyTextStyle();
        } else {
            storyCaption.setVisibility(View.GONE);
        }
    }

    // ------------------------------------------------------------
    // PREVIEW
    // ------------------------------------------------------------

    private void showPreview() {

        if (localMediaUri != null) {

            if ("video".equalsIgnoreCase(mediaType)) {

                storyPreviewImage.setVisibility(View.GONE);
                storyPreviewVideo.setVisibility(View.VISIBLE);

                storyPreviewVideo.setVideoURI(localMediaUri);

                storyPreviewVideo.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    storyPreviewVideo.start();
                });

            } else {

                storyPreviewVideo.setVisibility(View.GONE);
                storyPreviewImage.setVisibility(View.VISIBLE);

                Glide.with(this)
                        .load(localMediaUri)
                        .into(new CustomTarget<Drawable>() {

                            @Override
                            public void onResourceReady(
                                    Drawable resource,
                                    Transition<? super Drawable> transition) {

                                storyPreviewImage.setImageDrawable(resource);
                                setupImageMatrix();
                            }

                            @Override
                            public void onLoadCleared(
                                    Drawable placeholder) {
                            }
                        });
            }

            return;
        }

        storyPreviewVideo.setVisibility(View.GONE);
        storyPreviewImage.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(mediaUrl)
                .into(new CustomTarget<Drawable>() {

                    @Override
                    public void onResourceReady(
                            Drawable resource,
                            Transition<? super Drawable> transition) {

                        storyPreviewImage.setImageDrawable(resource);
                        setupImageMatrix();
                    }

                    @Override
                    public void onLoadCleared(
                            Drawable placeholder) {
                    }
                });
    }

    // ------------------------------------------------------------
    // PHOTO: PINCH + DRAG
    // ------------------------------------------------------------

    private void setupImageMatrix() {

        if (storyPreviewImage.getDrawable() == null) {
            return;
        }

        storyPreviewImage.post(() -> {

            float viewW = storyPreviewImage.getWidth();
            float viewH = storyPreviewImage.getHeight();

            Drawable drawable = storyPreviewImage.getDrawable();

            float drawableW = drawable.getIntrinsicWidth();
            float drawableH = drawable.getIntrinsicHeight();

            if (drawableW <= 0 || drawableH <= 0 ||
                    viewW <= 0 || viewH <= 0) {
                return;
            }

            float scale = Math.max(
                    viewW / drawableW,
                    viewH / drawableH
            );

            minImageScale = scale;
            imageScale = scale;

            imageMatrix.reset();

            float dx = (viewW - drawableW * scale) / 2f;
            float dy = (viewH - drawableH * scale) / 2f;

            imageMatrix.postScale(scale, scale);
            imageMatrix.postTranslate(dx, dy);

            storyPreviewImage.setScaleType(
                    ImageView.ScaleType.MATRIX
            );
            storyPreviewImage.setImageMatrix(imageMatrix);
        });
    }

    private void setupPhotoGestures() {

        storyPreviewImage.setOnTouchListener(
                (v, event) -> {

                    if (event.getPointerCount() >= 2) {

                        pinchMode = true;
                        imageDragging = false;

                        float distance =
                                distance(event);

                        if (lastPinchDistance > 0) {

                            float factor =
                                    distance /
                                            lastPinchDistance;

                            zoomImage(
                                    factor,
                                    event.getX(0),
                                    event.getY(0),
                                    event.getX(1),
                                    event.getY(1)
                            );
                        }

                        lastPinchDistance = distance;

                        return true;
                    }

                    switch (event.getActionMasked()) {

                        case MotionEvent.ACTION_DOWN:

                            pinchMode = false;
                            imageDragging = true;

                            imageLastX = event.getX();
                            imageLastY = event.getY();

                            return true;

                        case MotionEvent.ACTION_MOVE:

                            if (!pinchMode &&
                                    imageDragging &&
                                    event.getPointerCount() == 1) {

                                float dx =
                                        event.getX() - imageLastX;

                                float dy =
                                        event.getY() - imageLastY;

                                imageMatrix.postTranslate(
                                        dx,
                                        dy
                                );

                                storyPreviewImage.setImageMatrix(
                                        imageMatrix
                                );

                                imageLastX = event.getX();
                                imageLastY = event.getY();
                            }

                            return true;

                        case MotionEvent.ACTION_POINTER_UP:

                            lastPinchDistance = 0;
                            pinchMode = false;
                            imageDragging = false;
                            return true;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:

                            lastPinchDistance = 0;
                            pinchMode = false;
                            imageDragging = false;
                            return true;
                    }

                    return true;
                }
        );
    }

    private void zoomImage(
            float factor,
            float x1,
            float y1,
            float x2,
            float y2
    ) {

        if (factor <= 0) return;

        float newScale =
                imageScale * factor;

        newScale = Math.max(
                minImageScale,
                Math.min(
                        maxImageScale,
                        newScale
                )
        );

        float actualFactor =
                newScale / imageScale;

        float focusX =
                (x1 + x2) / 2f;

        float focusY =
                (y1 + y2) / 2f;

        imageMatrix.postScale(
                actualFactor,
                actualFactor,
                focusX,
                focusY
        );

        imageScale = newScale;

        storyPreviewImage.setImageMatrix(
                imageMatrix
        );
    }

    private float distance(MotionEvent e) {

        if (e.getPointerCount() < 2) {
            return 0;
        }

        float dx =
                e.getX(0) - e.getX(1);

        float dy =
                e.getY(0) - e.getY(1);

        return (float) Math.sqrt(
                dx * dx + dy * dy
        );
    }

    // ------------------------------------------------------------
    // BUTTONS
    // ------------------------------------------------------------

    private void setupClicks() {

        ImageButton back =
                findViewById(R.id.btnBackStoryEditor);

        back.setOnClickListener(
                v -> finish()
        );

        btnText.setOnClickListener(
                v -> openTextEditor()
        );

        btnMusic.setOnClickListener(
                v -> openMusicSheet()
        );

        btnDoneText.setOnClickListener(
                v -> finishTextEditing()
        );

        btnShareStory.setOnClickListener(
                v -> publishStory()
        );

        textSizeSeek.setMax(40);
        textSizeSeek.setProgress(18);

        textSizeSeek.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        textSizeSp =
                                16f + progress;

                        storyCaption.setTextSize(
                                textSizeSp
                        );
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {
                    }
                }
        );

        findViewById(R.id.btnStyleClassic)
                .setOnClickListener(v -> {
                    textStyle = 0;
                    applyTextStyle();
                });

        findViewById(R.id.btnStyleBold)
                .setOnClickListener(v -> {
                    textStyle = 1;
                    applyTextStyle();
                });

        findViewById(R.id.btnStyleModern)
                .setOnClickListener(v -> {
                    textStyle = 2;
                    applyTextStyle();
                });

        findViewById(R.id.btnTextWhite)
                .setOnClickListener(v -> {
                    textColor = Color.WHITE;
                    applyTextStyle();
                });

        findViewById(R.id.btnTextYellow)
                .setOnClickListener(v -> {
                    textColor = Color.rgb(
                            255,
                            225,
                            80
                    );
                    applyTextStyle();
                });

        findViewById(R.id.btnTextPink)
                .setOnClickListener(v -> {
                    textColor = Color.rgb(
                            255,
                            105,
                            180
                    );
                    applyTextStyle();
                });
    }

    // ------------------------------------------------------------
    // TEXT
    // ------------------------------------------------------------

    private void openTextEditor() {

        storyCaption.setVisibility(View.VISIBLE);
        textToolbar.setVisibility(View.VISIBLE);

        if (storyCaption.getText().length() == 0) {
            storyCaption.setHint("Write something…");
        }

        storyCaption.requestFocus();
        storyCaption.setSelection(
                storyCaption.length()
        );

        applyTextStyle();

        InputMethodManager imm =
                (InputMethodManager)
                        getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );

        if (imm != null) {

            imm.showSoftInput(
                    storyCaption,
                    InputMethodManager.SHOW_IMPLICIT
            );
        }
    }

    private void finishTextEditing() {

        hideKeyboard();

        storyCaption.clearFocus();

        if (storyCaption.getText().toString().trim().isEmpty()) {
            storyCaption.setVisibility(View.GONE);
        }

        textToolbar.setVisibility(View.GONE);
    }

    private void setupTextDrag() {

        storyCaption.setOnTouchListener(
                (v, event) -> {

                    switch (event.getActionMasked()) {

                        case MotionEvent.ACTION_DOWN:

                            textLastX =
                                    event.getRawX();

                            textLastY =
                                    event.getRawY();

                            textMoved = false;

                            return false;

                        case MotionEvent.ACTION_MOVE:

                            float dx =
                                    event.getRawX()
                                            - textLastX;

                            float dy =
                                    event.getRawY()
                                            - textLastY;

                            if (Math.abs(dx) > 4 ||
                                    Math.abs(dy) > 4) {

                                textMoved = true;

                                storyCaption.setTranslationX(
                                        storyCaption.getTranslationX()
                                                + dx
                                );

                                storyCaption.setTranslationY(
                                        storyCaption.getTranslationY()
                                                + dy
                                );

                                textLastX =
                                        event.getRawX();

                                textLastY =
                                        event.getRawY();

                                return true;
                            }

                            return false;

                        case MotionEvent.ACTION_UP:

                            if (textMoved) {
                                return true;
                            }

                            return false;
                    }

                    return false;
                }
        );
    }

    private void applyTextStyle() {

        storyCaption.setTextSize(
                textSizeSp
        );

        storyCaption.setTextColor(
                textColor
        );

        if (textStyle == 1) {

            storyCaption.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.BOLD
                    )
            );

            storyCaption.setBackground(
                    roundedBg(
                            0xAA000000,
                            dp(12)
                    )
            );

        } else if (textStyle == 2) {

            storyCaption.setTypeface(
                    Typeface.create(
                            "sans-serif-light",
                            Typeface.NORMAL
                    )
            );

            storyCaption.setBackground(
                    roundedBg(
                            0x55000000,
                            dp(24)
                    )
            );

        } else {

            storyCaption.setTypeface(
                    Typeface.create(
                            "sans-serif",
                            Typeface.NORMAL
                    )
            );

            storyCaption.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    // ------------------------------------------------------------
    // MUSIC
    // ------------------------------------------------------------

    private void openMusicSheet() {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(22),
                dp(8),
                dp(22),
                dp(12)
        );

        TextView title =
                new TextView(this);

        title.setText("Music");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView subtitle =
                new TextView(this);

        subtitle.setText(
                selectedSongUri == null
                        ? "Choose a song from your phone"
                        : selectedSongName
        );

        subtitle.setTextColor(
                0xFFBDBDBD
        );

        subtitle.setTextSize(14);
        subtitle.setPadding(
                0,
                dp(6),
                0,
                dp(16)
        );

        root.addView(subtitle);

        Button choose =
                actionButton(
                        selectedSongUri == null
                                ? "Choose music"
                                : "Change music"
                );

        root.addView(
                choose,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(50)
                )
        );

        Button preview =
                actionButton(
                        "Preview selected song"
                );

        preview.setBackground(
                roundedBg(
                        0xFF2A2A2A,
                        dp(25)
                )
        );

        LinearLayout.LayoutParams pp =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(50)
                );

        pp.topMargin = dp(10);

        root.addView(
                preview,
                pp
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(root)
                        .setNegativeButton(
                                "Done",
                                null
                        )
                        .create();

        choose.setOnClickListener(
                v -> {
                    dialog.dismiss();
                    songPicker.launch("audio/*");
                }
        );

        preview.setOnClickListener(
                v -> {

                    if (selectedSongUri == null) {

                        Toast.makeText(
                                this,
                                "Choose music first",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if (musicPlayer == null) {

                        prepareMusicPreview(
                                selectedSongUri
                        );
                    }

                    if (musicPlayer == null) {
                        return;
                    }

                    if (musicPlayer.isPlaying()) {

                        musicPlayer.pause();
                        preview.setText(
                                "Play preview"
                        );

                    } else {

                        musicPlayer.start();
                        preview.setText(
                                "Pause preview"
                        );
                    }
                }
        );

        dialog.show();

        Window window =
                dialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawable(
                    new ColorDrawable(
                            0xFF151515
                    )
            );
        }
    }

    private void prepareMusicPreview(
            Uri uri
    ) {

        releaseMusic();

        try {

            musicPlayer =
                    MediaPlayer.create(
                            this,
                            uri
                    );

            if (musicPlayer != null) {

                musicPlayer.setOnCompletionListener(
                        mp -> {
                            try {
                                mp.seekTo(0);
                            } catch (Exception ignored) {
                            }
                        }
                );
            }

        } catch (Exception e) {

            musicPlayer = null;

            Toast.makeText(
                    this,
                    "Music preview unavailable",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private Button actionButton(String text) {

        Button button =
                new Button(this);

        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(15);
        button.setAllCaps(false);

        button.setBackground(
                roundedBg(
                        0xFF6C4FD3,
                        dp(25)
                )
        );

        return button;
    }

    // ------------------------------------------------------------
    // PUBLISH
    // ------------------------------------------------------------

    private void publishStory() {

        hideKeyboard();
        textToolbar.setVisibility(View.GONE);

        btnShareStory.setEnabled(false);
        btnShareStory.setText("Publishing…");

        if (localMediaUri != null) {

            uploadMediaThenContinue();
            return;
        }

        if (TextUtils.isEmpty(mediaUrl)) {

            showError("Story media missing");
            return;
        }

        if (selectedSongUri != null) {

            uploadSongAndSave(mediaUrl);

        } else {

            saveStory(
                    mediaUrl,
                    mediaType,
                    ""
            );
        }
    }

    private void uploadMediaThenContinue() {

        Toast.makeText(
                this,
                "Uploading story…",
                Toast.LENGTH_SHORT
        ).show();

        MediaManager.get()
                .upload(localMediaUri)
                .unsigned(UPLOAD_PRESET)
                .option(
                        "resource_type",
                        "video".equalsIgnoreCase(mediaType)
                                ? "video"
                                : "image"
                )
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {
                    }

                    @Override
                    public void onProgress(
                            String requestId,
                            long bytes,
                            long totalBytes) {
                    }

                    @Override
                    public void onSuccess(
                            String requestId,
                            Map resultData) {

                        Object urlObject =
                                resultData.get(
                                        "secure_url"
                                );

                        if (urlObject == null) {

                            runOnUiThread(
                                    () -> showError(
                                            "Media URL missing"
                                    )
                            );

                            return;
                        }

                        String uploadedUrl =
                                String.valueOf(urlObject);

                        runOnUiThread(() -> {

                            if (selectedSongUri != null) {

                                uploadSongAndSave(
                                        uploadedUrl
                                );

                            } else {

                                saveStory(
                                        uploadedUrl,
                                        mediaType,
                                        ""
                                );
                            }
                        });
                    }

                    @Override
                    public void onError(
                            String requestId,
                            ErrorInfo error) {

                        runOnUiThread(
                                () -> showError(
                                        error != null &&
                                                error.getDescription() != null
                                                ? error.getDescription()
                                                : "Story upload failed"
                                )
                        );
                    }

                    @Override
                    public void onReschedule(
                            String requestId,
                            ErrorInfo error) {
                    }
                })
                .dispatch();
    }

    private void uploadSongAndSave(
            String finalMediaUrl
    ) {

        if (selectedSongUri == null) {

            saveStory(
                    finalMediaUrl,
                    mediaType,
                    ""
            );

            return;
        }

        Toast.makeText(
                this,
                "Adding music…",
                Toast.LENGTH_SHORT
        ).show();

        MediaManager.get()
                .upload(selectedSongUri)
                .unsigned(UPLOAD_PRESET)
                .option(
                        "resource_type",
                        "video"
                )
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {
                    }

                    @Override
                    public void onProgress(
                            String requestId,
                            long bytes,
                            long totalBytes) {
                    }

                    @Override
                    public void onSuccess(
                            String requestId,
                            Map resultData) {

                        Object urlObject =
                                resultData.get(
                                        "secure_url"
                                );

                        if (urlObject == null) {

                            runOnUiThread(
                                    () -> showError(
                                            "Music URL missing"
                                    )
                            );

                            return;
                        }

                        saveStory(
                                finalMediaUrl,
                                mediaType,
                                String.valueOf(
                                        urlObject
                                )
                        );
                    }

                    @Override
                    public void onError(
                            String requestId,
                            ErrorInfo error) {

                        runOnUiThread(
                                () -> showError(
                                        error != null &&
                                                error.getDescription() != null
                                                ? error.getDescription()
                                                : "Music upload failed"
                                )
                        );
                    }

                    @Override
                    public void onReschedule(
                            String requestId,
                            ErrorInfo error) {
                    }
                })
                .dispatch();
    }

    private void saveStory(
            String finalMediaUrl,
            String finalMediaType,
            String songUrl
    ) {

        String uid =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(user -> {

                    String username =
                            safe(
                                    user.getString(
                                            "username"
                                    )
                            );

                    if (username.isEmpty()) {

                        username =
                                safe(
                                        user.getString(
                                                "userId"
                                        )
                                );
                    }

                    if (username.isEmpty()) {
                        username = "User";
                    }

                    String name =
                            safe(
                                    user.getString(
                                            "name"
                                    )
                            );

                    String profileImage =
                            safe(
                                    user.getString(
                                            "profileImage"
                                    )
                            );

                    long now =
                            System.currentTimeMillis();

                    long expiresAt =
                            now +
                                    24L * 60L * 60L * 1000L;

                    Map<String, Object> story =
                            new HashMap<>();

                    story.put("userId", uid);
                    story.put("username", username);
                    story.put("name", name);
                    story.put("profileImage", profileImage);

                    story.put(
                            "mediaUrl",
                            finalMediaUrl
                    );

                    story.put(
                            "mediaType",
                            finalMediaType
                    );

                    story.put(
                            "caption",
                            storyCaption.getText()
                                    .toString()
                                    .trim()
                    );

                    // Saved so StoryViewer can play it.
                    story.put(
                            "songName",
                            selectedSongName == null
                                    ? ""
                                    : selectedSongName
                    );

                    story.put(
                            "songUrl",
                            songUrl == null
                                    ? ""
                                    : songUrl
                    );

                    story.put(
                            "timestamp",
                            now
                    );

                    story.put(
                            "expiresAt",
                            expiresAt
                    );

                    story.put(
                            "viewedBy",
                            new ArrayList<String>()
                    );

                    story.put(
                            "likes",
                            new ArrayList<String>()
                    );

                    db.collection("stories")
                            .add(story)
                            .addOnSuccessListener(
                                    documentReference -> {

                                        runOnUiThread(() -> {

                                            Toast.makeText(
                                                    this,
                                                    "Story added 🎉",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            finish();
                                        });
                                    }
                            )
                            .addOnFailureListener(
                                    e ->
                                            showError(
                                                    e.getMessage() != null
                                                            ? e.getMessage()
                                                            : "Could not add story"
                                            )
                            );
                })
                .addOnFailureListener(
                        e -> showError(
                                "Could not load profile"
                        )
                );
    }

    // ------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------

    private GradientDrawable roundedBg(
            int color,
            float radius
    ) {

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(color);
        bg.setCornerRadius(radius);

        return bg;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void hideKeyboard() {

        View current =
                getCurrentFocus();

        if (current != null) {

            InputMethodManager imm =
                    (InputMethodManager)
                            getSystemService(
                                    Context.INPUT_METHOD_SERVICE
                            );

            if (imm != null) {

                imm.hideSoftInputFromWindow(
                        current.getWindowToken(),
                        0
                );
            }
        }
    }

    private void showError(String message) {

        runOnUiThread(() -> {

            btnShareStory.setEnabled(true);
            btnShareStory.setText(
                    "Share to Story"
            );

            Toast.makeText(
                    this,
                    message == null ||
                            message.isEmpty()
                            ? "Story failed"
                            : message,
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    private void releaseMusic() {

        if (musicPlayer != null) {

            try {
                musicPlayer.stop();
            } catch (Exception ignored) {
            }

            musicPlayer.release();
            musicPlayer = null;
        }
    }

    private int dp(int value) {
        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    @Override
    protected void onDestroy() {

        if (storyPreviewVideo != null) {
            storyPreviewVideo.stopPlayback();
        }

        releaseMusic();

        super.onDestroy();
    }
}
