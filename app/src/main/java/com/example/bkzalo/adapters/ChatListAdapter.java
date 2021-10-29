package com.example.bkzalo.adapters;

import android.content.Context;
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
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;
import com.example.bkzalo.models.UserSelect;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder>{

    private ArrayList<Participant> arrayList_participant;
    private ArrayList<Room> arrayList_room;
    private ArrayList<User> arrayList_user;
    private Context context;

    public ChatListAdapter(ArrayList<Participant> arrayList_participant, ArrayList<Room> arrayList_room, ArrayList<User> arrayList_user, Context context) {
        this.arrayList_participant = arrayList_participant;
        this.arrayList_room = arrayList_room;
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

        Room room = arrayList_room.get(position);

        if(room.getType().equals("private")){

            //private
            holder.img_group.setVisibility(View.GONE);
            holder.img_private.setVisibility(View.VISIBLE);

            Participant parti = GetParti(room);

            User user = GetFriendByRoomId(room.getId());


            if(parti.getNickname().equals("")){
                holder.tv_name.setText(user.getName());
            }else {
                holder.tv_name.setText(parti.getNickname());
            }

            String image_path = Constant.SERVER_URL + "image/image_user/" + user.getImage();

            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.message_placeholder_ic)
                    .into(holder.img_private);
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

        }

    }

    @Override
    public int getItemCount() {
        return arrayList_room.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView img_private, iv_group_ic;
        CardView img_group;
        TextView tv_name, tv_text, tv_name_group;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_text = itemView.findViewById(R.id.tv_text);
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
