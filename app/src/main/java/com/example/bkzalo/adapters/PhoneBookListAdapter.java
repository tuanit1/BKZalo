package com.example.bkzalo.adapters;
import static android.Manifest.permission.CALL_PHONE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.activitiy.Profile_PB_Activity;
import com.example.bkzalo.fragments.FragmentPhonebook;
import com.example.bkzalo.listeners.ChatListListener;
import com.example.bkzalo.listeners.ClickPhoneBookListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import static android.Manifest.permission.CALL_PHONE;

public class PhoneBookListAdapter extends RecyclerView.Adapter<PhoneBookListAdapter.MyViewHolder> {
    private ArrayList<User> arrayList_user;
    private ClickPhoneBookListener listener;
    private Context context;

    public PhoneBookListAdapter(ArrayList<User> arrayList_user, ClickPhoneBookListener listener, Context context) {
        this.arrayList_user = arrayList_user;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_phonebook, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        final User user = arrayList_user.get(position);

        holder.tv_name.setText(user.getName());
        holder.tv_phone.setText(user.getPhone());

        holder.item_phonebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onClick(user);

            }
        });

        holder.imv_phonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call(user.getPhone());
            }
        });

        String image_path = Constant.SERVER_URL + "image/image_user/" + user.getImage();

        Picasso.get()
                .load(image_path)
                .into(holder.imv_phonebook);

    }

    private void Call(String phone)
    {
        String s = "tel:" + phone;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(s));
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(intent);
        } else {
            return;
        }
    }


    @Override
    public int getItemCount() {
        return arrayList_user.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imv_phonebook;
        TextView tv_name, tv_phone;
        LinearLayout item_phonebook;
        ImageView imv_phonecall;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name_phonebook);
            tv_phone = itemView.findViewById(R.id.tv_phone_phonebook);
            imv_phonebook = itemView.findViewById(R.id.imv_phonebook);
            item_phonebook = itemView.findViewById(R.id.item_phonebook);
            imv_phonecall = itemView.findViewById(R.id.imv_phonecall);

        }

    }

}
