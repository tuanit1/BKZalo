package com.example.bkzalo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SearchMessageAdapter extends RecyclerView.Adapter<SearchMessageAdapter.MyViewHolder>{

    private ArrayList<Message> arrayList_message;
    private Context context;

    public SearchMessageAdapter(ArrayList<Message> arrayList_message) {
        this.arrayList_message = arrayList_message;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_message, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Message msg = arrayList_message.get(position);

        String img_path = Constant.SERVER_URL + "image/image_user/" + msg.getImage();

        Picasso.get()
                .load(img_path)
                .placeholder(R.drawable.message_placeholder_ic)
                .into(holder.iv_user_image);

        holder.tv_msg.setText(msg.getMessage());

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

        String date_txt = formatter.format(msg.getTime());

        holder.tv_time.setText(date_txt);

        holder.tv_name.setText(msg.getName());
    }

    @Override
    public int getItemCount() {
        return arrayList_message.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_msg, tv_time, tv_name;
        ImageView iv_user_image;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            iv_user_image = itemView.findViewById(R.id.iv_user_image);

            tv_msg = itemView.findViewById(R.id.tv_msg);

            tv_time = itemView.findViewById(R.id.tv_time);

            tv_name = itemView.findViewById(R.id.tv_name);

        }
    }
}
