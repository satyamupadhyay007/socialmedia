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
                                .centerCrop()
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

        btnBack.setOnClickListener(
                v -> finish()
        );


        // ==================================================
        // CHANGE PHOTO
        // ==================================================

        btnChangePhoto.setOnClickListener(
                v ->
                        imagePicker.launch("image/*")
        );


        // ==================================================
        // SAVE
        // ==================================================

        btnSave.setOnClickListener(
                v -> saveProfile()
        );
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
                                .centerCrop()
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
                    bio
            );

        } else {

            saveToFirestore(
                    name,
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
            String bio,
            String imageUrl
    ) {

        Map<String, Object> data =
                new HashMap<>();


        data.put(
                "name",
                name
        );


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