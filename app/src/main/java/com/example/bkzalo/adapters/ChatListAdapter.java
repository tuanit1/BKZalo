package com.example.bkzalo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.listeners.ChatListListener;
import com.example.bkzalo.models.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder>{

    private ArrayList<User> arrayList_user;
    private Context context;

    public ChatListAdapter(ArrayList<User> arrayList_user, Context context) {
        this.arrayList_user = arrayList_user;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_chatlist, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        User user = arrayList_user.get(position);

        holder.tv_name.setText(user.getName());

    }

    @Override
    public int getItemCount() {
        return arrayList_user.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tv_name, tv_text;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_text = itemView.findViewById(R.id.tv_text);
            imageView = itemView.findViewById(R.id.image_view);

        }
    }
}
