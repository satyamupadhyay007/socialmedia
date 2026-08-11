package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;
    Button signupButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Connect XML views with Java
        username = findViewById(R.id.newusername);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.gotologin);

        // LOGIN BUTTON
        loginButton.setOnClickListener(v -> {

            String email = username.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            // Check empty fields
            if (email.isEmpty()) {
                username.setError("Enter your email");
                username.requestFocus();
                return;
            }

            if (userPassword.isEmpty()) {
                password.setError("Enter your password");
                password.requestFocus();
                return;
            }

            // Firebase Login
            mAuth.signInWithEmailAndPassword(email, userPassword)
                    .addOnCompleteListener(this, task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Login successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // HomeActivity baad mein add karenge
                            // Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            // startActivity(intent);
                            // finish();

                        } else {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Login failed: " +
                                            task.getException().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        // SIGN UP BUTTON
        signupButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    signupactivity.class
            );

            startActivity(intent);
        });
    }
}