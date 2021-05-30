package com.example.ekiraanaa.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ekiraanaa.Activity.EditProductActivity;
import com.example.ekiraanaa.Activity.LoginActivity;
import com.example.ekiraanaa.FilterProduct;
import com.example.ekiraanaa.FilterProductUser;
import com.example.ekiraanaa.Models.ModelProducts;
import com.example.ekiraanaa.Models.ModelShop;
import com.example.ekiraanaa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.TreeMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {
    private String ordered_quantity;
    private Context context;
    public ArrayList<ModelProducts> productsList,filterList;
    private FilterProductUser filter;


    public AdapterProductUser(Context context, ArrayList<ModelProducts> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = filterList;
        this.filter = filter;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.product_user_single_row,parent,false);
        return new HolderProductUser(view);
    }


    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        //get data
        ModelProducts modelProducts=productsList.get(position);
        String discountAvailable=modelProducts.getDiscountAvailable();
        String discountNote=modelProducts.getDiscountNote();
        String discountPrice=modelProducts.getDiscountPrice();
        String category=modelProducts.getProductCategory();
        String originalPrice=modelProducts.getOriginalPrice();
        String productDescription=modelProducts.getProductDescription();
        String productTitle=modelProducts.getProductTitle();
        String productQuantity=modelProducts.getProductQuantity();
        String productId=modelProducts.getProductId();
        String timestamp=modelProducts.getTimestamp();
        String productIcon=modelProducts.getProductIcon();
        String productSubCategory=modelProducts.getProductSubCategory();

        //set data
        holder.txt_title.setText(productTitle);
        holder.txt_discounted_note.setText(discountNote);
        holder.txt_description.setText(productDescription);
        holder.txt_original_price.setText("₹"+originalPrice);
        holder.txt_discounted_price.setText("₹"+discountPrice);
        holder.txt_category.setText(category);
        holder.txt_quantity_single.setText(productQuantity);
        if(ordered_quantity==null){
            holder.txt_ordered_quantity.setVisibility(View.GONE);
            holder.txt_in_cart.setVisibility(View.GONE);
            holder.txt_ordered_quantity.setVisibility(View.GONE);

        }
        else {
            holder.txt_in_cart.setVisibility(View.VISIBLE);
            holder.txt_ordered_quantity.setVisibility(View.VISIBLE);
            holder.txt_ordered_quantity.setText(ordered_quantity);
        }



        if(discountAvailable.equals("true")){
            holder.txt_discounted_note.setVisibility(View.VISIBLE);
            holder.txt_discounted_price.setVisibility(View.VISIBLE);
            holder.txt_original_price.setPaintFlags(holder.txt_original_price.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.txt_discounted_note.setVisibility(View.GONE);
            holder.txt_discounted_price.setVisibility(View.GONE);
            holder.txt_original_price.setPaintFlags(0);
        }
        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_add_product).into(holder.img_product);

        }
        catch(Exception e){
            holder.img_product.setImageResource(R.drawable.ic_add_product);

        }

        holder.txt_addTocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                if(firebaseAuth.getUid()==null){
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
                    builder.setTitle("Not Logged In")
                            .setMessage("User Need to Be Log In for adding to Cart")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent(context, LoginActivity.class);
                                    context.startActivity(intent);

                                }
                            });
                    android.app.AlertDialog dialog=builder.create();
                    dialog.show();
                }
                //add product to cart
                showQuantityDialog(modelProducts,position);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuantityDialog(modelProducts,position);
            }
        });
        holder.btn_edit_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsBottomSheet(modelProducts);
            }
        });

    }

    private void detailsBottomSheet(ModelProducts modelProducts) {
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        View view=LayoutInflater.from(context).inflate(R.layout.bts_product_details_seller,null);
        bottomSheetDialog.setContentView(view);

        ImageButton btn_back=view.findViewById(R.id.btn_back);
        ImageButton btn_delete=view.findViewById(R.id.btn_delete);
        ImageButton btn_edit_product=view.findViewById(R.id.btn_edit_product);
        ImageView img_product=view.findViewById(R.id.img_product);
        TextView txt_discount_note=view.findViewById(R.id.txt_discount_note);
        TextView txt_original_price=view.findViewById(R.id.txt_original_price);
        TextView txt_title=view.findViewById(R.id.txt_title);
        TextView txt_description=view.findViewById(R.id.txt_description);
        TextView txt_category=view.findViewById(R.id.txt_category);
        TextView txt_quantity=view.findViewById(R.id.txt_quantity);
        TextView txt_discount_price=view.findViewById(R.id.txt_discount_price);
        TextView txt_sub_category=view.findViewById(R.id.txt_sub_category);

        String id=modelProducts.getProductId();
        String uid=modelProducts.getUid();
        String discountAvailable=modelProducts.getDiscountAvailable();
        String discountNote=modelProducts.getDiscountNote();
        String discountPrice=modelProducts.getDiscountPrice();
        String productCategory=modelProducts.getProductCategory();
        String productDescription=modelProducts.getProductDescription();
        String icon=modelProducts.getProductIcon();
        String quantity=modelProducts.getProductQuantity();
        String title=modelProducts.getProductTitle();
        String timestamp=modelProducts.getTimestamp();
        String sub_category=modelProducts.getProductSubCategory();
        String originalPrice=modelProducts.getOriginalPrice();

        txt_title.setText(title);
        txt_description.setText(productDescription);
        txt_category.setText(productCategory);
        txt_quantity.setText(quantity);
        txt_discount_note.setText(discountNote);
        txt_discount_price.setText("Rs"+discountPrice);
        txt_original_price.setText("Rs"+originalPrice);
        txt_sub_category.setText(sub_category);
        if(discountAvailable.equals("true")){
            txt_discount_note.setVisibility(View.VISIBLE);
            txt_discount_price.setVisibility(View.VISIBLE);
            txt_original_price.setPaintFlags(txt_original_price.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            txt_discount_note.setVisibility(View.GONE);
            txt_discount_price.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_add_product).into(img_product);

        }
        catch(Exception e){
            img_product.setImageResource(R.drawable.ic_add_product);

        }

        bottomSheetDialog.show();
        btn_edit_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit handle
                Intent intent=new Intent(context, EditProductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dellete handle
                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure yoy want to delete product "+title+"?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProduct(id);
                            }
                        });
            }
        });

    }
    private void deleteProduct(String id) {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Products");
        reference.child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Product deleted",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private double cost=0.0;
    private double finalCost=0.0;
    private int quantity=0;

    private void showQuantityDialog(ModelProducts modelProducts,int position) {
        View view=LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        ImageView img_product=view.findViewById(R.id.img_product);
        TextView txt_title=view.findViewById(R.id.txt_product_title);
        TextView txt_quantity=view.findViewById(R.id.txt_product_quantity);
        TextView txt_description=view.findViewById(R.id.txt_product_description);
        TextView txt_discounted_note=view.findViewById(R.id.txt_product_discounted_note);
        TextView txt_discounted_price_note=view.findViewById(R.id.txt_product_discounted_price_note);
        TextView txt_original_price_note=view.findViewById(R.id.txt_product_original_price_note);
        TextView txt_final_price=view.findViewById(R.id.txt_final_price);
        ImageButton btn_increment=view.findViewById(R.id.btn_increment);
        TextView txt_quantity_cart=view.findViewById(R.id.txt_quantity_cart);
        ImageButton btn_decrement=view.findViewById(R.id.btn_decrement);
        Button btn_continue=view.findViewById(R.id.btn_continue);
        String quantity_display=modelProducts.getProductQuantity();

        String productId=modelProducts.getProductId();
        String product_title=modelProducts.getProductTitle();
        String product_quantity=modelProducts.getProductQuantity();
        String description= modelProducts.getProductDescription();
        String discountNote=modelProducts.getDiscountNote();
        String image=modelProducts.getProductIcon();
        String price;
        if(modelProducts.getDiscountAvailable().equals("true")){
            price=modelProducts.getDiscountPrice();
            txt_discounted_note.setVisibility(View.VISIBLE);
            txt_original_price_note.setPaintFlags(txt_original_price_note.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        }else{
            txt_discounted_note.setVisibility(View.GONE);
            txt_discounted_price_note.setVisibility(View.GONE);
            price=modelProducts.getOriginalPrice();

        }
        cost=Double.parseDouble(price.replaceAll("₹",""));
        finalCost=Double.parseDouble(price.replaceAll("₹",""));
        quantity=1;
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        try{
            Picasso.get().load(image).placeholder(R.drawable.ic_baseline_shopping_cart_24).into(img_product);

        }
        catch(Exception e){
            img_product.setImageResource(R.drawable.ic_baseline_shopping_cart_24);

        }
        txt_title.setText(""+product_title);
        txt_quantity.setText(""+quantity_display);
        txt_description.setText(""+description);
        txt_discounted_note.setText(""+txt_discounted_note);
        txt_quantity.setText(""+product_quantity);
        txt_discounted_note.setText(""+discountNote);
        txt_original_price_note.setText("₹"+modelProducts.getOriginalPrice());
        txt_discounted_price_note.setText("₹"+modelProducts.getDiscountPrice());
        txt_final_price.setText(""+finalCost);

        AlertDialog dialog=builder.create();
        dialog.show();

        btn_increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost=finalCost+cost;
                quantity=quantity+1;
                txt_final_price.setText("₹"+finalCost);
                txt_quantity_cart.setText(""+quantity);
            }
        });
        btn_decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1){
                    finalCost=finalCost-cost;
                    quantity--;

                    txt_final_price.setText("₹"+finalCost);
                    txt_quantity_cart.setText(""+quantity);




                }
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=txt_title.getText().toString().trim();
                String eachPrice=price;
                String totalPrice=txt_final_price.getText().toString().trim().replace("₹","");
                String quantity_product=txt_quantity.getText().toString().trim();
                ordered_quantity=""+quantity;
                notifyItemChanged(position);



                addToCart(productId,title,eachPrice,totalPrice,quantity_product);


                dialog.dismiss();

            }
        });

    }
    private int itemId=1;
    private void addToCart(String productId, String title, String eachPrice, String price, String quantity) {
        itemId++;

        EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PId",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_EachPrice",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text", "not null"}))
                .doneTableColumn();
        Boolean b=easyDB.addData("Item_id",itemId)
                .addData("Item_PId",productId)
                .addData("Item_Name",title)
                .addData("Item_EachPrice",eachPrice)
                .addData("Item_Price",price)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to Cart",Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterProductUser(this,filterList);
        }
        return filter;
    }


    class HolderProductUser extends RecyclerView.ViewHolder{
        //uid views
        private ImageView img_product;
        private TextView txt_discounted_note,txt_title,txt_description,txt_addTocart,
                        txt_discounted_price,txt_original_price,txt_category,txt_quantity_single,txt_ordered_quantity,txt_in_cart;
        private ImageButton btn_edit_product;


        public HolderProductUser(@NonNull View itemView) {
            super(itemView);
            img_product=itemView.findViewById(R.id.img_product);
            txt_discounted_note=itemView.findViewById(R.id.txt_discounted_note);
            txt_title=itemView.findViewById(R.id.txt_title_single);
            txt_description=itemView.findViewById(R.id.txt_description_single);
            txt_addTocart=itemView.findViewById(R.id.txt_addToCart);
            txt_discounted_price=itemView.findViewById(R.id.txt_discounted_price);
            txt_original_price=itemView.findViewById(R.id.txt_original_price);
            txt_category=itemView.findViewById(R.id.txt_category_single);
            txt_quantity_single=itemView.findViewById(R.id.txt_quantity_single);
            btn_edit_product=itemView.findViewById(R.id.btn_edit_product);
            txt_ordered_quantity=itemView.findViewById(R.id.txt_ordered_quantity);
            txt_in_cart=itemView.findViewById(R.id.txt_in_cart);

    }
}
}
