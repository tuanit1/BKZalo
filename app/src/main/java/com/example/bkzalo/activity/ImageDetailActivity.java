package com.example.bkzalo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bkzalo.R;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class ImageDetailActivity extends AppCompatActivity {

    private TextView tv_name, tv_time;
    private ImageView iv_user_image, iv_back, imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Message message = null;

        Intent intent = getIntent();

        if(intent != null){
            message = (Message) intent.getSerializableExtra("message");
        }

        AnhXa(message);
    }

    private void AnhXa(Message msg) {
        tv_name = findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);
        iv_user_image = findViewById(R.id.iv_user_image);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDetailActivity.super.onBackPressed();
            }
        });
        imageview = findViewById(R.id.imageview);

        String img_path = Constant.SERVER_URL + "image/image_user/" + msg.getImage();

        Picasso.get()
                .load(img_path)
                .placeholder(R.drawable.message_placeholder_ic)
                .into(iv_user_image);

        String img_path_msg = Constant.SERVER_URL + "image/image_message/" + msg.getMessage();

        Picasso.get()
                .load(img_path_msg)
                .into(imageview);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");

        String date_txt = formatter.format(msg.getTime());

        tv_time.setText(date_txt);

        tv_name.setText(msg.getName());
    }
}