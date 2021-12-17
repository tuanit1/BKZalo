package com.example.bkzalo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.listeners.ClickItemRequestListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder>{

    private ArrayList<User> arrayList_user;
    private ClickItemRequestListener listener;
    private Context context;

    public RequestListAdapter(ArrayList<User> arrayList_user, ClickItemRequestListener listener) {
        this.arrayList_user = arrayList_user;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_friendlist, parent, false);
        return new RequestListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = arrayList_user.get(position);

        holder.tv_name.setText(user.getName());

        String image_url = Constant.SERVER_URL + "image/image_user/" + user.getImage();

        Picasso.get()
                .load(image_url)
                .placeholder(R.drawable.message_placeholder_ic)
                .into(holder.iv_user_image);

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(user.getId());
            }
        });

        holder.btn_acpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAccept(user.getId());
            }
        });

        holder.btn_refu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRefuse(user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList_user.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_user_image;
        TextView tv_name;
        Button btn_acpt;
        Button btn_refu;
        LinearLayout ll_item;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            iv_user_image = itemView.findViewById(R.id.iv_user_image);
            tv_name = itemView.findViewById(R.id.tv_name);
            btn_acpt = itemView.findViewById(R.id.btn_acpt);
            btn_refu = itemView.findViewById(R.id.btn_refu);
            ll_item = itemView.findViewById(R.id.ll_item);
        }
    }

}
