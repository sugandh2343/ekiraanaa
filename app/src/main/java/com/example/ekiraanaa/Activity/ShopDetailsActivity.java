package com.example.ekiraanaa.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ekiraanaa.Adapters.AdapterCartItem;
import com.example.ekiraanaa.Adapters.AdapterProductSeller;
import com.example.ekiraanaa.Adapters.AdapterProductUser;
import com.example.ekiraanaa.Constants;
import com.example.ekiraanaa.Models.ModelCartItem;
import com.example.ekiraanaa.Models.ModelProducts;
import com.example.ekiraanaa.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailsActivity extends AppCompatActivity {
    private RelativeLayout rl_shops,rl_toolbar,rl_products;
    private ImageView img_shop;
    private ImageButton btn_call,btn_back,btn_map,btn_cart,
                        btn_filter_product;
    private TextView txt_shop_name,txt_phone,txt_email,txt_open_closed,
                    txt_delivery_fee,txt_address,txt_filtered_product;
    private EditText et_search_product;
    private RecyclerView rv_products;

    private String myLatitude,myLongitude,shopLatitude,shopLongitude,shopName,shopEmail,shopAddress,shopPhone;
    public String deliveryFee;

    private String shopUid;

    private ArrayList<ModelProducts> productsList;
    private AdapterProductUser adapterProductUser;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        rl_shops=findViewById(R.id.rl_shops);
        rl_toolbar=findViewById(R.id.rl_toolbar);
        rl_products=findViewById(R.id.rl_products);
        img_shop=findViewById(R.id.img_shop);
        btn_call=findViewById(R.id.btn_call);
        btn_back=findViewById(R.id.btn_back);
        btn_map=findViewById(R.id.btn_map);
        btn_cart=findViewById(R.id.btn_cart);
        btn_filter_product=findViewById(R.id.btn_filter_product);
        txt_shop_name=findViewById(R.id.txt_shop_name);
        txt_phone=findViewById(R.id.txt_phone);
        txt_email=findViewById(R.id.txt_email);
        txt_open_closed=findViewById(R.id.txt_open_closed);
        txt_delivery_fee=findViewById(R.id.txt_delivery_fee);
        txt_address=findViewById(R.id.txt_address);
        txt_filtered_product=findViewById(R.id.txt_filtered_product);
        et_search_product=findViewById(R.id.et_search_product);
        rv_products=findViewById(R.id.rv_products);
        
        shopUid=getIntent().getStringExtra("shopUid");

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        deleteCart();
        
        
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call activity
                dialPhone();
            }
        });
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show cart dialog
                showCartDialog();
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open Maps
                openMap();
            }
        });
        btn_filter_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filter product
                AlertDialog.Builder builder=new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.product_categories_filter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected=Constants.product_categories_filter[which];
                                if(selected.equals("All")){
                                    loadShopProducts();
                                }
                                else{
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        et_search_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductUser.getFilter().filter(s);

                }
                catch(Exception e){
                    e.printStackTrace();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
                
                
        
    }

    private void deleteCart() {
        EasyDB easyDB=EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PId",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_EachPrice",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text", "not null"}))
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();
    }

    public double allTotalPrice=0.0;
    public TextView txt_subTotal,txt_delivery_fee_value,txt_total_fee_value;

    private void showCartDialog() {
        cartItemList=new ArrayList<>();

        View view= LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        //init views

        TextView txt_shop_name=view.findViewById(R.id.txt_shop_name);
        TextView txt_subTotal_label=view.findViewById(R.id.txt_subTotal_label);
        txt_subTotal=view.findViewById(R.id.txt_subTotal);
        TextView txt_delivery_fee_label=view.findViewById(R.id.txt_delivery_fee_label);
        txt_delivery_fee_value=view.findViewById(R.id.txt_delivery_fee_value);
        TextView txt_total_fee_label=view.findViewById(R.id.txt_total_fee_label);
        txt_total_fee_value=view.findViewById(R.id.txt_total_fee_value);
        Button btn_checkOut=view.findViewById(R.id.btn_checkOut);
        RecyclerView rv_cartItems=view.findViewById(R.id.rv_cartItems);
        RelativeLayout rl_prices=view.findViewById(R.id.rl_prices);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);
        txt_shop_name.setText(shopName);
        EasyDB easyDB=EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PId",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_EachPrice",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text", "not null"}))
                .doneTableColumn();
        Cursor res=easyDB.getAllData();
        while(res.moveToNext()){
            String id=res.getString(1);
            String pId=res.getString(2);
            String name=res.getString(3);
            String price=res.getString(4);
            String cost=res.getString(5);
            String quantity=res.getString(6);



            allTotalPrice=allTotalPrice+Double.parseDouble(cost);
            ModelCartItem modelCartItem=new ModelCartItem(
                    ""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity

            );
            cartItemList.add(modelCartItem);
        }
        adapterCartItem=new AdapterCartItem(this,cartItemList);
        rv_cartItems.setAdapter(adapterCartItem);

        txt_delivery_fee.setText("Rs"+deliveryFee);
        txt_subTotal.setText("Rs"+String.format("%.2f",allTotalPrice));
        txt_total_fee_value.setText("Rs"+(allTotalPrice+Double.parseDouble(deliveryFee.replace("Rs",""))));
        AlertDialog dialog=builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;
            }
        });

    }

    private void openMap() {
        String address="http://maps.google.com/maps?saddr"+myLatitude+","+myLongitude+"&daddr="+shopLatitude+","+shopLongitude;
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);

    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(this,""+shopPhone,Toast.LENGTH_SHORT).show();

    }

    private void loadShopProducts() {
        productsList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelProducts modelProducts=ds.getValue(ModelProducts.class);
                            productsList.add(modelProducts);

                        }
                        //setup adapter
                        adapterProductUser=new AdapterProductUser(ShopDetailsActivity.this,productsList);
                        rv_products.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadShopDetails() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=""+snapshot.child("name").getValue();
                shopName=""+snapshot.child("shopName").getValue();
                shopEmail=""+snapshot.child("email").getValue();
                shopPhone=""+snapshot.child("phone").getValue();
                shopAddress=""+snapshot.child("address").getValue();
                shopLatitude=""+snapshot.child("latitude").getValue();
                shopLongitude=""+snapshot.child("longitude").getValue();
                deliveryFee=""+snapshot.child("deliveryFee").getValue();
                String profileImage=""+snapshot.child("profileImage").getValue();
                String shopOpen=""+snapshot.child("shopOpen").getValue();

                txt_shop_name.setText(shopName);
                txt_email.setText(shopEmail);
                txt_delivery_fee.setText("Delivery Fee ; ₹"+deliveryFee);
                txt_address.setText(shopAddress);
                if(shopOpen.equals("true")){
                    txt_open_closed.setText("Open");

                }
                else{
                    txt_open_closed.setText("Closed");
                }
                try{
                    Picasso.get().load(profileImage).into(img_shop);

                }
                catch(Exception e){
                    img_shop.setImageResource(R.drawable.ic_store);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String name=""+ds.child("name").getValue();
                            String email=""+ds.child("email").getValue();
                            String phone=""+ds.child("phone").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();
                            String city=""+ds.child("city").getValue();
                            String accountType=""+ds.child("accountType").getValue();
                            myLatitude=""+ds.child("latitude").getValue();
                            myLongitude=""+ds.child("longitude").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}