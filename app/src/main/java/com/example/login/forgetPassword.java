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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class forgetPassword extends AppCompatActivity {

    EditText recoveryEmail;

    Button recoverButton;
    Button loginButton;

    LinearLayout recoveryProgress;

    TextView recoveryDot1;
    TextView recoveryDot2;
    TextView recoveryDot3;

    TextView recoveryMessage;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_forget_password
        );

        // ==========================================
        // FIREBASE
        // ==========================================

        mAuth = FirebaseAuth.getInstance();

        // ==========================================
        // CONNECT XML
        // ==========================================

        recoveryEmail = findViewById(
                R.id.recoveryEmail
        );

        recoverButton = findViewById(
                R.id.recover
        );

        loginButton = findViewById(
                R.id.gotologin
        );

        // ==========================================
        // PROGRESS LOADER
        // ==========================================

        recoveryProgress = findViewById(
                R.id.recoveryProgress
        );

        recoveryDot1 = findViewById(
                R.id.recoveryDot1
        );

        recoveryDot2 = findViewById(
                R.id.recoveryDot2
        );

        recoveryDot3 = findViewById(
                R.id.recoveryDot3
        );

        // ==========================================
        // MESSAGE
        // ==========================================

        recoveryMessage = findViewById(
                R.id.recoveryMessage
        );


        // ==========================================
        // RECOVER PASSWORD
        // ==========================================

        recoverButton.setOnClickListener(v -> {

            // Hide keyboard
            hideKeyboard();

            // Clear old error/message
            recoveryEmail.setError(null);

            recoveryMessage.setText("");

            recoveryMessage.setVisibility(
                    View.GONE
            );

            String email =
                    recoveryEmail
                            .getText()
                            .toString()
                            .trim();


            // ==========================================
            // EMPTY EMAIL
            // ==========================================

            if (email.isEmpty()) {

                recoveryEmail.setError(
                        "Enter your registered email"
                );

                recoveryEmail.requestFocus();

                return;
            }


            // ==========================================
            // EMAIL VALIDATION
            // ==========================================

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()) {

                recoveryEmail.setError(
                        "Enter a valid email"
                );

                recoveryEmail.requestFocus();

                return;
            }


            // ==========================================
            // START LOADING
            // ==========================================

            startRecoveryLoading();


            // ==========================================
            // SEND PASSWORD RESET EMAIL
            // ==========================================

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {

                        // ==================================
                        // SUCCESS
                        // ==================================

                        if (task.isSuccessful()) {

                            stopRecoveryLoading();

                            showMessage(
                                    "Password reset link has been sent to your email."
                            );

                        } else {

                            stopRecoveryLoading();

                            String errorMessage =
                                    "Unable to send reset email.";

                            if (task.getException() != null) {

                                String firebaseError =
                                        task.getException()
                                                .getMessage();

                                if (firebaseError != null &&
                                        !firebaseError.isEmpty()) {

                                    errorMessage =
                                            firebaseError;
                                }
                            }

                            showMessage(
                                    errorMessage
                            );
                        }
                    });
        });


        // ==========================================
        // BACK TO LOGIN
        // ==========================================

        loginButton.setOnClickListener(v -> {

            // Hide keyboard
            hideKeyboard();

            Intent loginIntent =
                    new Intent(
                            forgetPassword.this,
                            login.class
                    );

            startActivity(loginIntent);

            finish();
        });


        // ==========================================
        // EDGE TO EDGE
        // ==========================================

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
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
    // START RECOVERY LOADING
    // ==========================================

    private void startRecoveryLoading() {

        recoverButton.setEnabled(false);

        recoverButton.setVisibility(
                View.GONE
        );

        recoveryProgress.setVisibility(
                View.VISIBLE
        );

        animateRecoveryDots();
    }


    // ==========================================
    // STOP RECOVERY LOADING
    // ==========================================

    private void stopRecoveryLoading() {

        recoverButton.setEnabled(true);

        recoverButton.setVisibility(
                View.VISIBLE
        );

        recoveryProgress.setVisibility(
                View.GONE
        );
    }


    // ==========================================
    // THREE DOT ANIMATION
    // ==========================================

    private void animateRecoveryDots() {

        recoveryDot1.animate()
                .alpha(0.3f)
                .setDuration(300)
                .withEndAction(() -> recoveryDot1.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start())
                .start();


        recoveryDot2.animate()
                .alpha(0.3f)
                .setStartDelay(200)
                .setDuration(300)
                .withEndAction(() -> recoveryDot2.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start())
                .start();


        recoveryDot3.animate()
                .alpha(0.3f)
                .setStartDelay(400)
                .setDuration(300)
                .withEndAction(() -> recoveryDot3.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start())
                .start();


        // Repeat while loading
        recoveryProgress.postDelayed(
                () -> {

                    if (recoveryProgress.getVisibility()
                            == View.VISIBLE) {

                        animateRecoveryDots();
                    }

                },
                900
        );
    }


    // ==========================================
    // SHOW SCREEN MESSAGE
    // ==========================================

    private void showMessage(
            String message) {

        recoveryMessage.setText(
                message
        );

        recoveryMessage.setVisibility(
                View.VISIBLE
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