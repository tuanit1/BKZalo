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
import com.example.bkzalo.listeners.ImageDetailListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImageMessageAdapter extends RecyclerView.Adapter<ImageMessageAdapter.MyViewHolder>{

    private ArrayList<Message> arrayList_message;
    private ImageDetailListener listener;
    private Context context;

    public ImageMessageAdapter(ArrayList<Message> arrayList_message, ImageDetailListener listener) {
        this.arrayList_message = arrayList_message;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_msg, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        Message message = arrayList_message.get(position);

        String img_path = Constant.SERVER_URL + "image/image_message/" + message.getMessage();

        Picasso.get().load(img_path).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList_message.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageview);

        }
    }
}
