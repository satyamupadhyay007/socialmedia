package com.example.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        View root = findViewById(R.id.settingsRoot);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
                );
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ================= FIND VIEWS =================

        Button btnAccount = findViewById(R.id.btnAccount);
        Button btnPrivacy = findViewById(R.id.btnPrivacy);
        Button btnSecurity = findViewById(R.id.btnSecurity);
        Button btnNotifications = findViewById(R.id.btnNotifications);
        Button btnHelp = findViewById(R.id.btnHelp);
        Button btnAbout = findViewById(R.id.btnAbout);
        Button btnLogout = findViewById(R.id.btnLogout);

        ImageButton btnBackSettings = findViewById(R.id.btnBackSettings);

        // ================= BACK =================

        if (btnBackSettings != null) {
            btnBackSettings.setOnClickListener(v -> finish());
        }

        // ================= ACCOUNT =================

        if (btnAccount != null) {
            btnAccount.setOnClickListener(v -> showAccountOptions());
        }

        // ================= PRIVACY =================

        if (btnPrivacy != null) {
            btnPrivacy.setOnClickListener(v -> showPrivacyOptions());
        }

        // ================= SECURITY =================

        if (btnSecurity != null) {
            btnSecurity.setOnClickListener(v -> showSecurityOptions());
        }

        // ================= NOTIFICATIONS =================

        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> showNotificationOptions());
        }

        // ================= HELP =================

        if (btnHelp != null) {
            btnHelp.setOnClickListener(v -> showHelpOptions());
        }

        // ================= ABOUT =================

        if (btnAbout != null) {
            btnAbout.setOnClickListener(v -> showAboutDialog());
        }

        // ================= LOGOUT =================

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> confirmLogout());
        }
    }

    // ==================================================
    // ACCOUNT OPTIONS
    // ==================================================

    private void showAccountOptions() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = user.getEmail() != null ? user.getEmail() : "Not available";
        String userUid = user.getUid();

        db.collection("users").document(userUid).get()
                .addOnSuccessListener(document -> {
                    String handle = document.exists() ? document.getString("userId") : null;
                    String altHandle = (handle == null || handle.isEmpty()) && document.exists() ? document.getString("username") : handle;
                    String usernameHandle = (altHandle != null && !altHandle.isEmpty()) ? "@" + altHandle : "Not set";

                    String[] options = {"Edit Profile", "View My Profile", "Account Details (Username & Email)"};

                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Account Settings")
                            .setItems(options, (dialog, which) -> {
                                switch (which) {
                                    case 0: {
                                        Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                                        startActivity(intent);
                                        break;
                                    }
                                    case 1: {
                                        Intent intent = new Intent(SettingsActivity.this, profilepage.class);
                                        startActivity(intent);
                                        break;
                                    }
                                    case 2:
                                        new AlertDialog.Builder(SettingsActivity.this)
                                                .setTitle("Account Information")
                                                .setMessage("Username / User ID: " + usernameHandle + "\n\nEmail: " + userEmail + "\n\nAccount UID: " + userUid)
                                                .setPositiveButton("OK", null)
                                                .show();
                                        break;
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Failed to load account details", Toast.LENGTH_SHORT).show());
    }

    // ==================================================
    // PRIVACY OPTIONS
    // ==================================================

    private void showPrivacyOptions() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    boolean isPrivate = document.exists() && Objects.equals(document.getBoolean("isPrivate"), Boolean.TRUE);

                    String currentStatus = isPrivate ? "Private" : "Public";

                    String[] options = {
                            "Toggle Account Privacy (Currently: " + currentStatus + ")",
                            "Comments & Mentions Privacy",
                            "Blocked Accounts Information"
                    };

                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Privacy Settings")
                            .setItems(options, (dialog, which) -> {
                                switch (which) {
                                    case 0:
                                        toggleAccountPrivacy(uid, !isPrivate);
                                        break;
                                    case 1:
                                        new AlertDialog.Builder(SettingsActivity.this)
                                                .setTitle("Comments & Mentions")
                                                .setMessage("Everyone can comment on and mention your public posts. When your account is private, only your followers can comment.")
                                                .setPositiveButton("OK", null)
                                                .show();
                                        break;
                                    case 2:
                                        new AlertDialog.Builder(SettingsActivity.this)
                                                .setTitle("Blocked Accounts")
                                                .setMessage("When you block someone, they won't be able to see your profile, posts, or stories on Frenzo.")
                                                .setPositiveButton("OK", null)
                                                .show();
                                        break;
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                })
                .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Failed to load privacy settings", Toast.LENGTH_SHORT).show());
    }

    private void toggleAccountPrivacy(String uid, boolean newPrivacyState) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("isPrivate", newPrivacyState);

        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    String statusMsg = newPrivacyState ? "Your account is now Private." : "Your account is now Public.";
                    Toast.makeText(SettingsActivity.this, statusMsg, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Failed to update privacy setting", Toast.LENGTH_SHORT).show());
    }

    // ==================================================
    // SECURITY OPTIONS
    // ==================================================

    private void showSecurityOptions() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = (user != null) ? user.getEmail() : null;

        String[] options = {
                "Send Password Reset Email",
                "Forgot / Change Password Screen",
                "Email Verification Status"
        };

        new AlertDialog.Builder(this)
                .setTitle("Security Settings")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if (userEmail != null && !userEmail.isEmpty()) {
                                mAuth.sendPasswordResetEmail(userEmail)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(SettingsActivity.this, "Password reset link sent to " + userEmail, Toast.LENGTH_LONG).show())
                                        .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Error sending reset email: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            } else {
                                Toast.makeText(SettingsActivity.this, "No email found for this user", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 1: {
                            Intent intent = new Intent(SettingsActivity.this, forgetPassword.class);
                            startActivity(intent);
                            break;
                        }
                        case 2: {
                            boolean isVerified = user != null && user.isEmailVerified();
                            String status = isVerified ? "Verified ✅" : "Not Verified ❌";
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Email Verification")
                                    .setMessage("Your email (" + userEmail + ") is: " + status)
                                    .setPositiveButton("OK", null)
                                    .show();
                            break;
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ==================================================
    // NOTIFICATION OPTIONS
    // ==================================================

    private void showNotificationOptions() {
        String[] options = {
                "View Notifications Center",
                "View Follow Requests",
                "App System Notification Settings"
        };

        new AlertDialog.Builder(this)
                .setTitle("Notification Settings")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: {
                            Intent intent = new Intent(SettingsActivity.this, NotificationsActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case 1: {
                            Intent intent = new Intent(SettingsActivity.this, FollowRequestsActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case 2:
                            try {
                                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(SettingsActivity.this, "Cannot open system notification settings", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ==================================================
    // HELP & SUPPORT
    // ==================================================

    private void showHelpOptions() {
        String[] options = {
                "Report a Problem",
                "Contact Support Team",
                "Frequently Asked Questions (FAQs)"
        };

        new AlertDialog.Builder(this)
                .setTitle("Help & Support")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showReportProblemDialog();
                            break;
                        case 1: {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:support@frenzo.app"));
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Frenzo App Support Request");
                            try {
                                startActivity(Intent.createChooser(intent, "Send email using..."));
                            } catch (Exception e) {
                                Toast.makeText(SettingsActivity.this, "No email client found", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case 2: {
                            String faqText = "1. How do I post photos/videos?\n" +
                                    "Tap the '+' Create icon on the bottom navigation bar.\n\n" +
                                    "2. How do I upload a story?\n" +
                                    "Go to Home page and tap 'Your Story' at the top.\n\n" +
                                    "3. How do I chat with friends?\n" +
                                    "Tap the Message icon or search for a user to start a conversation.";

                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Frenzo FAQs")
                                    .setMessage(faqText)
                                    .setPositiveButton("Close", null)
                                    .show();
                            break;
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showReportProblemDialog() {
        final EditText input = new EditText(this);
        input.setHint("Describe the problem or issue...");
        input.setPadding(40, 30, 40, 30);

        new AlertDialog.Builder(this)
                .setTitle("Report a Problem")
                .setView(input)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String problemText = input.getText().toString().trim();
                    if (!problemText.isEmpty()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Map<String, Object> report = new HashMap<>();
                        report.put("userId", user != null ? user.getUid() : "anonymous");
                        report.put("problem", problemText);
                        report.put("timestamp", System.currentTimeMillis());

                        db.collection("reports").add(report)
                                .addOnSuccessListener(docRef -> Toast.makeText(SettingsActivity.this, "Report submitted successfully! Thank you.", Toast.LENGTH_LONG).show())
                                .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Failed to submit report.", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(SettingsActivity.this, "Please enter problem description", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ==================================================
    // ABOUT DIALOG
    // ==================================================

    private void showAboutDialog() {
        String aboutMessage = "Frenzo App\n" +
                "Version 1.0.0\n\n" +
                "Frenzo is a modern social media platform designed for connecting with friends, sharing photos, reels, stories, and real-time messaging.\n\n" +
                "© 2026 Frenzo Inc. All rights reserved.";

        new AlertDialog.Builder(this)
                .setTitle("About Frenzo")
                .setMessage(aboutMessage)
                .setPositiveButton("OK", null)
                .show();
    }

    // ==================================================
    // LOGOUT
    // ==================================================

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of Frenzo?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        // Stop active background listeners
        UnreadChatManager.stopListening();

        // Sign out from Firebase Authentication
        mAuth.signOut();

        Toast.makeText(
                SettingsActivity.this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        // Redirect to Login Screen and clear backstack
        Intent intent = new Intent(SettingsActivity.this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
