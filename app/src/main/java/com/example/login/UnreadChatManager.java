package com.example.login;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;

public class UnreadChatManager {

    private static ListenerRegistration listenerRegistration;
    private static int lastUnreadSum = -1;

    public static void startListening(Activity activity, TextView badgeView) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            if (badgeView != null) badgeView.setVisibility(View.GONE);
            return;
        }

        String currentUid = auth.getCurrentUser().getUid();
        String unreadField = "unread_" + currentUid;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        listenerRegistration = db.collection("chats")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || activity == null || activity.isFinishing()) {
                        return;
                    }

                    int currentUnreadChats = 0;
                    int totalUnreadMessagesSum = 0;
                    String latestMessageText = "";

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Long unread = doc.getLong(unreadField);
                        if (unread != null && unread > 0) {
                            currentUnreadChats++;
                            totalUnreadMessagesSum += unread.intValue();

                            String lastMsg = doc.getString("lastMessage");
                            if (lastMsg != null && !lastMsg.isEmpty()) {
                                latestMessageText = lastMsg;
                            }
                        }
                    }

                    // Update Bottom Nav Badge
                    if (badgeView != null) {
                        if (currentUnreadChats > 0) {
                            badgeView.setVisibility(View.VISIBLE);
                            badgeView.setText(currentUnreadChats > 9 ? "9+" : "" + currentUnreadChats);
                        } else {
                            badgeView.setVisibility(View.GONE);
                        }
                    }

                    // In-app Popup Toast when new unread message arrives
                    if (lastUnreadSum != -1 && totalUnreadMessagesSum > lastUnreadSum) {
                        if (!Objects.equals(activity.getClass().getSimpleName(), "ChatActivity")) {
                            String toastText = "💬 New Message: " + (latestMessageText.isEmpty() ? "1 unread message" : latestMessageText);
                            Toast.makeText(
                                    activity.getApplicationContext(),
                                    toastText,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    lastUnreadSum = totalUnreadMessagesSum;
                });
    }

    public static void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}
