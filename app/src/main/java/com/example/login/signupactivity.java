package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signupactivity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText email;

    Button signupButton;
    Button loginButton;

    LinearLayout signupProgress;

    TextView signupDot1;
    TextView signupDot2;
    TextView signupDot3;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_signupactivity
        );

        // ==========================================
        // CONNECT XML
        // ==========================================

        username = findViewById(
                R.id.recoveryEmail
        );

        password = findViewById(
                R.id.PASSWORD1
        );

        email = findViewById(
                R.id.email
        );

        signupButton = findViewById(
                R.id.login
        );

        loginButton = findViewById(
                R.id.gotologin
        );

        // ==========================================
        // PROGRESS LOADER
        // ==========================================

        signupProgress = findViewById(
                R.id.signupProgress
        );

        signupDot1 = findViewById(
                R.id.signupDot1
        );

        signupDot2 = findViewById(
                R.id.signupDot2
        );

        signupDot3 = findViewById(
                R.id.signupDot3
        );

        // ==========================================
        // FIREBASE
        // ==========================================

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        // ==========================================
        // SIGN UP
        // ==========================================

        signupButton.setOnClickListener(v -> {

            // Hide keyboard
            hideKeyboard();

            // Clear previous errors
            username.setError(null);
            password.setError(null);
            email.setError(null);

            String userIdText =
                    username
                            .getText()
                            .toString()
                            .trim();

            String passwordText =
                    password
                            .getText()
                            .toString()
                            .trim();

            String emailText =
                    email
                            .getText()
                            .toString()
                            .trim();

            // ==========================================
            // USER ID
            // ==========================================

            if (userIdText.isEmpty()) {

                username.setError(
                        "Enter User ID"
                );

                username.requestFocus();

                return;
            }

            // ==========================================
            // EMAIL
            // ==========================================

            if (emailText.isEmpty()) {

                email.setError(
                        "Enter email"
                );

                email.requestFocus();

                return;
            }

            // ==========================================
            // PASSWORD
            // ==========================================

            if (passwordText.isEmpty()) {

                password.setError(
                        "Enter password"
                );

                password.requestFocus();

                return;
            }

            if (passwordText.length() < 6) {

                password.setError(
                        "Password must be at least 6 characters"
                );

                password.requestFocus();

                return;
            }

            // ==========================================
            // START LOADING
            // ==========================================

            startSignupLoading();

            // ==========================================
            // USER ID CASE INSENSITIVE
            // ==========================================

            String userIdKey =
                    userIdText.toLowerCase();

            // ==========================================
            // CREATE FIREBASE AUTH ACCOUNT
            // ==========================================

            mAuth.createUserWithEmailAndPassword(
                            emailText,
                            passwordText
                    )
                    .addOnCompleteListener(task -> {

                        // ==================================
                        // AUTH FAILURE
                        // ==================================

                        if (!task.isSuccessful()) {

                            stopSignupLoading();

                            String error =
                                    "Signup failed";

                            if (task.getException() != null) {

                                String firebaseError =
                                        task.getException()
                                                .getMessage();

                                if (firebaseError != null) {

                                    error =
                                            firebaseError;
                                }
                            }

                            /*
                             * Email already registered
                             */

                            if (error.toLowerCase()
                                    .contains("email")
                                    &&
                                    error.toLowerCase()
                                            .contains("already")) {

                                email.setError(
                                        "Email already registered"
                                );

                                email.requestFocus();

                            } else {

                                email.setError(
                                        "Could not create account"
                                );

                                email.requestFocus();
                            }

                            return;
                        }

                        // ==================================
                        // GET FIREBASE USER
                        // ==================================

                        FirebaseUser firebaseUser =
                                mAuth.getCurrentUser();

                        if (firebaseUser == null) {

                            stopSignupLoading();

                            username.setError(
                                    "Unable to create user"
                            );

                            username.requestFocus();

                            return;
                        }

                        // ==================================
                        // FIREBASE UID
                        // ==================================

                        String authUid =
                                firebaseUser.getUid();

                        // ==========================================
                        // SAVE FIRESTORE DATA
                        // ==========================================

                        db.runTransaction(
                                        transaction -> {

                                            // ==================================
                                            // USERNAME DOCUMENT
                                            // ==================================

                                            var usernameRef =
                                                    db.collection(
                                                            "usernames"
                                                    ).document(
                                                            userIdKey
                                                    );

                                            // ==================================
                                            // USER PROFILE DOCUMENT
                                            // ==================================

                                            var userRef =
                                                    db.collection(
                                                            "users"
                                                    ).document(
                                                            authUid
                                                    );

                                            // ==================================
                                            // CHECK USER ID
                                            // ==================================

                                            var usernameSnapshot =
                                                    transaction.get(
                                                            usernameRef
                                                    );

                                            if (usernameSnapshot.exists()) {

                                                throw new RuntimeException(
                                                        "USERNAME_TAKEN"
                                                );
                                            }

                                            // ==================================
                                            // RESERVE USER ID
                                            // ==================================

                                            Map<String, Object>
                                                    usernameData =
                                                    new HashMap<>();

                                            usernameData.put(
                                                    "uid",
                                                    authUid
                                            );

                                            usernameData.put(
                                                    "userId",
                                                    userIdText
                                            );

                                            /*
                                             * Email is kept only for
                                             * Firebase authentication.
                                             */

                                            usernameData.put(
                                                    "email",
                                                    emailText
                                            );

                                            transaction.set(
                                                    usernameRef,
                                                    usernameData
                                            );

                                            // ==================================
                                            // USER PROFILE
                                            // ==================================

                                            Map<String, Object>
                                                    userData =
                                                    new HashMap<>();

                                            userData.put(
                                                    "userId",
                                                    userIdText
                                            );

                                            /*
                                             * Name, bio and DP will be
                                             * added later.
                                             */

                                            transaction.set(
                                                    userRef,
                                                    userData
                                            );

                                            return null;

                                        }
                                )
                                .addOnSuccessListener(
                                        unused -> {

                                            // Stop loading
                                            stopSignupLoading();

                                            Intent intent =
                                                    new Intent(
                                                            signupactivity.this,
                                                            homepage.class
                                                    );

                                            startActivity(intent);

                                            finish();
                                        }
                                )
                                .addOnFailureListener(
                                        e -> {

                                            // Stop loading
                                            stopSignupLoading();

                                            // ==================================
                                            // USER ID ALREADY TAKEN
                                            // ==================================

                                            if ("USERNAME_TAKEN"
                                                    .equals(
                                                            e.getMessage()
                                                    )) {

                                                /*
                                                 * Delete Auth account because
                                                 * User ID is already taken.
                                                 */

                                                firebaseUser.delete()
                                                        .addOnCompleteListener(
                                                                deleteTask -> {

                                                                    username.setError(
                                                                            "User ID already taken"
                                                                    );

                                                                    username.requestFocus();
                                                                }
                                                        );

                                            } else {

                                                username.setError(
                                                        "Could not save user data"
                                                );

                                                username.requestFocus();
                                            }
                                        }
                                );
                    });
        });

        // ==========================================
        // GO TO LOGIN
        // ==========================================

        loginButton.setOnClickListener(v -> {

            hideKeyboard();

            Intent intent =
                    new Intent(
                            signupactivity.this,
                            login.class
                    );

            startActivity(intent);
        });

        // ==========================================
        // EDGE TO EDGE
        // ==========================================

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(
                        R.id.newpassword
                ),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type
                                            .systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    // ==========================================
    // START SIGNUP LOADING
    // ==========================================

    private void startSignupLoading() {

        signupButton.setEnabled(false);

        signupButton.setVisibility(
                View.GONE
        );

        signupProgress.setVisibility(
                View.VISIBLE
        );

        animateSignupDots();
    }

    // ==========================================
    // STOP SIGNUP LOADING
    // ==========================================

    private void stopSignupLoading() {

        signupButton.setEnabled(true);

        signupButton.setVisibility(
                View.VISIBLE
        );

        signupProgress.setVisibility(
                View.GONE
        );
    }

    // ==========================================
    // THREE DOT ANIMATION
    // ==========================================

    private void animateSignupDots() {

        signupDot1.animate()
                .alpha(0.3f)
                .setDuration(300)
                .withEndAction(() -> signupDot1.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start())
                .start();

        signupDot2.animate()
                .alpha(0.3f)
                .setStartDelay(200)
                .setDuration(300)
                .withEndAction(() -> signupDot2.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start())
                .start();

        signupDot3.animate()
                .alpha(0.3f)
                .setStartDelay(400)
                .setDuration(300)
                .withEndAction(() -> signupDot3.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start())
                .start();

        // Repeat animation while loading
        signupProgress.postDelayed(
                () -> {

                    if (signupProgress.getVisibility()
                            == View.VISIBLE) {

                        animateSignupDots();
                    }

                },
                900
        );
    }

    // ==========================================
    // HIDE KEYBOARD
    // ==========================================

    private void hideKeyboard() {

        InputMethodManager imm =
                (InputMethodManager)
                        getSystemService(
                                Context.INPUT_METHOD_SERVICE
                        );

        if (imm != null &&
                getCurrentFocus() != null) {

            imm.hideSoftInputFromWindow(
                    getCurrentFocus()
                            .getWindowToken(),
                    0
            );
        }
    }
}