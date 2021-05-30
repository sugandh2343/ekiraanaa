package com.example.ekiraanaa.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.ekiraanaa.Adapters.AdapterCartItem;
import com.example.ekiraanaa.Adapters.AdapterOrderUser;
import com.example.ekiraanaa.Adapters.AdapterProductUser;
import com.example.ekiraanaa.Adapters.AdapterShopUser;
import com.example.ekiraanaa.Models.ModelCartItem;
import com.example.ekiraanaa.Models.ModelOrderUser;
import com.example.ekiraanaa.Models.ModelProducts;
import com.example.ekiraanaa.Models.ModelShop;
import com.example.ekiraanaa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class MainUserActivity extends AppCompatActivity {
    private TextView txt_name,txt_email,tab_products,tab_orders,txt_mobile,txt_login,bottom_toolbar_orders,bottom_toolbar_contact_us,
            bottom_toolbar_home;
    private ImageButton btn_logout,btn_edit_profile,btn_add_cart_user;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private RelativeLayout rl_products,rl_orders;
    private RecyclerView rv_products_user,rv_order_user;
    private ImageView img_user;
//    private ArrayList<ModelShop> shopsList;
    private ArrayList<ModelProducts> productsList;
//    private AdapterShopUser adapterShopUser;
    private AdapterProductUser adapterProductUser;
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;
    private String userName;

    private ArrayList<ModelOrderUser> orderList;
    private AdapterOrderUser adapterOrderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        txt_name=findViewById(R.id.txt_name);
        txt_email=findViewById(R.id.txt_email);
        txt_mobile=findViewById(R.id.txt_mobile);
        txt_login=findViewById(R.id.txt_login);
        rl_products=findViewById(R.id.rl_products);
        rl_orders=findViewById(R.id.rl_orders);
        img_user=findViewById(R.id.img_profile);
        rv_products_user=findViewById(R.id.rv_products_user);
        rv_order_user=findViewById(R.id.rv_order_user);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv_products_user.setLayoutManager(manager);
        rv_order_user.setLayoutManager(new LinearLayoutManager(this));
        rv_order_user.setAdapter(adapterOrderUser);
        tab_products=findViewById(R.id.tab_product);
        tab_orders=findViewById(R.id.tab_orders);
        bottom_toolbar_contact_us=findViewById(R.id.bottom_toolbar_contact_us);
        bottom_toolbar_orders=findViewById(R.id.bottom_toolbar_orders);
        bottom_toolbar_home=findViewById(R.id.bottom_toolbar_home);
//        rv_shops=findViewById(R.id.rv_shops);
//
//        tab_shops=findViewById(R.id.tab_shops);
        rl_orders.findViewById(R.id.rl_orders);
        tab_orders=findViewById(R.id.tab_orders);
        btn_logout=findViewById(R.id.btn_logout);

        firebaseAuth=FirebaseAuth.getInstance();
        btn_edit_profile=findViewById(R.id.btn_edit_profile);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        btn_edit_profile=findViewById(R.id.btn_edit_profile);
        btn_add_cart_user=findViewById(R.id.btn_cart_user);
        loadProducts();
        deleteCart() ;


        btn_add_cart_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCartDialog();
            }
        });




        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUserActivity.this,EditProfileUserActivity.class));
                finish();

            }
        });
        checkUser();
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
            }
        });
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUserActivity.this,LoginActivity.class));
                finish();

            }
        });
//        tab_products.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProductsUI();
//            }
//        });
//        tab_orders.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showOrdersUI();
//            }
//        });
        bottom_toolbar_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUI();

            }
        });
        bottom_toolbar_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductsUI();
            }
        });


    }
    private void loadProducts() {
        productsList=new ArrayList<>();
        ValueEventListener reference= FirebaseDatabase.getInstance().getReference("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelProducts modelProducts=ds.getValue(ModelProducts.class);
                            productsList.add(modelProducts);


                        }
                        Log.d("Product List",""+productsList.size());
                        adapterProductUser=new AdapterProductUser(MainUserActivity.this,productsList);
                        rv_products_user.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrders() {

        orderList=new ArrayList<>();
        ValueEventListener reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderUser modelOrderUser=ds.getValue(ModelOrderUser.class);
                            orderList.add(modelOrderUser);


                        }
                        Log.d("Order List",""+ orderList.size());
                        adapterOrderUser=new AdapterOrderUser(MainUserActivity.this,orderList);
                        rv_order_user.setAdapter(adapterOrderUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            img_user.setImageResource(R.mipmap.ic_user);
            txt_mobile.setVisibility(View.GONE);
            txt_name.setVisibility(View.GONE);
            txt_login.setVisibility(View.VISIBLE);

        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                         String name=""+ds.child("name").getValue();
                         String phone=""+ds.child("phone").getValue();
                         String profileImage=""+ds.child("profileImage").getValue();
                         String city=""+ds.child("city").getValue();
                         userName=name;

                         txt_name.setText(name);
                         txt_mobile.setText(phone);
                         try{
                             Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(img_user);
                         }
                         catch(Exception e){
                             img_user.setImageResource(R.mipmap.ic_user);

                         }
                         loadProducts();
                         loadOrders();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//    private void loadShops(String city) {
////        shopsList=new ArrayList<>();
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
//        reference.orderByChild("accountType").equalTo("Seller")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        shopsList.clear();
//                        for(DataSnapshot ds: snapshot.getChildren()){
//                            ModelShop modelShop=ds.getValue(ModelShop.class);
//
//                            String shopCity=""+ds.child("city").getValue();
//
//                            if(shopCity.equals(city)){
//                                shopsList.add(modelShop);
//                            }
//                        }
//                        adapterShopUser=new AdapterShopUser(MainUserActivity.this,shopsList);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

    private void makeOffline() {
        progressDialog.setMessage("Logging Out....");
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("online","false");
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void showOrdersUI() {
        //show product UI and hide orders UI
        rl_products.setVisibility(View.GONE);
        rl_orders.setVisibility(View.VISIBLE);
        bottom_toolbar_orders.setTextColor(getResources().getColor(R.color.colorBlack));
        bottom_toolbar_orders.setBackgroundResource(R.drawable.shape_rect_04);
        bottom_toolbar_home.setTextColor(getResources().getColor(R.color.colorWhite));
        bottom_toolbar_home.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

        private void showProductsUI() {
        //show order UI and hide products ui
        rl_products.setVisibility(View.VISIBLE);
        rl_orders.setVisibility(View.GONE);
        bottom_toolbar_orders.setTextColor(getResources().getColor(R.color.colorBlack));
        bottom_toolbar_orders.setBackgroundResource(R.drawable.shape_rect_04);
        bottom_toolbar_home.setTextColor(getResources().getColor(R.color.colorWhite));
        bottom_toolbar_home.setBackgroundColor(getResources().getColor(android.R.color.transparent));

//
//
//
    }

    public double allTotalPrice=0.0;
    private double deliveryFee=35.0;
    public TextView txt_subTotal,txt_delivery_fee_value,txt_total_fee_value;

    private void showCartDialog() {
        cartItemList=new ArrayList<>();

        View view= LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        //init views

        TextView txt_shop_name=view.findViewById(R.id.txt_shop_name);
        TextView txt_subTotal_label=view.findViewById(R.id.txt_subTotal_label);
        txt_subTotal=view.findViewById(R.id.txt_subTotal);
        TextView txt_delivery_fee_label=view.findViewById(R.id.txt_delivery_fee_label);
        TextView txt_delivery_fee=view.findViewById(R.id.txt_delivery_fee_value);
        txt_delivery_fee_value=view.findViewById(R.id.txt_delivery_fee_value);
        TextView txt_total_fee_label=view.findViewById(R.id.txt_total_fee_label);
        txt_total_fee_value=view.findViewById(R.id.txt_total_fee_value);
        Button btn_checkOut=view.findViewById(R.id.btn_checkOut);
        RecyclerView rv_cartItems=view.findViewById(R.id.rv_cartItems);
        RelativeLayout rl_prices=view.findViewById(R.id.rl_prices);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);
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
        txt_total_fee_value.setText("Rs"+allTotalPrice);
        AlertDialog dialog=builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;
            }
        });
        btn_checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateuserDetails();
                dialog.dismiss();
            }
        });

    }

    private void validateuserDetails() {
        ValueEventListener ref= FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String phone=""+ds.child("phone").getValue();
                            String latitude=""+ds.child("latitude").getValue();
                            String longitude=""+ds.child("longitude").getValue();

                            if(phone.equals("")||phone.equals("null")){
                                Toast.makeText(MainUserActivity.this, "Phone Number required", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if(latitude.equals("")||latitude.equals("null")||longitude.equals("")||longitude.equals("null")){
                                Toast.makeText(MainUserActivity.this, "Address required", Toast.LENGTH_SHORT).show();
                                return;
                                }
                            if(cartItemList.size()==0){
                                Toast.makeText(MainUserActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        submitorder();
    }

    private void submitorder() {
        progressDialog.setMessage("Placing Order");
        progressDialog.show();
        String timestamp=""+System.currentTimeMillis();
        String cost=""+txt_total_fee_value.getText().toString().trim().replace("Rs","");
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus","In Progress");
        hashMap.put("orderCost",""+cost);
        hashMap.put("orderBy",""+userName);
        hashMap.put("orderByUid",""+firebaseAuth.getUid());
        hashMap.put("orderQuantity",""+cartItemList.size());

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        for(int i=0;i<cartItemList.size();i++){
                            String pId=cartItemList.get(i).getPId();
                            String id=cartItemList.get(i).getId();
                            String cost=cartItemList.get(i).getCost();
                            String name=cartItemList.get(i).getName();
                            String price=cartItemList.get(i).getPrice();
                            String quantity=cartItemList.get(i).getQuantity();

                            HashMap<String,String> hashMap1=new HashMap<>();
                            hashMap1.put("pId",""+pId);
                            hashMap1.put("name",""+name);
                            hashMap1.put("cost",""+cost);
                            hashMap1.put("price",""+price);
                            hashMap1.put("quantity",""+quantity);
                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, "Order Placed Successfully", Toast.LENGTH_LONG).show();






                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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


}