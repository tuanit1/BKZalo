/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:13 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/27/21, 4:41 PM
 * /
 */

package com.example.bkzalo.activitiy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bkzalo.R;

public class PrivacyActivity extends AppCompatActivity {

    private TextView tv_privacy;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        tv_privacy = findViewById(R.id.tv_privacy);
        tv_privacy.setText("https://www.freeprivacypolicy.com/live/64effa9b-3704-4552-a801-12a0b18c2ef8");
        Linkify.addLinks(tv_privacy, Linkify.WEB_URLS);

        back = findViewById(R.id.imv_back_privacy);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}