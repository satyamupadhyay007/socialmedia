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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class login extends AppCompatActivity {

    EditText username;
    EditText password;

    Button loginButton;
    Button signupButton;
    Button forgetPasswordButton;

    LinearLayout loginProgress;

    TextView dot1;
    TextView dot2;
    TextView dot3;

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_login);


        // ==========================================
        // FIREBASE
        // ==========================================

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();


        // ==========================================
        // CHECK EXISTING LOGIN SESSION
        // ==========================================
        //
        // Firebase automatically keeps the signed-in
        // user's session. So if a user is already
        // logged in, don't show login screen again.
        //

        if (mAuth.getCurrentUser() != null) {

            Intent intent =
                    new Intent(
                            login.this,
                            homepage.class
                    );

            // Remove login screen from back stack
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();

            return;
        }


        // ==========================================
        // CONNECT XML
        // ==========================================

        username =
                findViewById(
                        R.id.newusername
                );

        password =
                findViewById(
                        R.id.password
                );

        loginButton =
                findViewById(
                        R.id.loginButton
                );

        signupButton =
                findViewById(
                        R.id.createAccount
                );

        forgetPasswordButton =
                findViewById(
                        R.id.forgetPassword
                );

        loginProgress =
                findViewById(
                        R.id.loginProgress
                );

        dot1 =
                findViewById(
                        R.id.dot1
                );

        dot2 =
                findViewById(
                        R.id.dot2
                );

        dot3 =
                findViewById(
                        R.id.dot3
                );


        // ==========================================
        // LOGIN BUTTON
        // ==========================================

        loginButton.setOnClickListener(v -> {

            // Hide keyboard
            hideKeyboard();


            // Clear old errors
            username.setError(null);

            password.setError(null);


            String userIdText =
                    username
                            .getText()
                            .toString()
                            .trim();


            String userPassword =
                    password
                            .getText()
                            .toString()
                            .trim();


            // ==========================================
            // USER ID VALIDATION
            // ==========================================

            if (userIdText.isEmpty()) {

                username.setError(
                        "Enter User ID"
                );

                username.requestFocus();

                return;
            }


            // ==========================================
            // PASSWORD VALIDATION
            // ==========================================

            if (userPassword.isEmpty()) {

                password.setError(
                        "Enter password"
                );

                password.requestFocus();

                return;
            }


            // ==========================================
            // START LOADING
            // ==========================================

            startLoginLoading();


            // ==========================================
            // USER ID CASE INSENSITIVE
            // ==========================================

            String userIdKey =
                    userIdText.toLowerCase();


            // ==========================================
            // FIND USERNAME
            // ==========================================

            db.collection("usernames")
                    .document(userIdKey)
                    .get()
                    .addOnSuccessListener(
                            usernameDocument -> {


                                // ==================================
                                // USER ID NOT FOUND
                                // ==================================

                                if (
                                        !usernameDocument.exists()
                                ) {

                                    stopLoginLoading();

                                    username.setError(
                                            "User ID not found"
                                    );

                                    username.requestFocus();

                                    return;
                                }


                                // ==================================
                                // FIREBASE UID
                                // ==================================

                                String authUid =
                                        usernameDocument
                                                .getString(
                                                        "uid"
                                                );


                                if (
                                        authUid == null ||
                                                authUid.isEmpty()
                                ) {

                                    stopLoginLoading();

                                    username.setError(
                                            "Account data is incomplete"
                                    );

                                    username.requestFocus();

                                    return;
                                }


                                // ==================================
                                // NEW ACCOUNT
                                //
                                // usernames/{userId}/email
                                // ==================================

                                String email =
                                        usernameDocument
                                                .getString(
                                                        "email"
                                                );


                                if (
                                        email != null &&
                                                !email.isEmpty()
                                ) {

                                    loginWithEmail(
                                            email,
                                            userPassword
                                    );

                                    return;
                                }


                                // ==================================
                                // OLD ACCOUNT
                                //
                                // users/{uid}/email
                                // ==================================

                                db.collection("users")
                                        .document(authUid)
                                        .get()
                                        .addOnSuccessListener(
                                                userDocument -> {


                                                    if (
                                                            !userDocument.exists()
                                                    ) {

                                                        stopLoginLoading();

                                                        username.setError(
                                                                "User profile not found"
                                                        );

                                                        username.requestFocus();

                                                        return;
                                                    }


                                                    String oldEmail =
                                                            userDocument
                                                                    .getString(
                                                                            "email"
                                                                    );


                                                    if (
                                                            oldEmail == null ||
                                                                    oldEmail.isEmpty()
                                                    ) {

                                                        stopLoginLoading();

                                                        username.setError(
                                                                "Account data is incomplete"
                                                        );

                                                        username.requestFocus();

                                                        return;
                                                    }


                                                    // Login old account
                                                    loginWithEmail(
                                                            oldEmail,
                                                            userPassword
                                                    );
                                                }
                                        )
                                        .addOnFailureListener(
                                                e -> {

                                                    stopLoginLoading();

                                                    username.setError(
                                                            "Could not get user data"
                                                    );

                                                    username.requestFocus();
                                                }
                                        );
                            }
                    )
                    .addOnFailureListener(
                            e -> {

                                stopLoginLoading();

                                username.setError(
                                        "Could not check User ID"
                                );

                                username.requestFocus();
                            }
                    );
        });


        // ==========================================
        // CREATE ACCOUNT
        // ==========================================

        signupButton.setOnClickListener(v -> {

            hideKeyboard();

            Intent signup =
                    new Intent(
                            login.this,
                            signupactivity.class
                    );

            startActivity(
                    signup
            );
        });


        // ==========================================
        // FORGOT PASSWORD
        // ==========================================

        forgetPasswordButton.setOnClickListener(v -> {

            hideKeyboard();

            Intent forgetPass =
                    new Intent(
                            login.this,
                            forgetPassword.class
                    );

            startActivity(
                    forgetPass
            );
        });
    }


    // ==========================================
    // FIREBASE LOGIN
    // ==========================================

    private void loginWithEmail(
            String email,
            String userPassword
    ) {

        mAuth.signInWithEmailAndPassword(
                        email,
                        userPassword
                )
                .addOnCompleteListener(
                        login.this,
                        task -> {

                            if (task.isSuccessful()) {

                                stopLoginLoading();


                                Toast.makeText(
                                        login.this,
                                        "Login successful!",
                                        Toast.LENGTH_SHORT
                                ).show();


                                // ==================================
                                // HOMEPAGE
                                // ==================================

                                Intent intent =
                                        new Intent(
                                                login.this,
                                                homepage.class
                                        );


                                // Remove login from back stack
                                intent.addFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                );


                                startActivity(
                                        intent
                                );


                                finish();


                            } else {

                                // ==================================
                                // WRONG PASSWORD
                                // ==================================

                                stopLoginLoading();


                                password.setError(
                                        "Incorrect password"
                                );

                                password.requestFocus();


                                // Show keyboard again
                                password.postDelayed(
                                        () -> {

                                            InputMethodManager imm =
                                                    (InputMethodManager)
                                                            getSystemService(
                                                                    Context.INPUT_METHOD_SERVICE
                                                            );


                                            if (
                                                    imm != null
                                            ) {

                                                imm.showSoftInput(
                                                        password,
                                                        InputMethodManager
                                                                .SHOW_IMPLICIT
                                                );
                                            }

                                        },
                                        150
                                );
                            }
                        }
                );
    }


    // ==========================================
    // START THREE DOT LOADING
    // ==========================================

    private void startLoginLoading() {

        loginButton.setEnabled(false);

        loginButton.setVisibility(
                View.GONE
        );

        loginProgress.setVisibility(
                View.VISIBLE
        );

        animateDots();
    }


    // ==========================================
    // STOP THREE DOT LOADING
    // ==========================================

    private void stopLoginLoading() {

        loginButton.setEnabled(true);

        loginButton.setVisibility(
                View.VISIBLE
        );

        loginProgress.setVisibility(
                View.GONE
        );
    }


    // ==========================================
    // THREE DOT ANIMATION
    // ==========================================

    private void animateDots() {

        dot1.animate()
                .alpha(0.3f)
                .setDuration(300)
                .withEndAction(() -> {

                    dot1.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();

                })
                .start();


        dot2.animate()
                .alpha(0.3f)
                .setStartDelay(200)
                .setDuration(300)
                .withEndAction(() -> {

                    dot2.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();

                })
                .start();


        dot3.animate()
                .alpha(0.3f)
                .setStartDelay(400)
                .setDuration(300)
                .withEndAction(() -> {

                    dot3.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();

                })
                .start();


        // Repeat animation while login is running
        loginProgress.postDelayed(
                () -> {

                    if (
                            loginProgress.getVisibility()
                                    == View.VISIBLE
                    ) {

                        animateDots();
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


        if (
                imm != null &&
                        getCurrentFocus() != null
        ) {

            imm.hideSoftInputFromWindow(
                    getCurrentFocus()
                            .getWindowToken(),
                    0
            );
        }
    }
}