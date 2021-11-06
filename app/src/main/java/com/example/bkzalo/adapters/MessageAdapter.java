package com.example.bkzalo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.activitiy.AddGroupActivity;
import com.example.bkzalo.listeners.ClickChatItemListener;
import com.example.bkzalo.listeners.ClickChatListListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.utils.Constant;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{

    private ArrayList<Message> arrayList_message;
    private ClickChatItemListener listener;
    private Context context;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(ArrayList<Message> arrayList_message, ClickChatItemListener listener){
        this.arrayList_message = arrayList_message;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MyViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Message msg = arrayList_message.get(position);

        if(msg.getType().equals("noti")){
            holder.ll_msg.setVisibility(View.GONE);
            holder.tv_noti.setVisibility(View.VISIBLE);

            String text = "";

            if(msg.getNickname().equals("")){
                text = msg.getName() + " đã " + msg.getMessage();
            }else{
                text = msg.getNickname() + " đã " + msg.getMessage();
            }

            holder.tv_noti.setText(text);

            return;
        }

        if(msg.isRemove()){
            holder.tv_message.setVisibility(View.GONE);
            holder.iv_img_message.setVisibility(View.GONE);
            holder.cv_remove.setVisibility(View.VISIBLE);
        }else {
            switch (msg.getType()){
                case "text":

                    holder.tv_message.setVisibility(View.VISIBLE);
                    holder.iv_img_message.setVisibility(View.GONE);
                    holder.cv_remove.setVisibility(View.GONE);

                    if(msg.getUser_id() == Constant.UID){
                        holder.cv_message.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                PopupMenu menu = new PopupMenu(context, holder.cv_message);
                                menu.inflate(R.menu.menu_msg_item);

                                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        int id = item.getItemId();

                                        switch (id) {
                                            case R.id.menu_remove_msg:
                                                listener.onRemove(msg.getId());
                                                break;
                                        }
                                        return true;
                                    }
                                });

                                menu.show();
                                return true;
                            }
                        });
                    }

                    holder.tv_message.setText(msg.getMessage());

                    break;

                case "image":

                    holder.tv_message.setVisibility(View.GONE);
                    holder.iv_img_message.setVisibility(View.VISIBLE);
                    holder.cv_remove.setVisibility(View.GONE);

                    String img_path = Constant.SERVER_URL + "image/image_message/" + msg.getMessage();

                    if(msg.getUser_id() == Constant.UID){
                        holder.iv_img_message.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                PopupMenu menu = new PopupMenu(context, holder.iv_img_message);
                                menu.inflate(R.menu.menu_msg_item);

                                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        int id = item.getItemId();

                                        switch (id) {
                                            case R.id.menu_remove_msg:
                                                listener.onRemove(msg.getId());
                                                break;
                                        }
                                        return true;
                                    }
                                });

                                menu.show();
                                return true;
                            }
                        });
                    }

                    Picasso.get()
                            .load(img_path)
                            .into(holder.iv_img_message);
                    break;

            }
        }



        if(msg.getUser_id() != Constant.UID){

            String img_path = Constant.SERVER_URL + "image/image_user/" + msg.getImage();

            Picasso.get()
                    .load(img_path)
                    .placeholder(R.drawable.message_placeholder_ic)
                    .into(holder.iv_user_image);

            if(msg.getNickname().equals("")){
                holder.tv_name.setText(msg.getName());
            }else {
                holder.tv_name.setText(msg.getNickname());
            }

        }

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        String date_txt = formatter.format(msg.getTime());

        holder.tv_time.setText(date_txt);
    }

    @Override
    public int getItemCount() {
        return arrayList_message.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(arrayList_message.get(position).getType().equals("noti")){
            return MSG_TYPE_RIGHT;
        }else if(arrayList_message.get(position).getUser_id() == Constant.UID){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_message,tv_time, tv_name, tv_noti;
        ImageView iv_user_image, iv_img_message;
        CardView cv_message;
        LinearLayout cv_remove, ll_msg;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            iv_user_image = itemView.findViewById(R.id.iv_user_image);

            iv_img_message = itemView.findViewById(R.id.iv_img_message);

            tv_message = itemView.findViewById(R.id.tv_message);

            tv_time = itemView.findViewById(R.id.tv_time);

            tv_name = itemView.findViewById(R.id.tv_name);

            tv_noti = itemView.findViewById(R.id.tv_noti);

            cv_message = itemView.findViewById(R.id.cv_message);

            cv_remove = itemView.findViewById(R.id.cv_remove);

            ll_msg = itemView.findViewById(R.id.ll_msg);
        }
    }
}
