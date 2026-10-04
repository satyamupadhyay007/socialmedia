package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private RecyclerView recyclerNotifications;
    private TextView tvFollowRequests;

    private final List<NotificationItem> notificationList = new ArrayList<>();
    private final List<NotificationItem> requestList = new ArrayList<>();

    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_notifications);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        recyclerNotifications = findViewById(R.id.notificationsRecyclerView);
        tvFollowRequests = findViewById(R.id.tvFollowRequests);

        ImageButton btnBack = findViewById(R.id.btnNotificationsBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerNotifications.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new NotificationAdapter(notificationList);
        recyclerNotifications.setAdapter(adapter);

        tvFollowRequests.setOnClickListener(v -> {
            Intent intent = new Intent(
                    NotificationsActivity.this,
                    FollowRequestsActivity.class
            );
            startActivity(intent);
        });

        setupInsets();
        loadNotifications();
        loadRequestCount();
    }

    private void setupInsets() {
        View root = findViewById(R.id.notificationsRoot);
        View header = findViewById(R.id.notificationsHeader);

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, insets) -> {

                    Insets bars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.displayCutout()
                    );

                    header.setPadding(
                            header.getPaddingLeft(),
                            bars.top,
                            header.getPaddingRight(),
                            header.getPaddingBottom()
                    );

                    recyclerNotifications.setPadding(
                            recyclerNotifications.getPaddingLeft(),
                            recyclerNotifications.getPaddingTop(),
                            recyclerNotifications.getPaddingRight(),
                            bars.bottom
                    );

                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(root);
    }

    private void loadRequestCount() {

        String uid = auth.getCurrentUser().getUid();

        db.collection("relationships")
                .whereEqualTo("followingId", uid)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(snapshot -> {

                    int count = snapshot.size();

                    if (count > 0) {
                        tvFollowRequests.setText(
                                "Follow Requests  •  " + count
                        );
                    } else {
                        tvFollowRequests.setText(
                                "Follow Requests"
                        );
                    }
                })
                .addOnFailureListener(e ->
                        tvFollowRequests.setText("Follow Requests")
                );
    }

    private void loadNotifications() {

        String uid = auth.getCurrentUser().getUid();

        notificationList.clear();
        adapter.notifyDataSetChanged();

        db.collection("notifications")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot document : snapshot.getDocuments()) {

                        String notificationId = document.getId();
                        String fromUserId = document.getString("fromUserId");
                        String type = document.getString("type");
                        String message = document.getString("message");
                        String postId = document.getString("postId");
                        Boolean readValue = document.getBoolean("read");

                        if (fromUserId == null ||
                                fromUserId.trim().isEmpty()) {
                            continue;
                        }

                        boolean read =
                                readValue != null && readValue;

                        NotificationItem item =
                                new NotificationItem(
                                        notificationId,
                                        fromUserId,
                                        type,
                                        message,
                                        postId,
                                        read
                                );

                        notificationList.add(item);
                    }

                    // Follow requests are shown through the
                    // dedicated Follow Requests section.
                    requestList.clear();

                    for (NotificationItem item : notificationList) {
                        if ("follow_request".equals(item.type)) {
                            requestList.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    loadNotificationUsers();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Unable to load notifications",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void loadNotificationUsers() {

        for (int i = 0; i < notificationList.size(); i++) {

            final int index = i;
            NotificationItem item = notificationList.get(i);

            db.collection("users")
                    .document(item.fromUserId)
                    .get()
                    .addOnSuccessListener(document -> {

                        if (!document.exists()) {
                            return;
                        }

                        String username =
                                document.getString("userId");

                        if (username == null ||
                                username.trim().isEmpty()) {
                            username =
                                    document.getString("username");
                        }

                        item.username = username;
                        item.name = document.getString("name");
                        item.profileImage =
                                document.getString("profileImage");

                        adapter.notifyItemChanged(index);
                    });
        }
    }

    private void markAsRead(NotificationItem item) {

        if (item.read) {
            return;
        }

        db.collection("notifications")
                .document(item.notificationId)
                .update("read", true);

        item.read = true;
    }

    private static class NotificationItem {

        String notificationId;
        String fromUserId;
        String type;
        String message;
        String postId;

        boolean read;

        String username;
        String name;
        String profileImage;

        NotificationItem(
                String notificationId,
                String fromUserId,
                String type,
                String message,
                String postId,
                boolean read
        ) {
            this.notificationId = notificationId;
            this.fromUserId = fromUserId;
            this.type = type;
            this.message = message;
            this.postId = postId;
            this.read = read;
        }
    }

    private class NotificationAdapter
            extends RecyclerView.Adapter<
            NotificationAdapter.NotificationViewHolder> {

        private final List<NotificationItem> list;

        NotificationAdapter(List<NotificationItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(
                @NonNull android.view.ViewGroup parent,
                int viewType
        ) {

            View view = getLayoutInflater().inflate(
                    R.layout.item_notification,
                    parent,
                    false
            );

            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                @NonNull NotificationViewHolder holder,
                int position
        ) {

            NotificationItem item = list.get(position);

            String username = item.username;

            if (username == null ||
                    username.trim().isEmpty()) {
                username = "User";
            }

            holder.username.setText("@" + username);

            String message = item.message;

            if (message == null ||
                    message.trim().isEmpty()) {

                if ("follow_request".equals(item.type)) {
                    message = "requested to follow you";
                } else if ("follow_accepted".equals(item.type)) {
                    message = "accepted your follow request";
                } else {
                    message = "started following you";
                }
            }

            holder.message.setText(message);

            if (item.profileImage != null &&
                    !item.profileImage.trim().isEmpty()) {

                Glide.with(holder.image.getContext())
                        .load(item.profileImage)
                        .circleCrop()
                        .placeholder(
                                android.R.drawable.ic_menu_gallery
                        )
                        .error(
                                android.R.drawable.ic_menu_gallery
                        )
                        .into(holder.image);

            } else {

                holder.image.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }

            if ("follow_request".equals(item.type)) {

                holder.requestButton.setVisibility(View.VISIBLE);
                holder.requestButton.setText("Review");

                holder.requestButton.setOnClickListener(v -> {

                    Intent intent = new Intent(
                            NotificationsActivity.this,
                            FollowRequestsActivity.class
                    );

                    startActivity(intent);
                    markAsRead(item);
                });

            } else {

                holder.requestButton.setVisibility(View.GONE);
                holder.requestButton.setOnClickListener(null);
            }

            holder.itemView.setOnClickListener(v -> {

                if ("follow_request".equals(item.type)) {

                    Intent intent = new Intent(
                            NotificationsActivity.this,
                            FollowRequestsActivity.class
                    );

                    startActivity(intent);

                } else if (item.postId != null && !item.postId.trim().isEmpty()) {

                    Intent intent = new Intent(
                            NotificationsActivity.this,
                            PostDetailActivity.class
                    );

                    intent.putExtra(
                            "postId",
                            item.postId
                    );

                    startActivity(intent);

                } else {

                    Intent intent = new Intent(
                            NotificationsActivity.this,
                            UserProfileActivity.class
                    );

                    intent.putExtra(
                            "userId",
                            item.fromUserId
                    );

                    startActivity(intent);
                }

                markAsRead(item);
            });

            holder.itemView.setAlpha(
                    item.read ? 0.65f : 1.0f
            );
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class NotificationViewHolder
                extends RecyclerView.ViewHolder {

            ImageView image;
            TextView username;
            TextView message;
            TextView requestButton;

            NotificationViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(
                        R.id.notificationUserImage
                );

                username = itemView.findViewById(
                        R.id.notificationUsername
                );

                message = itemView.findViewById(
                        R.id.notificationMessage
                );

                requestButton = itemView.findViewById(
                        R.id.notificationAction
                );
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auth != null &&
                auth.getCurrentUser() != null) {

            loadNotifications();
            loadRequestCount();
        }
    }
}
