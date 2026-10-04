package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowRequestsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private RecyclerView recyclerView;

    private final List<FollowRequest> requestList =
            new ArrayList<>();

    private RequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_follow_requests);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }

        recyclerView =
                findViewById(R.id.followRequestsRecyclerView);

        ImageButton btnBack =
                findViewById(R.id.btnFollowRequestsBack);

        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new RequestAdapter(requestList);
        recyclerView.setAdapter(adapter);

        setupInsets();
        loadRequests();
    }

    private void setupInsets() {

        View root =
                findViewById(R.id.followRequestsRoot);

        View header =
                findViewById(R.id.followRequestsHeader);

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, insets) -> {

                    Insets bars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                                            | WindowInsetsCompat.Type.displayCutout()
                            );

                    header.setPadding(
                            header.getPaddingLeft(),
                            bars.top,
                            header.getPaddingRight(),
                            header.getPaddingBottom()
                    );

                    recyclerView.setPadding(
                            recyclerView.getPaddingLeft(),
                            recyclerView.getPaddingTop(),
                            recyclerView.getPaddingRight(),
                            bars.bottom
                    );

                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(root);
    }

    private void loadRequests() {

        String uid =
                auth.getCurrentUser().getUid();

        requestList.clear();
        adapter.notifyDataSetChanged();

        db.collection("relationships")
                .whereEqualTo("followingId", uid)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        String followerId =
                                document.getString("followerId");

                        if (followerId == null ||
                                followerId.trim().isEmpty()) {
                            continue;
                        }

                        requestList.add(
                                new FollowRequest(
                                        document.getId(),
                                        followerId
                                )
                        );
                    }

                    adapter.notifyDataSetChanged();
                    loadUsers();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Unable to load requests",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void loadUsers() {

        for (int i = 0; i < requestList.size(); i++) {

            final int index = i;
            FollowRequest request =
                    requestList.get(i);

            db.collection("users")
                    .document(request.followerId)
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

                        request.username = username;
                        request.name =
                                document.getString("name");
                        request.profileImage =
                                document.getString("profileImage");

                        adapter.notifyItemChanged(index);
                    });
        }
    }

    private void acceptRequest(FollowRequest request) {

        String currentUid =
                auth.getCurrentUser().getUid();

        db.collection("relationships")
                .document(request.relationshipId)
                .update(
                        "status",
                        "accepted"
                )
                .addOnSuccessListener(unused -> {

                    createAcceptedNotification(
                            request.followerId,
                            currentUid
                    );

                    Toast.makeText(
                            this,
                            "Request accepted",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadRequests();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Unable to accept request",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void deleteRequest(FollowRequest request) {

        db.collection("relationships")
                .document(request.relationshipId)
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Request deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadRequests();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Unable to delete request",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void createAcceptedNotification(
            String receiverUserId,
            String senderUserId
    ) {

        Map<String, Object> notification =
                new HashMap<>();

        notification.put(
                "userId",
                receiverUserId
        );

        notification.put(
                "fromUserId",
                senderUserId
        );

        notification.put(
                "type",
                "follow_accepted"
        );

        notification.put(
                "message",
                "accepted your follow request"
        );

        notification.put(
                "read",
                false
        );

        notification.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        db.collection("notifications")
                .add(notification);
    }

    private static class FollowRequest {

        String relationshipId;
        String followerId;

        String username;
        String name;
        String profileImage;

        FollowRequest(
                String relationshipId,
                String followerId
        ) {
            this.relationshipId = relationshipId;
            this.followerId = followerId;
        }
    }

    private class RequestAdapter
            extends RecyclerView.Adapter<
            RequestAdapter.RequestViewHolder> {

        private final List<FollowRequest> list;

        RequestAdapter(List<FollowRequest> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(
                @NonNull android.view.ViewGroup parent,
                int viewType
        ) {

            View view =
                    getLayoutInflater().inflate(
                            R.layout.item_follow_request,
                            parent,
                            false
                    );

            return new RequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                @NonNull RequestViewHolder holder,
                int position
        ) {

            FollowRequest request =
                    list.get(position);

            String username = request.username;

            if (username == null ||
                    username.trim().isEmpty()) {
                username = "User";
            }

            holder.username.setText(
                    "@" + username
            );

            holder.name.setText(
                    request.name == null
                            ? ""
                            : request.name
            );

            if (request.profileImage != null &&
                    !request.profileImage.trim().isEmpty()) {

                Glide.with(holder.image.getContext())
                        .load(request.profileImage)
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

            holder.itemView.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                FollowRequestsActivity.this,
                                UserProfileActivity.class
                        );

                intent.putExtra(
                        "userId",
                        request.followerId
                );

                startActivity(intent);
            });

            holder.confirm.setOnClickListener(
                    v -> acceptRequest(request)
            );

            holder.delete.setOnClickListener(
                    v -> deleteRequest(request)
            );
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RequestViewHolder
                extends RecyclerView.ViewHolder {

            ImageView image;
            TextView username;
            TextView name;
            Button confirm;
            Button delete;

            RequestViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(
                        R.id.requestUserImage
                );

                username = itemView.findViewById(
                        R.id.requestUsername
                );

                name = itemView.findViewById(
                        R.id.requestName
                );

                confirm = itemView.findViewById(
                        R.id.btnConfirmRequest
                );

                delete = itemView.findViewById(
                        R.id.btnDeleteRequest
                );
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auth != null &&
                auth.getCurrentUser() != null) {
            loadRequests();
        }
    }
}
