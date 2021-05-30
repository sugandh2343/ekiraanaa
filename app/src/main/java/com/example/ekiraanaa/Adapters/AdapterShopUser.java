package com.example.ekiraanaa.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ekiraanaa.Activity.ShopDetailsActivity;
import com.example.ekiraanaa.Models.ModelShop;
import com.example.ekiraanaa.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShopUser extends RecyclerView.Adapter<AdapterShopUser.HolderShop>{
    private Context context;
    public ArrayList<ModelShop> shopsList;

    public AdapterShopUser(Context context, ArrayList<ModelShop> shopsList) {
        this.context = context;
        this.shopsList = shopsList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.shop_user_single_row,parent,false);

        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
        ModelShop modelShop=shopsList.get(position);
        String accountType=modelShop.getAddress();
        String address=modelShop.getAddress();
        String state=modelShop.getState();
        String city=modelShop.getCity();
        String country=modelShop.getCountry();
        String deliveryFee=modelShop.getDeliveryFee();
        String email=modelShop.getEmail();
        String latitude=modelShop.getLatitude();
        String longitude=modelShop.getLongitude();
        String online=modelShop.getOnline();
        String name=modelShop.getName();
        String shopName=modelShop.getShopName();
        String phone=modelShop.getPhone();
        String uid=modelShop.getUid();
        String timeStamp=modelShop.getTimeStamp();
        String shopOpen=modelShop.getShopOpen();
        String profileImage=modelShop.getProfileImage();

        //setData
        holder.txt_shop_name.setText(shopName);
        holder.txt_phone.setText(phone);
        holder.txt_address.setText(address);
        if(online.equals("true")){
            holder.img_online.setVisibility(View.VISIBLE);
        }
        else{
            holder.img_online.setVisibility(View.GONE);
        }
        //check if shop open
        if(shopOpen.equals("true")){
            holder.txt_shop_closed.setVisibility(View.GONE);
        }
        else{
            holder.txt_shop_closed.setVisibility(View.VISIBLE);
        }
        try{
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(holder.img_shop);
        }
        catch(Exception e){
            holder.img_shop.setImageResource(R.drawable.ic_store);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ShopDetailsActivity.class);
                intent.putExtra("shopUid",uid);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return shopsList.size();
    }

    //view holder
    class HolderShop extends RecyclerView.ViewHolder{
        //ui views of shop_user_single_row.xml
        private ImageView img_shop,img_online;
        private TextView txt_shop_closed,txt_shop_name,txt_phone,txt_address;
        private RatingBar ratingBar;


        public HolderShop(@NonNull View itemView) {
            super(itemView);

            img_shop=itemView.findViewById(R.id.img_shop);
            img_online=itemView.findViewById(R.id.img_online);
            txt_shop_closed=itemView.findViewById(R.id.txt_shop_closed);
            txt_shop_name=itemView.findViewById(R.id.txt_shop_name);
            txt_phone=itemView.findViewById(R.id.txt_phone);
            txt_address=itemView.findViewById(R.id.txt_address);
            ratingBar=itemView.findViewById(R.id.rating_bar_shop);

        }
    }
}
