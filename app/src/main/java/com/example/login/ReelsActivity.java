package com.example.login;

import android.os.Bundle;

public class ReelsActivity extends PostDetailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent() != null && getIntent().getStringExtra("mediaType") == null) {
            getIntent().putExtra("mediaType", "video");
        }
        super.onCreate(savedInstanceState);
    }
}
