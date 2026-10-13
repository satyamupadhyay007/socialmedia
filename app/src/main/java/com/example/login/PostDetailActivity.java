package com.example.login;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private RecyclerView detailRecyclerView;

    private PostDetailAdapter postDetailAdapter;
    private final List<Post> postList = new ArrayList<>();

    private FirebaseFirestore db;

    private String targetPostId;
    private String filterUserId;
    private String filterMediaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_detail);

        View root = findViewById(R.id.postDetailRoot);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
                Insets bars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                                | WindowInsetsCompat.Type.displayCutout()
                );
                view.setPadding(
                        bars.left,
                        bars.top,
                        bars.right,
                        bars.bottom
                );
                return insets;
            });
        }

        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        detailRecyclerView = findViewById(R.id.detailRecyclerView);

        targetPostId = getIntent().getStringExtra("postId");
        filterUserId = getIntent().getStringExtra("userId");
        filterMediaType = getIntent().getStringExtra("mediaType");

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if ("video".equalsIgnoreCase(filterMediaType)) {
            if (tvTitle != null) tvTitle.setText("Reels");
        } else {
            if (tvTitle != null) tvTitle.setText("Explore");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        detailRecyclerView.setLayoutManager(layoutManager);

        postDetailAdapter = new PostDetailAdapter(postList);
        detailRecyclerView.setAdapter(postDetailAdapter);

        loadPosts();
    }

    private void loadPosts() {
        if (filterUserId != null && !filterUserId.trim().isEmpty()) {
            db.collection("posts")
                    .whereEqualTo("userId", filterUserId)
                    .get()
                    .addOnSuccessListener(snapshot -> processPosts(snapshot.getDocuments()))
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to load posts", Toast.LENGTH_SHORT).show());
        } else {
            db.collection("posts")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(snapshot -> processPosts(snapshot.getDocuments()))
                    .addOnFailureListener(e -> loadPostsFallback());
        }
    }

    private void loadPostsFallback() {
        db.collection("posts")
                .get()
                .addOnSuccessListener(snapshot -> processPosts(snapshot.getDocuments()))
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading posts", Toast.LENGTH_SHORT).show());
    }

    private void processPosts(List<DocumentSnapshot> documents) {
        postList.clear();
        int targetPosition = 0;

        if (documents != null) {
            for (DocumentSnapshot doc : documents) {
                try {
                    Post post = doc.toObject(Post.class);
                    if (post == null) continue;

                    if (post.getPostId() == null || post.getPostId().trim().isEmpty()) {
                        post.setPostId(doc.getId());
                    }

                    String mType = post.getMediaType();
                    if (mType == null || mType.trim().isEmpty()) {
                        post.setMediaType("image");
                    }

                    // Optional mediaType filter (e.g. video only if Reels tab)
                    if (filterMediaType != null && !filterMediaType.trim().isEmpty()) {
                        if (!filterMediaType.equalsIgnoreCase(post.getMediaType())) {
                            continue;
                        }
                    }

                    postList.add(post);

                    if (targetPostId != null && targetPostId.equals(post.getPostId())) {
                        targetPosition = postList.size() - 1;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        postDetailAdapter.updateList(postList);

        if (targetPosition >= 0 && targetPosition < postList.size()) {
            final int pos = targetPosition;
            detailRecyclerView.post(() -> {
                if (detailRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    ((LinearLayoutManager) detailRecyclerView.getLayoutManager())
                            .scrollToPositionWithOffset(pos, 0);
                }
            });
        }
    }
}
