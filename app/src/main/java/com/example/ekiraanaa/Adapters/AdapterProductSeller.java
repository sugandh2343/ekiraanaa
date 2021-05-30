package com.example.ekiraanaa.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ekiraanaa.Activity.EditProductActivity;
import com.example.ekiraanaa.FilterProduct;
import com.example.ekiraanaa.Models.ModelProducts;
import com.example.ekiraanaa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {
    private Context context;
    public ArrayList<ModelProducts> productList,filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProducts> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = filterList;
        this.filter = filter;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.product_seller_single_row,parent,false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {
        ModelProducts modelProducts=productList.get(position);
        //get data
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
        String originalPrice=modelProducts.getOriginalPrice();

        //set data
        holder.txt_title.setText(title);
        holder.txt_quantity.setText(quantity);
        holder.txt_discounted_note.setText(discountNote);
        holder.txt_discounted_note.setText("Rs"+discountPrice);
        holder.txt_original_price.setText("Rs"+originalPrice);
        //Log.d("discount","Discount Available"+discountAvailable);
        if(discountAvailable.equals("true")){
            holder.txt_discounted_note.setVisibility(View.VISIBLE);
            holder.txt_discount_price.setVisibility(View.VISIBLE);
            holder.txt_original_price.setPaintFlags(holder.txt_original_price.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.txt_discounted_note.setVisibility(View.GONE);
            holder.txt_discount_price.setVisibility(View.GONE);
            holder.txt_original_price.setPaintFlags(0);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_add_product).into(holder.img_product);

        }
        catch(Exception e){
            holder.img_product.setImageResource(R.drawable.ic_add_product);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle item clicks
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
        String originalPrice=modelProducts.getOriginalPrice();

        txt_title.setText(title);
        txt_description.setText(productDescription);
        txt_category.setText(productCategory);
        txt_quantity.setText(quantity);
        txt_discount_note.setText(discountNote);
        txt_discount_price.setText("Rs"+discountPrice);
        txt_original_price.setText("Rs"+originalPrice);
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
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
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
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
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


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterProduct(this,filterList);
        }
        return filter;
    }


    class HolderProductSeller extends RecyclerView.ViewHolder{

        private ImageView img_product;
        private TextView txt_discounted_note,txt_title, txt_quantity,txt_discount_price,txt_original_price;


        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);
            img_product=itemView.findViewById(R.id.img_product);
            txt_discount_price=itemView.findViewById(R.id.txt_discounted_price);
            txt_title=itemView.findViewById(R.id.txt_title);
            txt_discounted_note=itemView.findViewById(R.id.txt_discounted_note);
            txt_quantity=itemView.findViewById(R.id.txt_quantity);
            txt_original_price=itemView.findViewById(R.id.txt_original_price);
        }
    }
}
