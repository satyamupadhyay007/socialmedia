package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect XML views
        username = findViewById(R.id.newusername);
        password = findViewById(R.id.password);

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.createAccount);
        forgetPasswordButton = findViewById(R.id.forgetPassword);


        // =========================
        // LOGIN BUTTON
        // =========================

        loginButton.setOnClickListener(v -> {

            String usernameText =
                    username.getText().toString().trim();

            String userPassword =
                    password.getText().toString().trim();


            // Check username
            if (usernameText.isEmpty()) {

                username.setError("Enter username");
                username.requestFocus();
                return;
            }


            // Check password
            if (userPassword.isEmpty()) {

                password.setError("Enter password");
                password.requestFocus();
                return;
            }


            // Username is case-insensitive
            String usernameKey =
                    usernameText.toLowerCase();


            /*
             * Find username inside:
             *
             * usernames
             *      └── username
             *            └── uid
             */

            db.collection("usernames")
                    .document(usernameKey)
                    .get()
                    .addOnSuccessListener(usernameDocument -> {


                        // Username doesn't exist
                        if (!usernameDocument.exists()) {

                            Toast.makeText(
                                    login.this,
                                    "Username not found",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }


                        // Get Firebase UID
                        String userId =
                                usernameDocument.getString("uid");


                        if (userId == null || userId.isEmpty()) {

                            Toast.makeText(
                                    login.this,
                                    "User data is incomplete",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }


                        /*
                         * Now get the user's profile:
                         *
                         * users
                         *    └── UID
                         *          ├── username
                         *          └── email
                         */

                        db.collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(userDocument -> {


                                    if (!userDocument.exists()) {

                                        Toast.makeText(
                                                login.this,
                                                "User profile not found",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }


                                    // Get email
                                    String email =
                                            userDocument.getString("email");


                                    if (email == null ||
                                            email.isEmpty()) {

                                        Toast.makeText(
                                                login.this,
                                                "Email not found for this user",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }


                                    /*
                                     * Now login using:
                                     *
                                     * Email + Password
                                     *
                                     * Firebase Authentication
                                     */

                                    mAuth.signInWithEmailAndPassword(
                                                    email,
                                                    userPassword
                                            )
                                            .addOnCompleteListener(
                                                    login.this,
                                                    task -> {


                                                        if (task.isSuccessful()) {

                                                            Toast.makeText(
                                                                    login.this,
                                                                    "Login successful!",
                                                                    Toast.LENGTH_SHORT
                                                            ).show();




                                                             Intent intent =
                                                              new Intent(
                                                             login.this,
                                                              homepage.class
                                                              );

                                                             startActivity(intent);
                                                             finish();


                                                        } else {

                                                            Toast.makeText(
                                                                    login.this,
                                                                    "Incorrect password",
                                                                    Toast.LENGTH_LONG
                                                            ).show();
                                                        }
                                                    }
                                            );
                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(
                                            login.this,
                                            "Could not get user data",
                                            Toast.LENGTH_LONG
                                    ).show();
                                });

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                login.this,
                                "Could not check username: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });


        // =========================
        // CREATE ACCOUNT BUTTON
        // =========================

        signupButton.setOnClickListener(v -> {

            Intent signup = new Intent(
                    login.this,
                    signupactivity.class
            );

            startActivity(signup);
        });


        // =========================
        // FORGOT PASSWORD BUTTON
        // =========================

        forgetPasswordButton.setOnClickListener(v -> {

            Intent forgetPass = new Intent(
                    login.this,
                    forgetPassword.class
            );

            startActivity(forgetPass);
        });
    }
}