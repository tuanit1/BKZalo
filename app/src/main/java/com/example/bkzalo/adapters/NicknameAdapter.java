package com.example.bkzalo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.listeners.UserSelectListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NicknameAdapter extends RecyclerView.Adapter<NicknameAdapter.MyViewHolder>{

    private ArrayList<User> arrayList_user;
    private ArrayList<Participant> arrayList_participant;
    private UserSelectListener listener;
    private Context context;

    public NicknameAdapter(ArrayList<User> arrayList_user, ArrayList<Participant> arrayList_participant, UserSelectListener listener) {
        this.arrayList_user = arrayList_user;
        this.arrayList_participant = arrayList_participant;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_nickname, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        User user = arrayList_user.get(position);
        Participant participant = arrayList_participant.get(position);

        String img_path = Constant.SERVER_URL + "image/image_user/" + user.getImage();

        Picasso.get()
                .load(img_path)
                .placeholder(R.drawable.message_placeholder_ic)
                .into(holder.iv_user_image);

        if(participant.getNickname().isEmpty()){
            holder.tv_nickname.setText("Chưa có biệt danh");
        }else {
            holder.tv_nickname.setText(participant.getNickname());
        }

        holder.tv_name.setText(user.getName());

        holder.rv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(user.getId(), false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList_user.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_nickname;
        ImageView iv_user_image;
        RelativeLayout rv_item;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            iv_user_image = itemView.findViewById(R.id.img_user_name);

            tv_name = itemView.findViewById(R.id.tv_name);

            tv_nickname = itemView.findViewById(R.id.tv_nickname);

            rv_item = itemView.findViewById(R.id.rv_item);

        }
    }
}
