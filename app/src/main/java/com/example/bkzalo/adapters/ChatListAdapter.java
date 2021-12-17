package com.example.bkzalo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.listeners.ClickChatListListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;
import com.example.bkzalo.models.UserSelect;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder>{

    private ArrayList<Participant> arrayList_participant;
    private ClickChatListListener listener;
    private ArrayList<Room> arrayList_room;
    private ArrayList<User> arrayList_user;
    private ArrayList<Message> arrayList_message;
    private Context context;
    private String type = "";

    public ChatListAdapter(String type, ArrayList<Participant> arrayList_participant, ArrayList<Room> arrayList_room, ArrayList<User> arrayList_user, ArrayList<Message> arrayList_message, Context context, ClickChatListListener listener) {
        this.arrayList_participant = arrayList_participant;
        this.listener = listener;
        this.arrayList_room = arrayList_room;
        this.arrayList_user = arrayList_user;
        this.arrayList_message = arrayList_message;
        this.context = context;
        this.type = type;
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

        Room room = arrayList_room.get(position);

        if(type.equals("hide_list")){
            holder.tv_hide.setVisibility(View.VISIBLE);
            holder.tv_hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.unHide(room.getId());
                }
            });
        }

        if(room.getType().equals("private")){

            //private
            holder.img_group.setVisibility(View.GONE);
            holder.img_private.setVisibility(View.VISIBLE);
            holder.iv_group_ic.setVisibility(View.GONE);

            Participant parti = GetParti(room);

            User user = GetFriendByRoomId(room.getId());

            String name = "";

            if(user.isOnline()){
                holder.iv_on.setVisibility(View.VISIBLE);
                holder.iv_off.setVisibility(View.GONE);
            }else {
                holder.iv_on.setVisibility(View.GONE);
                holder.iv_off.setVisibility(View.VISIBLE);
            }

            if(parti.getNickname().equals("")){
                holder.tv_name.setText(user.getName());
                name = user.getName();
            }else {
                holder.tv_name.setText(parti.getNickname());
                name = parti.getNickname();
            }

            String image_path = Constant.SERVER_URL + "image/image_user/" + user.getImage();

            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.message_placeholder_ic)
                    .into(holder.img_private);

            holder.rv_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(room.getId(), user.getId(),"private");
                }
            });
        }else {

            //group
            holder.tv_name.setText(room.getName());
            holder.iv_group_ic.setVisibility(View.VISIBLE);

            if(!room.getImage().equals("")){

                //group with image
                holder.img_group.setVisibility(View.GONE);
                holder.img_private.setVisibility(View.VISIBLE);

                String image_path = Constant.SERVER_URL + "image/image_room/" + room.getImage();

                Picasso.get()
                        .load(image_path)
                        .placeholder(R.drawable.message_placeholder_ic)
                        .into(holder.img_private);
            }else {

                //group default image
                holder.img_group.setVisibility(View.VISIBLE);
                holder.img_private.setVisibility(View.GONE);

                String[] arrayList_txt = room.getName().toUpperCase().split(" ");

                if(arrayList_txt.length == 1){
                    String[] first = arrayList_txt[0].split("");
                    String first_char = first[0];

                    holder.tv_name_group.setText(first_char);
                }else {
                    String[] first = arrayList_txt[0].split("");
                    String first_char = first[0];

                    String[] second = arrayList_txt[1].split("");
                    String second_char = second[0];

                    holder.tv_name_group.setText(first_char+second_char);
                }

            }

            holder.rv_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(room.getId(), 0 ,"group");
                }
            });

        }

        Message message = GetMessage(room);


        if(message != null){

            String name = "";

            if(message.getNickname().equals("")){
                name = message.getName();
            }else {
                name = message.getNickname();
            }

            switch (message.getType()){
                case "text":
                    if(message.getUser_id() == Constant.UID){
                        if(message.isRemove()){
                            holder.tv_text.setText("Bạn đã gỡ một tin nhắn");
                        }else {
                            holder.tv_text.setText("Bạn: " + message.getMessage());
                        }

                    }else {
                        if(room.getType().equals("group")){
                            if(message.isRemove()){
                                holder.tv_text.setText(name + " đã gỡ một tin nhắn");
                            }else {
                                holder.tv_text.setText(name + ": " + message.getMessage());
                            }

                        }else {
                            if(message.isRemove()){
                                holder.tv_text.setText("đã gỡ một tin nhắn");
                            }else {
                                holder.tv_text.setText(message.getMessage());
                            }
                        }

                        if(!message.isSeen()){
                            holder.tv_text.setTextColor(context.getResources().getColor(R.color.text_color));
                            holder.tv_text.setTypeface(holder.tv_text.getTypeface(), Typeface.BOLD);

                            holder.tv_time.setTextColor(context.getResources().getColor(R.color.text_color));
                            holder.tv_time.setTypeface(holder.tv_text.getTypeface(), Typeface.BOLD);
                        }
                    }

                    break;
                case "image":

                    if(message.getUser_id() == Constant.UID){
                        if(message.isRemove()){
                            holder.tv_text.setText("Bạn đã gỡ một tin nhắn");
                        }else {
                            holder.tv_text.setText("Bạn: " + "đã gửi một hình ảnh");
                        }

                    }else {
                        if(room.getType().equals("group")){
                            if(message.isRemove()){
                                holder.tv_text.setText(name + " đã gỡ một tin nhắn");
                            }else {
                                holder.tv_text.setText(name + ": đã gửi một hình ảnh");
                            }

                        }else {
                            if(message.isRemove()){
                                holder.tv_text.setText("đã gỡ một tin nhắn");
                            }else {
                                holder.tv_text.setText("đã gửi một hình ảnh");
                            }
                        }

                        if(!message.isSeen()){
                            holder.tv_text.setTextColor(context.getResources().getColor(R.color.text_color));
                            holder.tv_text.setTypeface(holder.tv_text.getTypeface(), Typeface.BOLD);

                            holder.tv_time.setTextColor(context.getResources().getColor(R.color.text_color));
                            holder.tv_time.setTypeface(holder.tv_text.getTypeface(), Typeface.BOLD);
                        }
                    }

                    break;
                case "noti":
                case "leave":

                    if(message.getUser_id() == Constant.UID){
                        holder.tv_text.setText("Bạn đã " + message.getMessage());
                    }else {
                        holder.tv_text.setText(name + " đã " + message.getMessage());

                        if(!message.isSeen()){
                            holder.tv_text.setTextColor(context.getResources().getColor(R.color.text_color));
                            holder.tv_text.setTypeface(holder.tv_text.getTypeface(), Typeface.BOLD);

                            holder.tv_time.setTextColor(context.getResources().getColor(R.color.text_color));
                            holder.tv_time.setTypeface(holder.tv_text.getTypeface(), Typeface.BOLD);
                        }
                    }



                    break;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

            String date_txt = formatter.format(message.getTime());

            holder.tv_time.setText(date_txt);

        }else {
            holder.tv_text.setText("Không có tin nhắn nào");
            holder.tv_time.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return arrayList_room.size();
    }

    public void setAdapterData(ArrayList<Room> arrayList){
        this.arrayList_room = arrayList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_private, iv_group_ic, iv_on, iv_off;
        CardView img_group;
        TextView tv_name, tv_text, tv_name_group, tv_hide, tv_time;
        RelativeLayout rv_item;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            rv_item = itemView.findViewById(R.id.rv_item);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_text = itemView.findViewById(R.id.tv_text);
            iv_on = itemView.findViewById(R.id.iv_on);
            iv_off = itemView.findViewById(R.id.iv_off);
            tv_hide = itemView.findViewById(R.id.tv_hide);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_name_group = itemView.findViewById(R.id.tv_name_group);
            img_private = itemView.findViewById(R.id.img_private);
            img_group = itemView.findViewById(R.id.img_group);
            iv_group_ic = itemView.findViewById(R.id.iv_group_ic);

        }
    }

    public Participant GetParti(Room room){
        for(Participant participant : arrayList_participant){
            if(participant.getRoom_id() == room.getId() && participant.getUser_id() != Constant.UID){
                return participant;
            }
        }

        return null;
    }

    public Message GetMessage(Room room){
        for(Message m : arrayList_message){
            if(m.getRoom_id() == room.getId()){
                return m;
            }
        }

        return null;
    }

    public User GetFriendByRoomId(int room_id){
        for(Participant p : arrayList_participant){
            if(p.getUser_id() != Constant.UID && p.getRoom_id() == room_id){
                for(User u : arrayList_user){
                    if(u.getId() == p.getUser_id()){
                        return u;
                    }
                }
            }
        }

        return null;
    }
}
