package com.example.login;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {

    public static void createNotification(
            String toUserId,
            String fromUserId,
            String fromUsername,
            String type,
            String message,
            String postId
    ) {
        if (toUserId == null || fromUserId == null || toUserId.isEmpty() || fromUserId.isEmpty()) {
            return;
        }

        // Do not notify yourself
        if (toUserId.equals(fromUserId)) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (fromUsername == null || fromUsername.trim().isEmpty()) {
            db.collection("users").document(fromUserId).get()
                    .addOnSuccessListener(document -> {
                        String username = document.getString("userId");
                        if (username == null || username.isEmpty()) {
                            username = document.getString("username");
                        }
                        if (username == null || username.isEmpty()) {
                            username = "Someone";
                        }
                        sendNotificationDoc(db, toUserId, fromUserId, username, type, message, postId);
                    })
                    .addOnFailureListener(e ->
                            sendNotificationDoc(db, toUserId, fromUserId, "Someone", type, message, postId)
                    );
        } else {
            sendNotificationDoc(db, toUserId, fromUserId, fromUsername, type, message, postId);
        }
    }

    private static void sendNotificationDoc(
            FirebaseFirestore db,
            String toUserId,
            String fromUserId,
            String fromUsername,
            String type,
            String message,
            String postId
    ) {
        String fullMessage;
        if (message != null && message.contains(fromUsername)) {
            fullMessage = message;
        } else {
            fullMessage = fromUsername + " " + (message != null ? message : "");
        }

        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", toUserId);
        notification.put("toUserId", toUserId);
        notification.put("fromUserId", fromUserId);
        notification.put("fromUsername", fromUsername);
        notification.put("type", type);
        notification.put("message", fullMessage.trim());
        if (postId != null && !postId.trim().isEmpty()) {
            notification.put("postId", postId);
        }
        notification.put("read", false);
        notification.put("createdAt", System.currentTimeMillis());
        notification.put("timestamp", System.currentTimeMillis());

        db.collection("notifications").add(notification);
    }
}
