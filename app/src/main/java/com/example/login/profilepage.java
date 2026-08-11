package com.example.login;

import android.os.Bundle;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class profilepage extends AppCompatActivity {

    RecyclerView recyclerPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        recyclerPosts = findViewById(R.id.recyclerPosts);

        recyclerPosts.setLayoutManager(
                new GridLayoutManager(this, 3)
        );

        List<Integer> images = Arrays.asList(
                R.drawable.post_1,
                R.drawable.post_2,
                R.drawable.post_3,
                R.drawable.post_4,
                R.drawable.post_5,
                R.drawable.post_6,
                R.drawable.post_7,
                R.drawable.post_8,
                R.drawable.post_9
        );

        ProfilePostAdapter adapter = new ProfilePostAdapter(images);

        recyclerPosts.setAdapter(adapter);
    }
}