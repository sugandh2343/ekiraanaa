package com.example.ekiraanaa.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ekiraanaa.Models.ModelOrderUser;
import com.example.ekiraanaa.R;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser> {
    private Context context;
    private ArrayList<ModelOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> orderUserList) {
        this.context = context;
        this.orderUserList = orderUserList;
    }

    @NonNull
    @Override
    public AdapterOrderUser.HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.order_user_single_row,parent,false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterOrderUser.HolderOrderUser holder, int position) {
        ModelOrderUser modelOrderUser=orderUserList.get(position);
        String orderId=modelOrderUser.getOrderId();
        String orderBy=modelOrderUser.getOrderBy();
        String orderCost=modelOrderUser.getOrderCost();
        String orderStatus=modelOrderUser.getOrderStatus();
        String orderTime=modelOrderUser.getOrderTime();
        String orderItemQty=modelOrderUser.getOrderItemQty();

        holder.txt_total_amt_value.setText(""+orderCost);
        holder.txt_order_id.setText("Order Id: "+orderId);
        holder.txt_order_status.setText(""+orderStatus);
        if(orderStatus.equals("In Progress")){
            holder.txt_order_status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else  if(orderStatus.equals("Completed")){
            holder.txt_order_status.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
        else  if(orderStatus.equals("Cancelled")){
            holder.txt_order_status.setTextColor(context.getResources().getColor(R.color.colorRed));
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatted_date= DateFormat.format("dd/mm/yyyy",calendar).toString();
        holder.txt_order_date.setText(formatted_date);



    }

    @Override
    public int getItemCount() {

        return orderUserList.size();
    }

    class HolderOrderUser extends RecyclerView.ViewHolder {
        private TextView txt_order_id, txt_order_date, txt_total_item_value, txt_total_item_label, txt_total_amt_value,
                txt_order_status;
        private ImageView img_next;

        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);
            txt_order_id = itemView.findViewById(R.id.txt_order_id);
            txt_order_date = itemView.findViewById(R.id.txt_order_date);
            txt_total_item_value = itemView.findViewById(R.id.txt_total_item_value);
            txt_total_item_label = itemView.findViewById(R.id.txt_total_item_label);
            txt_total_amt_value = itemView.findViewById(R.id.txt_total_amt_value);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);
            img_next = itemView.findViewById(R.id.img_next);

        }
    }
}