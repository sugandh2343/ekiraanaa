package com.example.ekiraanaa.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ekiraanaa.Adapters.AdapterProductSeller;
import com.example.ekiraanaa.Constants;
import com.example.ekiraanaa.Models.ModelProducts;
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

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {
    private TextView txt_name,txt_shop_name,txt_mobile,tab_product,tab_orders,txt_filtered_products;
    private ImageButton btn_logout,btn_add_product,btn_edit_profile,btn_filter_product;
    private ImageView img_profile;
    private EditText et_search;
    private RelativeLayout rl_products,rl_orders;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private RecyclerView rv_products;
    private ArrayList<ModelProducts> productList;
    private AdapterProductSeller adapterProductSeller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);
        txt_name=findViewById(R.id.txt_name);
        btn_logout=findViewById(R.id.btn_logout);
        txt_name=findViewById(R.id.txt_name);
        txt_shop_name=findViewById(R.id.txt_shop_name);
        txt_mobile=findViewById(R.id.txt_mobile);
        tab_product=findViewById(R.id.tab_product);
        tab_orders=findViewById(R.id.tab_orders);
        rl_orders=findViewById(R.id.rl_orders);
        rl_products=findViewById(R.id.rl_products);
        txt_filtered_products=findViewById(R.id.txt_filtered_product);
        et_search=findViewById(R.id.et_search_product);
        btn_filter_product=findViewById(R.id.btn_filter_product);
        rv_products=findViewById(R.id.rv_products);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        btn_add_product=findViewById(R.id.btn_add_product);
        btn_edit_profile=findViewById(R.id.btn_edit_profile);


        checkUser();
        showProductUI();
        loadAllProducts();
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductSeller.getFilter().filter(s);

                }
                catch(Exception e){
                    e.printStackTrace();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerActivity.this,EditProfileSellerActivity.class));
                finish();

            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeOffline();
            }
        });

        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerActivity.this,AddProductActivity.class));
            }
        });
        tab_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductUI();
            }
        });
        tab_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUI();
            }
        });
        btn_filter_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category")
                        .setItems(Constants.product_categories_filter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               String selected=Constants.product_categories_filter[which];
                               if(selected.equals("All")){
                                   loadAllProducts();
                               }
                               else{
                                   loadFilteredProducts(selected);
                               }
                            }
                        }).show();
            }
        });
    }

    private void loadFilteredProducts(String selected) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String productCategory=""+ds.child("productCategory").getValue();
                            if(selected.equals(productCategory)) {
                                ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                                productList.add(modelProducts);
                            }

                            adapterProductSeller=new AdapterProductSeller(MainSellerActivity.this,productList);
                            rv_products.setAdapter(adapterProductSeller);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelProducts modelProducts = ds.getValue(ModelProducts.class);
                    productList.add(modelProducts);
                }

                adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                rv_products.setAdapter(adapterProductSeller);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showOrdersUI() {
        //show product UI and hide orders UI
        rl_products.setVisibility(View.GONE);
        rl_orders.setVisibility(View.VISIBLE);
        tab_orders.setTextColor(getResources().getColor(R.color.colorBlack));
        tab_orders.setBackgroundResource(R.drawable.shape_rect_04);
        tab_product.setTextColor(getResources().getColor(R.color.colorWhite));
        tab_product.setBackgroundColor(getResources().getColor(android.R.color.transparent));


    }

    private void showProductUI() {
        //show order UI and hide products ui
        rl_products.setVisibility(View.VISIBLE);
        rl_orders.setVisibility(View.GONE);
        tab_product.setTextColor(getResources().getColor(R.color.colorBlack));
        tab_product.setBackgroundResource(R.drawable.shape_rect_04);
        tab_orders.setTextColor(getResources().getColor(R.color.colorWhite));
        tab_orders.setBackgroundColor(getResources().getColor(android.R.color.transparent));




    }

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
                        Toast.makeText(MainSellerActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }




    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();;
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
                            String accountType=""+ds.child("accountType").getValue();
                            txt_name.setText(name+"("+accountType+")");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}