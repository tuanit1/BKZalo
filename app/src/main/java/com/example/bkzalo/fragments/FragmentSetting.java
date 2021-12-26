/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:15 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 11:41 PM
 * /
 */

package com.example.bkzalo.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.bkzalo.R;
import com.example.bkzalo.activitiy.AuthorActivity;
import com.example.bkzalo.activitiy.EditProfileActivity;
import com.example.bkzalo.activitiy.MemberListActivity;
import com.example.bkzalo.activitiy.PrivacyActivity;
import com.example.bkzalo.activitiy.ResetPassActivity;
import com.example.bkzalo.activitiy.LoginActivity;
import com.example.bkzalo.asynctasks.GetUIDAsync;
import com.example.bkzalo.listeners.GetUIDListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import io.grpc.inprocess.InternalInProcess;
import okhttp3.RequestBody;

public class FragmentSetting extends Fragment {

    private View view;
    private SwitchCompat switch_darkmode;
    private LinearLayoutCompat signout, changepass, privacy, author, ll_edit_profile;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private SharedPreferences preferences2;
    private TextView tv_name;
    private RoundedImageView imv_user_setting;
    private Methods methods;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        preferences2 = getContext().getSharedPreferences("dataLogin", MODE_PRIVATE);
        methods = new Methods(getContext());

        AnhXa();

        if (Constant.mGoogleSignInClient!=null)
        {
            String email = firebaseUser.getProviderData().get(1).getEmail();
            GetUser(email, methods);
        }
        return view;
    }

    private void AnhXa()
    {
        switch_darkmode = view.findViewById(R.id.switch_darkmode);

        tv_name = view.findViewById(R.id.tv_name_setting);
        tv_name.setText(Constant.NAME);

        privacy =  view.findViewById(R.id.linear_privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PrivacyActivity.class);
                startActivity(intent);
            }
        });

        author =  view.findViewById(R.id.linear_author);
        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AuthorActivity.class);
                startActivity(intent);
            }
        });

        signout = view.findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });

        changepass = view.findViewById(R.id.layout_changepass);
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ResetPassActivity.class);
                startActivity(intent);
            }
        });

        ll_edit_profile = view.findViewById(R.id.ll_edit_profile);
        ll_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        imv_user_setting = view.findViewById(R.id.imv_user_setting);
        String image_path = Constant.SERVER_URL + "image/image_user/" + Constant.IMAGE;

        Picasso.get()
                .load(image_path)
                .placeholder(R.drawable.message_placeholder_ic)
                .into(imv_user_setting);
    }

    private void SignOut()
    {
        if(firebaseUser != null)
        {
            if (Constant.mGoogleSignInClient!=null)
            {
                Constant.mGoogleSignInClient.signOut();
            }

            if (LoginManager.getInstance()!=null)
            {
                LoginManager.getInstance().logOut();
            }

            FirebaseAuth.getInstance().signOut();
            SharedPreferences.Editor editor = preferences2.edit();
            editor.putBoolean("isLogin", false);
            editor.putBoolean("isLoginFB", false);
            editor.putBoolean("isLoginGG", false);
            editor.commit();
        }
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void GetUser(String email, Methods methods) {

        Bundle bundle = new Bundle();
        bundle.putString("email", email);

        RequestBody requestBody = methods.getRequestBody("method_get_user", bundle, null);

        GetUIDListener listener = new GetUIDListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status, User user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Constant.UID = user.getId();
                        Constant.PHONE = user.getPhone();
                        Constant.NAME = user.getName();
                        Constant.IMAGE = user.getImage();
                    }else {
                        Constant.UID = 0;
                    }
                }else{
                }
            }
        };

        GetUIDAsync getUIDAsync = new GetUIDAsync(requestBody, listener);
        getUIDAsync.execute();
    }


}