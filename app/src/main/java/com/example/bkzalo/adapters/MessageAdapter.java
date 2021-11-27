package com.example.bkzalo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.listeners.ClickChatItemListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

        if(msg.getType().equals("noti") || msg.getType().equals("leave")){
            holder.ll_msg.setVisibility(View.GONE);
            holder.tv_message.setVisibility(View.GONE);
            holder.iv_img_message.setVisibility(View.GONE);
            holder.cv_remove.setVisibility(View.GONE);
            holder.tv_day.setVisibility(View.GONE);
            holder.tv_noti.setVisibility(View.VISIBLE);

            String text = "";

            if(msg.getNickname().equals("")){
                text = msg.getName() + " đã " + msg.getMessage();
            }else{
                text = msg.getNickname() + " đã " + msg.getMessage();
            }

            holder.tv_noti.setText(text);

        }else if(msg.isRemove()){
            holder.ll_msg.setVisibility(View.VISIBLE);
            holder.tv_message.setVisibility(View.GONE);
            holder.iv_img_message.setVisibility(View.GONE);
            holder.cv_remove.setVisibility(View.VISIBLE);
            holder.tv_noti.setVisibility(View.GONE);
            holder.tv_day.setVisibility(View.GONE);

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

            SimpleDateFormat formatter1= new SimpleDateFormat("dd/MM/yyyy HH:mm");
            if(position > 0){
                Date date = msg.getTime();
                Date date_previous = arrayList_message.get(position-1).getTime();
                long diffInTime = date.getTime() - date_previous.getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInTime);

                if(diffInMinutes > 120){
                    holder.tv_day.setText(formatter1.format(date));
                    holder.tv_day.setVisibility(View.VISIBLE);
                }
            }else {
                holder.tv_day.setText(formatter1.format(msg.getTime()));
                holder.tv_day.setVisibility(View.VISIBLE);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

            String date_txt = formatter.format(msg.getTime());

            holder.tv_time.setText(date_txt);

            if(msg.isSeen() && !msg.getType().equals("noti") && !msg.getType().equals("leave") && msg.getUser_id() == Constant.UID && position == arrayList_message.size()-1){
                holder.tv_seen.setVisibility(View.VISIBLE);
            }
        }else {

            holder.tv_noti.setVisibility(View.GONE);
            holder.ll_msg.setVisibility(View.VISIBLE);
            holder.tv_day.setVisibility(View.GONE);

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

                    holder.iv_img_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onImageClick(msg);
                        }
                    });
                    break;

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

            SimpleDateFormat formatter1= new SimpleDateFormat("dd/MM/yyyy HH:mm");
            if(position > 0){
                Date date = msg.getTime();
                Date date_previous = arrayList_message.get(position-1).getTime();
                long diffInTime = date.getTime() - date_previous.getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInTime);

                if(diffInMinutes > 120){
                    holder.tv_day.setText(formatter1.format(date));
                    holder.tv_day.setVisibility(View.VISIBLE);
                }
            }else {
                holder.tv_day.setText(formatter1.format(msg.getTime()));
                holder.tv_day.setVisibility(View.VISIBLE);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

            String date_txt = formatter.format(msg.getTime());

            holder.tv_time.setText(date_txt);

            if(msg.isSeen() && !msg.getType().equals("noti") && !msg.getType().equals("leave") && msg.getUser_id() == Constant.UID && position == arrayList_message.size()-1){
                holder.tv_seen.setVisibility(View.VISIBLE);
            }

        }


    }

    @Override
    public int getItemCount() {
        return arrayList_message.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(arrayList_message.get(position).getType().equals("noti") || arrayList_message.get(position).getType().equals("leave")){
            return MSG_TYPE_RIGHT;
        }else if(arrayList_message.get(position).getUser_id() == Constant.UID){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_message,tv_time, tv_name, tv_noti, tv_seen, tv_day;
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

            tv_seen = itemView.findViewById(R.id.tv_seen);

            tv_day = itemView.findViewById(R.id.tv_day);

            tv_noti = itemView.findViewById(R.id.tv_noti);

            cv_message = itemView.findViewById(R.id.cv_message);

            cv_remove = itemView.findViewById(R.id.cv_remove);

            ll_msg = itemView.findViewById(R.id.ll_msg);
        }
    }
}
