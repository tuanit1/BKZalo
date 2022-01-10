/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:14 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/11/21, 12:17 AM
 * /
 */

package com.example.bkzalo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.listeners.ClickMemberListener;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder>{

    private ArrayList<User> arrayList_user;
    private ArrayList<Participant> arrayList_parti;
    private ClickMemberListener listener;
    private Context context;

    public MemberAdapter(ArrayList<User> arrayList_user, ArrayList<Participant> arrayList_parti, ClickMemberListener listener) {
        this.arrayList_user = arrayList_user;
        this.arrayList_parti = arrayList_parti;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MemberAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_member, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MemberAdapter.MyViewHolder holder, int position) {
        User user = arrayList_user.get(position);
        Participant participant = arrayList_parti.get(position);

        String image_path = user.getImage_url();

        if(!image_path.isEmpty()){
            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.image_user_holder)
                    .error(R.drawable.message_placeholder_ic)
                    .into(holder.img_user);
        }else{
            Picasso.get()
                    .load(R.drawable.message_placeholder_ic)
                    .into(holder.img_user);
        }

        holder.tv_name.setText(user.getName());

        if(participant.isAdmin()){
            holder.tv_admin.setVisibility(View.VISIBLE);
        }else {
            holder.tv_admin.setVisibility(View.GONE);
        }

        if(user.getId() == Constant.UID){
            holder.iv_more.setVisibility(View.GONE);
        }

        holder.iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context, holder.iv_more);

                menu.inflate(R.menu.menu_member);
                MenuItem menuItem = menu.getMenu().getItem(1);

                if(participant.isAdmin()){
                    menuItem.setTitle("Hủy quyền quản trị viên");
                }else {
                    menuItem.setTitle("Chỉ định làm quản trị viên");
                }

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        switch (id) {
                            case R.id.menu_member_profile:
                                listener.onProfile(user.getId());
                                break;

                            case R.id.menu_member_admin:
                                listener.onAdmin(user.getId(), participant.isAdmin());
                                break;
                            case R.id.menu_member_delete:
                                listener.onDelete(user.getId());
                                break;
                        }
                        return true;
                    }
                });

                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList_user.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_user, iv_more;
        TextView tv_name, tv_admin;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            iv_more = itemView.findViewById(R.id.iv_more);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_admin = itemView.findViewById(R.id.tv_admin);

        }
    }
}
