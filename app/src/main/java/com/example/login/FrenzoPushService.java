package com.example.login;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;

public class FrenzoPushService extends Service {

    private ListenerRegistration chatListener;
    private ListenerRegistration notificationListener;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private int lastMessageSum = -1;
    private final long serviceStartTime = System.currentTimeMillis();

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        SystemNotificationHelper.createNotificationChannel(this);
        startListening();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startListening();
        return START_STICKY;
    }

    private void startListening() {
        if (auth.getCurrentUser() == null) return;
        String currentUid = auth.getCurrentUser().getUid();
        String unreadField = "unread_" + currentUid;

        // 1. Listen for new chat messages
        if (chatListener == null) {
            chatListener = db.collection("chats")
                    .addSnapshotListener((snapshot, error) -> {
                        if (error != null || snapshot == null) return;

                        int currentUnreadSum = 0;
                        String lastMessageText = "";

                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Long unread = doc.getLong(unreadField);
                            if (unread != null && unread > 0) {
                                currentUnreadSum += unread.intValue();
                                String msg = doc.getString("lastMessage");
                                if (msg != null && !msg.isEmpty()) {
                                    lastMessageText = msg;
                                }
                            }
                        }

                        if (lastMessageSum != -1 && currentUnreadSum > lastMessageSum) {
                            // Trigger System Notification for new message
                            Intent intent = new Intent(this, ChatListActivity.class);
                            SystemNotificationHelper.showSystemNotification(
                                    this,
                                    1001,
                                    "💬 New Message on Frenzo",
                                    lastMessageText.isEmpty() ? "You received a new message" : lastMessageText,
                                    intent
                            );
                        }
                        lastMessageSum = currentUnreadSum;
                    });
        }

        // 2. Listen for likes, comments, replies, follow requests
        if (notificationListener == null) {
            notificationListener = db.collection("notifications")
                    .whereEqualTo("toUserId", currentUid)
                    .addSnapshotListener((snapshot, error) -> {
                        if (error != null || snapshot == null) return;

                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Boolean read = doc.getBoolean("read");
                            Long createdAt = doc.getLong("createdAt");

                            if ((read == null || !read) && createdAt != null && createdAt > serviceStartTime) {
                                String type = doc.getString("type");
                                String message = doc.getString("message");
                                String postId = doc.getString("postId");

                                Intent targetIntent = getNotificationIntent(type, postId);

                                String title = "Frenzo";
                                if (type != null && type.contains("like")) {
                                    title = "❤️ New Like";
                                } else if (type != null && type.contains("comment")) {
                                    title = "💬 New Comment";
                                } else if (type != null && type.contains("follow")) {
                                    title = "👤 New Follow";
                                }

                                SystemNotificationHelper.showSystemNotification(
                                        this,
                                        (int) (System.currentTimeMillis() % 10000),
                                        title,
                                        Objects.requireNonNullElse(message, "New activity on your profile"),
                                        targetIntent
                                );
                            }
                        }
                    });
        }
    }

    private Intent getNotificationIntent(String type, String postId) {
        if (postId != null && !postId.trim().isEmpty()) {
            Intent intent = new Intent(this, PostDetailActivity.class);
            intent.putExtra("postId", postId);
            return intent;
        } else if ("follow_request".equals(type)) {
            return new Intent(this, FollowRequestsActivity.class);
        } else {
            return new Intent(this, NotificationsActivity.class);
        }
    }

    @Override
    public void onDestroy() {
        if (chatListener != null) chatListener.remove();
        if (notificationListener != null) notificationListener.remove();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
