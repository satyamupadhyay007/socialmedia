package com.example.login;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UploadPostActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgPostPreview;
    private EditText editCaption;

    private Button btnChoosePhoto;
    private Button btnPost;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Uri selectedMediaUri;

    // image = normal post
    // video = reel
    private String selectedMediaType = "image";

    private String currentUserId;

    private static final String UPLOAD_PRESET =
            "frenzo_media";

    // ============================================================
    // PHOTO + VIDEO PICKER
    // ============================================================

    private final ActivityResultLauncher<String> mediaPicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        selectedMediaUri = uri;

                        // Show selected media preview
                        if ("video".equalsIgnoreCase(
                                selectedMediaType
                        )) {

                            /*
                             * Glide can show the first frame
                             * of a video as preview.
                             */
                            Glide.with(this)
                                    .load(uri)
                                    .centerCrop()
                                    .into(imgPostPreview);

                        } else {

                            Glide.with(this)
                                    .load(uri)
                                    .centerCrop()
                                    .into(imgPostPreview);
                        }
                    }
            );

    // ============================================================
    // CREATE
    // ============================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_upload_post
        );

        mAuth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        currentUserId =
                mAuth.getCurrentUser().getUid();

        // ========================================================
        // FIND VIEWS
        // ========================================================

        btnBack =
                findViewById(
                        R.id.btnBackUpload
                );

        imgPostPreview =
                findViewById(
                        R.id.imgPostPreview
                );

        editCaption =
                findViewById(
                        R.id.editCaption
                );

        btnChoosePhoto =
                findViewById(
                        R.id.btnChoosePhoto
                );

        btnPost =
                findViewById(
                        R.id.btnPost
                );

        progressBar =
                findViewById(
                        R.id.progressPost
                );

        // ========================================================
        // BACK
        // ========================================================

        btnBack.setOnClickListener(
                v -> finish()
        );

        // ========================================================
        // CHOOSE PHOTO / VIDEO
        // ========================================================

        btnChoosePhoto.setOnClickListener(
                v -> showMediaTypeDialog()
        );

        // ========================================================
        // UPLOAD
        // ========================================================

        btnPost.setOnClickListener(
                v -> uploadPost()
        );
    }

    // ============================================================
    // PHOTO / VIDEO DIALOG
    // ============================================================

    private void showMediaTypeDialog() {

        String[] options = {
                "📷 Photo",
                "🎥 Video / Reel"
        };

        new AlertDialog.Builder(this)
                .setTitle(
                        "Choose what to upload"
                )
                .setItems(
                        options,
                        (dialog, which) -> {

                            if (which == 0) {

                                selectedMediaType =
                                        "image";

                                mediaPicker.launch(
                                        "image/*"
                                );

                            } else {

                                selectedMediaType =
                                        "video";

                                mediaPicker.launch(
                                        "video/*"
                                );
                            }
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // ============================================================
    // UPLOAD POST / REEL
    // ============================================================

    private void uploadPost() {

        if (selectedMediaUri == null) {

            Toast.makeText(
                    this,
                    "Please select a photo or video",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String caption =
                editCaption.getText()
                        .toString()
                        .trim();

        progressBar.setVisibility(
                View.VISIBLE
        );

        btnPost.setEnabled(
                false
        );

        Toast.makeText(
                this,
                "Uploading...",
                Toast.LENGTH_SHORT
        ).show();

        // ========================================================
        // CLOUDINARY
        // ========================================================

        MediaManager.get()
                .upload(
                        selectedMediaUri
                )
                .unsigned(
                        UPLOAD_PRESET
                )
                /*
                 * VERY IMPORTANT
                 *
                 * image → image resource
                 * video → video resource
                 */
                .option(
                        "resource_type",
                        "video".equalsIgnoreCase(
                                selectedMediaType
                        )
                                ? "video"
                                : "image"
                )
                .callback(
                        new UploadCallback() {

                            @Override
                            public void onStart(
                                    String requestId
                            ) {
                            }

                            @Override
                            public void onProgress(
                                    String requestId,
                                    long bytes,
                                    long totalBytes
                            ) {
                            }

                            @Override
                            public void onSuccess(
                                    String requestId,
                                    Map resultData
                            ) {

                                String mediaUrl =
                                        "";

                                if (resultData != null) {

                                    Object secureUrl =
                                            resultData.get(
                                                    "secure_url"
                                            );

                                    if (secureUrl != null) {

                                        mediaUrl =
                                                secureUrl.toString();
                                    }
                                }

                                if (mediaUrl.isEmpty()) {

                                    runOnUiThread(
                                            () -> {

                                                progressBar
                                                        .setVisibility(
                                                                View.GONE
                                                        );

                                                btnPost
                                                        .setEnabled(
                                                                true
                                                        );

                                                Toast.makeText(
                                                        UploadPostActivity.this,
                                                        "Media URL not received",
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            }
                                    );

                                    return;
                                }

                                savePostToFirestore(
                                        mediaUrl,
                                        caption,
                                        selectedMediaType
                                );
                            }

                            @Override
                            public void onError(
                                    String requestId,
                                    ErrorInfo error
                            ) {

                                runOnUiThread(
                                        () -> {

                                            progressBar
                                                    .setVisibility(
                                                            View.GONE
                                                    );

                                            btnPost
                                                    .setEnabled(
                                                            true
                                                    );

                                            String message =
                                                    "Upload failed";

                                            if (error != null &&
                                                    error.getDescription() != null &&
                                                    !error.getDescription()
                                                            .isEmpty()) {

                                                message =
                                                        error.getDescription();
                                            }

                                            Toast.makeText(
                                                    UploadPostActivity.this,
                                                    message,
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                );
                            }

                            @Override
                            public void onReschedule(
                                    String requestId,
                                    ErrorInfo error
                            ) {
                            }
                        }
                )
                .dispatch();
    }

    // ============================================================
    // SAVE TO FIRESTORE
    // ============================================================

    private void savePostToFirestore(
            String mediaUrl,
            String caption,
            String mediaType
    ) {

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(
                        userDocument -> {

                            String userName =
                                    userDocument.getString(
                                            "name"
                                    );

                            if (userName == null ||
                                    userName.trim().isEmpty()) {

                                userName =
                                        "User";
                            }

                            // Generate Post ID
                            String postId =
                                    db.collection("posts")
                                            .document()
                                            .getId();

                            Map<String, Object> post =
                                    new HashMap<>();

                            post.put(
                                    "postId",
                                    postId
                            );

                            post.put(
                                    "userId",
                                    currentUserId
                            );

                            post.put(
                                    "userName",
                                    userName
                            );

                            /*
                             * Existing field name is kept
                             * as imageUrl so old adapter
                             * remains compatible.
                             */
                            post.put(
                                    "imageUrl",
                                    mediaUrl
                            );

                            post.put(
                                    "caption",
                                    caption
                            );

                            /*
                             * THIS IS THE IMPORTANT FIELD
                             *
                             * image = Posts
                             * video = Reels
                             */
                            post.put(
                                    "mediaType",
                                    "video".equalsIgnoreCase(
                                            mediaType
                                    )
                                            ? "video"
                                            : "image"
                            );

                            post.put(
                                    "createdAt",
                                    Timestamp.now()
                            );

                            db.collection("posts")
                                    .document(postId)
                                    .set(post)
                                    .addOnSuccessListener(
                                            unused -> {

                                                progressBar
                                                        .setVisibility(
                                                                View.GONE
                                                        );

                                                btnPost
                                                        .setEnabled(
                                                                true
                                                        );

                                                if ("video"
                                                        .equalsIgnoreCase(
                                                                mediaType
                                                        )) {

                                                    Toast.makeText(
                                                            UploadPostActivity.this,
                                                            "Reel uploaded successfully 🎥",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                } else {

                                                    Toast.makeText(
                                                            UploadPostActivity.this,
                                                            "Post uploaded successfully 📷",
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }

                                                finish();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> {

                                                progressBar
                                                        .setVisibility(
                                                                View.GONE
                                                        );

                                                btnPost
                                                        .setEnabled(
                                                                true
                                                        );

                                                Toast.makeText(
                                                        UploadPostActivity.this,
                                                        "Failed: " +
                                                                e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            }
                                    );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            progressBar
                                    .setVisibility(
                                            View.GONE
                                    );

                            btnPost
                                    .setEnabled(
                                            true
                                    );

                            Toast.makeText(
                                    UploadPostActivity.this,
                                    "Could not get user data",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    // ============================================================
    // CLEAN VIDEO
    // ============================================================

    @Override
    protected void onDestroy() {

        if (imgPostPreview != null) {

            Glide.with(this)
                    .clear(imgPostPreview);
        }

        super.onDestroy();
    }
}