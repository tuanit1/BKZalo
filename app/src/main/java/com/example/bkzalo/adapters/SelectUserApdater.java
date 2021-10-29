package com.example.bkzalo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.listeners.UserSelectListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.models.UserSelect;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SelectUserApdater extends RecyclerView.Adapter<SelectUserApdater.MyViewHolder>{

    private ArrayList<UserSelect> arrayList_user;
    private UserSelectListener listener;
    private Context context;

    public SelectUserApdater(ArrayList<UserSelect> arrayList_user, UserSelectListener listener, Context context) {
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
        View view = inflater.inflate(R.layout.item_add_group_user, parent, false);
        return new SelectUserApdater.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        UserSelect user = arrayList_user.get(position);

        holder.tv_name.setText(user.getName());

        String img_path = Constant.SERVER_URL + "image/image_user/" + user.getImage();

        Picasso.get()
                .load(img_path)
                .placeholder(R.drawable.message_placeholder_ic)
                .into(holder.imageview);

        holder.ckb.setChecked(user.isChecked());
        holder.ckb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                arrayList_user.get(holder.getAdapterPosition()).setChecked(isChecked);
                listener.onClick(arrayList_user.get(holder.getAdapterPosition()).getId(), isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList_user.size();
    }

    public void setAdapterData(ArrayList<UserSelect> arrayList){
        this.arrayList_user = arrayList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageview;
        CheckBox ckb;
        TextView tv_name;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.imageview);
            tv_name = itemView.findViewById(R.id.tv_name);
            ckb = itemView.findViewById(R.id.ckb);

        }
    }
}
