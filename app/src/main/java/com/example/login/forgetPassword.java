package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Connect XML views
        recoveryEmail = findViewById(R.id.recoveryEmail);
        recoverButton = findViewById(R.id.recover);
        loginButton = findViewById(R.id.gotologin);


        // =========================
        // RECOVER PASSWORD
        // =========================

        recoverButton.setOnClickListener(v -> {

            String email = recoveryEmail.getText()
                    .toString()
                    .trim();

            // Check empty email
            if (email.isEmpty()) {

                recoveryEmail.setError("Enter your registered email");
                recoveryEmail.requestFocus();
                return;
            }

            // Basic email validation
            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()) {

                recoveryEmail.setError("Enter a valid email");
                recoveryEmail.requestFocus();
                return;
            }


            // Send password reset email
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    forgetPassword.this,
                                    "Password reset email sent. Check your email.",
                                    Toast.LENGTH_LONG
                            ).show();

                        } else {

                            String errorMessage =
                                    "Unable to send reset email.";

                            if (task.getException() != null) {
                                errorMessage =
                                        task.getException().getMessage();
                            }

                            Toast.makeText(
                                    forgetPassword.this,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });


        // =========================
        // BACK TO LOGIN
        // =========================

        loginButton.setOnClickListener(v -> {

            Intent loginIntent = new Intent(
                    forgetPassword.this,
                    login.class
            );

            startActivity(loginIntent);
            finish();
        });


        // =========================
        // EDGE TO EDGE
        // =========================

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
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
}