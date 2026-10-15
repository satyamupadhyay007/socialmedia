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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgEditProfile;

    private Button btnChangePhoto;
    private Button btnSave;

    private EditText editName;
    private EditText editUsername;
    private EditText editBio;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String currentUserId;

    private Uri selectedImageUri;

    private static final String UPLOAD_PRESET = "frenzo_media";


    // ==================================================
    // IMAGE PICKER
    // ==================================================

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {

                        if (uri == null) {
                            return;
                        }

                        selectedImageUri = uri;

                        Glide.with(
                                        EditProfileActivity.this
                                )
                                .load(uri)
                                .circleCrop()
                                .into(imgEditProfile);
                    }
            );


    // ==================================================
    // ON CREATE
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_edit_profile
        );

        // System Bar Insets for Responsive Edge-To-Edge
        View root = findViewById(R.id.editProfileRoot);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                                | WindowInsetsCompat.Type.displayCutout()
                );
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }


        // ==================================================
        // FIREBASE
        // ==================================================

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        if (mAuth.getCurrentUser() == null) {

            finish();
            return;
        }


        currentUserId =
                mAuth.getCurrentUser().getUid();


        // ==================================================
        // CONNECT XML
        // ==================================================

        ImageButton btnBack =
                findViewById(R.id.btnBack);

        imgEditProfile =
                findViewById(R.id.imgEditProfile);

        btnChangePhoto =
                findViewById(R.id.btnChangePhoto);

        btnSave =
                findViewById(R.id.btnSave);

        editName =
                findViewById(R.id.editName);

        editUsername =
                findViewById(R.id.editUsername);

        editBio =
                findViewById(R.id.editBio);

        progressBar =
                findViewById(R.id.progressBar);


        // ==================================================
        // LOAD OLD PROFILE DATA
        // ==================================================

        loadProfile();


        // ==================================================
        // BACK
        // ==================================================

        if (btnBack != null) {
            btnBack.setOnClickListener(
                    v -> finish()
            );
        }


        // ==================================================
        // CHANGE PHOTO
        // ==================================================

        if (btnChangePhoto != null) {
            btnChangePhoto.setOnClickListener(
                    v -> imagePicker.launch("image/*")
            );
        }


        // ==================================================
        // SAVE
        // ==================================================

        if (btnSave != null) {
            btnSave.setOnClickListener(
                    v -> saveProfile()
            );
        }
    }


    // ==================================================
    // LOAD PROFILE
    // ==================================================

    private void loadProfile() {

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }


                    String name =
                            document.getString("name");

                    if (name != null) {

                        editName.setText(name);
                    }

                    String primaryHandle = document.getString("userId");
                    String username = (primaryHandle != null && !primaryHandle.isEmpty())
                            ? primaryHandle
                            : document.getString("username");

                    if (username != null) {
                        editUsername.setText(username);
                    }


                    String bio =
                            document.getString("bio");

                    if (bio != null) {

                        editBio.setText(bio);
                    }


                    String profileImage =
                            document.getString("profileImage");

                    if (
                            profileImage != null &&
                                    !profileImage.isEmpty()
                    ) {

                        Glide.with(
                                        EditProfileActivity.this
                                )
                                .load(profileImage)
                                .circleCrop()
                                .into(imgEditProfile);
                    }

                })
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        EditProfileActivity.this,
                                        "Could not load profile",
                                        Toast.LENGTH_SHORT
                                ).show()
                );
    }


    // ==================================================
    // SAVE PROFILE
    // ==================================================

    private void saveProfile() {

        String name =
                editName
                        .getText()
                        .toString()
                        .trim();

        String rawUsername =
                editUsername
                        .getText()
                        .toString()
                        .trim();

        String username = rawUsername.contains(" ") ? rawUsername.replaceAll("\\s+", "_") : rawUsername;

        String bio =
                editBio
                        .getText()
                        .toString()
                        .trim();


        if (name.length() > 50) {

            editName.setError(
                    "Maximum 50 characters"
            );

            editName.requestFocus();

            return;
        }

        if (username.length() > 30) {
            editUsername.setError("Maximum 30 characters");
            editUsername.requestFocus();
            return;
        }

        if (!java.util.Objects.equals(username, rawUsername)) {
            editUsername.setText(username);
        }


        if (bio.length() > 150) {

            editBio.setError(
                    "Maximum 150 characters"
            );

            editBio.requestFocus();

            return;
        }


        showLoading();


        if (selectedImageUri != null) {

            uploadPhoto(
                    selectedImageUri,
                    name,
                    username,
                    bio
            );

        } else {

            saveToFirestore(
                    name,
                    username,
                    bio,
                    null
            );
        }
    }


    // ==================================================
    // UPLOAD PHOTO
    // ==================================================

    private void uploadPhoto(
            Uri imageUri,
            String name,
            String username,
            String bio
    ) {

        MediaManager.get()
                .upload(imageUri)
                .unsigned(UPLOAD_PRESET)
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

                                if (totalBytes <= 0) {
                                    return;
                                }

                                int progress =
                                        (int)
                                                (
                                                        bytes * 100
                                                                / totalBytes
                                                );

                                runOnUiThread(
                                        () ->
                                                progressBar.setProgress(
                                                        progress
                                                )
                                );
                            }


                            @Override
                            public void onSuccess(
                                    String requestId,
                                    Map resultData
                            ) {

                                String imageUrl = "";


                                if (resultData != null) {

                                    Object secureUrl =
                                            resultData.get(
                                                    "secure_url"
                                            );

                                    if (secureUrl != null) {

                                        imageUrl =
                                                secureUrl.toString();
                                    }
                                }


                                final String finalImageUrl =
                                        imageUrl;


                                runOnUiThread(
                                        () -> {

                                            if (
                                                    finalImageUrl.isEmpty()
                                            ) {

                                                hideLoading();

                                                Toast.makeText(
                                                        EditProfileActivity.this,
                                                        "Photo URL not received",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                return;
                                            }


                                            saveToFirestore(
                                                    name,
                                                    username,
                                                    bio,
                                                    finalImageUrl
                                            );
                                        }
                                );
                            }


                            @Override
                            public void onError(
                                    String requestId,
                                    ErrorInfo error
                            ) {

                                String message =
                                        "Photo upload failed";


                                if (
                                        error != null &&
                                                error.getDescription() != null
                                ) {

                                    message =
                                            error.getDescription();
                                }


                                final String finalMessage =
                                        message;


                                runOnUiThread(
                                        () -> {

                                            hideLoading();


                                            Toast.makeText(
                                                    EditProfileActivity.this,
                                                    finalMessage,
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


    // ==================================================
    // SAVE FIRESTORE
    // ==================================================

    private void saveToFirestore(
            String name,
            String username,
            String bio,
            String imageUrl
    ) {

        Map<String, Object> data =
                new HashMap<>();


        data.put(
                "name",
                name
        );

        if (!username.isEmpty()) {
            data.put("userId", username);
            data.put("username", username);
        }


        data.put(
                "bio",
                bio
        );


        if (
                imageUrl != null &&
                        !imageUrl.isEmpty()
        ) {

            data.put(
                    "profileImage",
                    imageUrl
            );
        }


        db.collection("users")
                .document(currentUserId)
                .set(
                        data,
                        SetOptions.merge()
                )
                .addOnSuccessListener(
                        unused -> {

                            hideLoading();


                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                            ).show();


                            finish();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            hideLoading();


                            Toast.makeText(
                                    EditProfileActivity.this,
                                    "Save failed: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // ==================================================
    // LOADING
    // ==================================================

    private void showLoading() {

        progressBar.setVisibility(
                View.VISIBLE
        );

        btnSave.setEnabled(
                false
        );

        btnChangePhoto.setEnabled(
                false
        );
    }


    private void hideLoading() {

        progressBar.setVisibility(
                View.GONE
        );

        btnSave.setEnabled(
                true
        );

        btnChangePhoto.setEnabled(
                true
        );
    }
}
