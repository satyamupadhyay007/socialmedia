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

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signupactivity);

        username = findViewById(R.id.recoveryEmail);
        password = findViewById(R.id.PASSWORD1);
        email = findViewById(R.id.email);

        signupButton = findViewById(R.id.login);
        loginButton = findViewById(R.id.gotologin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // SIGN UP
        signupButton.setOnClickListener(v -> {

            String usernameText = username.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String emailText = email.getText().toString().trim();

            if (usernameText.isEmpty()) {
                username.setError("Enter username");
                username.requestFocus();
                return;
            }

            if (emailText.isEmpty()) {
                email.setError("Enter email");
                email.requestFocus();
                return;
            }

            if (passwordText.isEmpty()) {
                password.setError("Enter password");
                password.requestFocus();
                return;
            }

            if (passwordText.length() < 6) {
                password.setError("Password must be at least 6 characters");
                password.requestFocus();
                return;
            }

            /*
             * Username is case-insensitive.
             * Example:
             * Ashish123
             * ashish123
             * ASHISH123
             *
             * All are treated as the same username.
             */
            String usernameKey = usernameText.toLowerCase();

            // First create Firebase Authentication account
            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {

                        if (!task.isSuccessful()) {

                            String error = "Signup failed.";

                            if (task.getException() != null) {
                                error = "Signup failed: "
                                        + task.getException().getMessage();
                            }

                            Toast.makeText(
                                    signupactivity.this,
                                    error,
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser == null) {
                            Toast.makeText(
                                    signupactivity.this,
                                    "Unable to create user.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        String userId = firebaseUser.getUid();

                        /*
                         * usernames/{usernameKey}
                         *
                         * Example:
                         *
                         * usernames/
                         *     ashish123
                         *          uid: ABC123
                         */
                        db.runTransaction(transaction -> {

                            var usernameRef = db.collection("usernames")
                                    .document(usernameKey);

                            var userRef = db.collection("users")
                                    .document(userId);

                            // Check whether username already exists
                            var usernameSnapshot = transaction.get(usernameRef);

                            if (usernameSnapshot.exists()) {
                                throw new RuntimeException("USERNAME_TAKEN");
                            }

                            // Reserve username
                            Map<String, Object> usernameData =
                                    new HashMap<>();

                            usernameData.put("uid", userId);
                            usernameData.put("username", usernameText);

                            transaction.set(usernameRef, usernameData);

                            // Save user information
                            Map<String, Object> userData =
                                    new HashMap<>();

                            userData.put("username", usernameText);
                            userData.put("email", emailText);

                            transaction.set(userRef, userData);

                            return null;

                        }).addOnSuccessListener(unused -> {

                            Toast.makeText(
                                    signupactivity.this,
                                    "Account created successfully!",
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent = new Intent(
                                    signupactivity.this,
                                    homepage.class
                            );


                            startActivity(intent);
                            finish();

                        }).addOnFailureListener(e -> {

                            /*
                             * Username already exists.
                             *
                             * Delete the Firebase Authentication account
                             * that we just created because the username
                             * cannot be used.
                             */
                            if ("USERNAME_TAKEN".equals(e.getMessage())) {

                                firebaseUser.delete()
                                        .addOnCompleteListener(deleteTask -> {

                                            Toast.makeText(
                                                    signupactivity.this,
                                                    "Username already taken. Please choose another username.",
                                                    Toast.LENGTH_LONG
                                            ).show();

                                            username.requestFocus();
                                        });

                            } else {

                                /*
                                 * Some other Firestore error happened.
                                 */
                                Toast.makeText(
                                        signupactivity.this,
                                        "Could not save user data: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
                    });
        });

        // GO TO LOGIN
        loginButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    signupactivity.this,
                    login.class
            );

            startActivity(intent);
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.newpassword),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
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