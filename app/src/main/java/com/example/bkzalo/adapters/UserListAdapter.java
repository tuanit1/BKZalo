/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 5:20 PM
 * /
 */

package com.example.bkzalo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.listeners.ClickItemRequestListener;
import com.example.bkzalo.listeners.ClickItemUserListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder>{

    private ArrayList<User> arrayList_user;
    private ClickItemUserListener listener;
    private Context context;

    public UserListAdapter(ArrayList<User> arrayList_user, ClickItemUserListener listener) {
        this.arrayList_user = arrayList_user;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_userlist, parent, false);
        return new UserListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = arrayList_user.get(position);

        holder.tv_name.setText(user.getName());

        String image_path = user.getImage_url();

        if(!image_path.isEmpty()){
            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.image_user_holder)
                    .error(R.drawable.message_placeholder_ic)
                    .into(holder.iv_user_image);
        }else{
            Picasso.get()
                    .load(R.drawable.message_placeholder_ic)
                    .into(holder.iv_user_image);
        }

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList_user.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_user_image;
        TextView tv_name;
        LinearLayout ll_item;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            iv_user_image = itemView.findViewById(R.id.iv_user_image);
            tv_name = itemView.findViewById(R.id.tv_name);
            ll_item = itemView.findViewById(R.id.ll_item);
        }
    }

    public void updateList(ArrayList<User> newList) {
        arrayList_user = new ArrayList<>();
        arrayList_user.addAll(newList);
        notifyDataSetChanged();
    }
}
