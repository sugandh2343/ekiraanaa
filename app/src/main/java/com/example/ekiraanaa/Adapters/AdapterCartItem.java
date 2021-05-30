package com.example.ekiraanaa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ekiraanaa.Activity.MainUserActivity;
import com.example.ekiraanaa.Activity.ShopDetailsActivity;
import com.example.ekiraanaa.Models.ModelCartItem;
import com.example.ekiraanaa.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{
    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.cart_item_single_row,parent,false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, int position) {
        ModelCartItem modelCartItem=cartItems.get(position);

        String id=modelCartItem.getId();
        String PId=modelCartItem.getPId();
        String title=modelCartItem.getName();
        String cost=modelCartItem.getCost();
        String price=modelCartItem.getPrice();
        String quantity=modelCartItem.getQuantity();

        holder.txt_item_title.setText(""+title);
        holder.txt_item_price.setText(""+cost);
        holder.txt_product_quantity.setText(""+quantity);
        holder.txt_item_each_price.setText(""+price);

        holder.txt_item_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will create a new table if not exist
                EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                        .addColumn(new Column("Item_PId",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Name",new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_EachPrice",new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price",new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Quantity",new String[]{"text", "not null"}))
                        .doneTableColumn();
                easyDB.deleteRow(1,id);
                Toast.makeText(context,"Removed From Cart",Toast.LENGTH_SHORT).show();
                //refresh list
                cartItems.remove(position);
                notifyItemChanged(position);

                double tx=Double.parseDouble((((MainUserActivity)context).txt_total_fee_value.getText().toString().trim().replace("Rs","")));
                double totalPrice=tx-Double.parseDouble(cost.replace("Rs",""));
                double deliveryFee=Double.parseDouble((((ShopDetailsActivity)context).deliveryFee.replace("Rs","")));
                double sTotalPrice=Double.parseDouble(String.format("%.2f",totalPrice))-Double.parseDouble(String.format("%.2f",deliveryFee));
                ((MainUserActivity)context).allTotalPrice=0.00;
                ((MainUserActivity)context).txt_subTotal.setText("Rs"+String.format("%.2f",sTotalPrice));
                ((MainUserActivity)context).txt_total_fee_value.setText("Rs"+String.format("%.2f",Double.parseDouble(String.format("%.2f",totalPrice))));

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder{
        private TextView txt_item_title,txt_item_price,txt_item_each_price,txt_item_quantity_ordered,txt_item_remove,txt_product_quantity;



        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            txt_item_title=itemView.findViewById(R.id.txt_item_title);
            txt_item_price=itemView.findViewById(R.id.txt_item_price);
            txt_item_each_price=itemView.findViewById(R.id.txt_item_each_price);
            txt_item_quantity_ordered=itemView.findViewById(R.id.txt_item_quantity_ordered);
            txt_product_quantity=itemView.findViewById(R.id.txt_product_quantity);
            txt_item_remove=itemView.findViewById(R.id.txt_item_remove);



        }
    }
}
