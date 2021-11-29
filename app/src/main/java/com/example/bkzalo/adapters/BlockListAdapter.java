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
import com.example.bkzalo.listeners.ClickItemBlockListener;
import com.example.bkzalo.listeners.ClickItemRequestListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.MyViewHolder>{

    private ArrayList<User> arrayList_user;
    private ClickItemBlockListener listener;
    private Context context;

    public BlockListAdapter(ArrayList<User> arrayList_user, ClickItemBlockListener listener) {
        this.arrayList_user = arrayList_user;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_blocklist, parent, false);
        return new BlockListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = arrayList_user.get(position);

        holder.tv_namefri.setText(user.getName());

        String image_url = Constant.SERVER_URL + "image/image_user/" + user.getImage();

        Picasso.get()
                .load(image_url)
                .into(holder.iview_user_image);

        holder.llout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(user.getId());
            }
        });
        holder.btn_unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { listener.onUnBlock(user.getId()); }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList_user.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        Button btn_unblock;
        ImageView iview_user_image;
        TextView tv_namefri;
        LinearLayout llout_item;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            btn_unblock = itemView.findViewById(R.id.btn_unblock);
            iview_user_image = itemView.findViewById(R.id.iview_user_image);
            tv_namefri = itemView.findViewById(R.id.tv_namefri);
            llout_item = itemView.findViewById(R.id.llout_item);
        }
    }

}
