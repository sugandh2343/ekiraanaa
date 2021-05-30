package com.example.ekiraanaa.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ekiraanaa.Constants;
import com.example.ekiraanaa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private EditText et_titile,et_description,et_quantity,et_price,
            et_discounted_price,et_discounted_note;
    private TextView et_category,et_sub_category;
    private SwitchCompat switch_discount;
    private Button btn_add_product_cart;
    private ImageView img_product;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    //permission constant
    private static final int camera_request_code=200;
    private static final int storage_request_code=300;
    private static final int image_pick_gallery_code=400;
    private static final int image_pick_camera_code=500;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private Uri image_uri;
    private String category;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        et_titile=findViewById(R.id.et_title);
        et_description=findViewById(R.id.et_description);
        et_category=findViewById(R.id.et_category);
        et_quantity=findViewById(R.id.et_quantity);
        et_price=findViewById(R.id.et_price);
        et_discounted_price=findViewById(R.id.et_discounted_price);
        et_discounted_note=findViewById(R.id.et_discounted_note);
        et_sub_category=findViewById(R.id.et_sub_category);
        switch_discount=findViewById(R.id.switch_discount);
        btn_back=findViewById(R.id.btn_back);
        btn_add_product_cart=findViewById(R.id.btn_add_product_cart);
        img_product=findViewById(R.id.img_product);

        et_discounted_price.setVisibility(View.GONE);
        et_discounted_note.setVisibility(View.GONE);

        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        switch_discount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    et_discounted_price.setVisibility(View.VISIBLE);
                    et_discounted_note.setVisibility(View.VISIBLE);
                }
                else{
                    et_discounted_price.setVisibility(View.GONE);
                    et_discounted_note.setVisibility(View.GONE);

                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        img_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickdialog();

            }
        });
        et_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });
        et_sub_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(category==""){
                    Toast.makeText(AddProductActivity.this,"Choose Category First",Toast.LENGTH_SHORT).show();

                }
                else{
                    showSubCategory(category);
                }
            }
        });
        btn_add_product_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flow
                //1.input
                // Validate
                //Add data to db
                inputData();

            }
        });
    }

    private void showSubCategory(String category) {
        Log.d("Category",""+category);
        boolean result=category.equals("COVID ESSENTIALS");
        Log.d("Result",""+result);
        if(category.equals("COVID ESSENTIALS")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.CovidEssentials, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.CovidEssentials[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();

        }
        else if(category.equals("Beverages")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.Beverages, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.Beverages[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();

        }
        else if(category.equals("Breakfast")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.Breakfast, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.Breakfast[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();

        }
        else if(category.equals("Health Drinks")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.HealthDrinks, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.HealthDrinks[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();

        }
        else if (category.equals("Dairy")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.Dairy, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.Dairy[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();
        }
        else if(category.equals("Provisions")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.Provisions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.Provisions[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();

        }
        else if(category.equals("Condiments")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.Condiments, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.Condiments[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();
        }
        else if(category.equals("Instant Food")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.InstantFood, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.InstantFood[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();

        }
        else if(category.equals("Snacks and Frozen Food")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.SnacksAndFrozenFood, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.SnacksAndFrozenFood[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();

        }
        else if(category.equals("Chocolates")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.Chocolates, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.Chocolates[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();
        }
        else if(category.equals("Ice Creams")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.IceCreams, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.IceCreams[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();
        }
        else if (category.equals("Baby Care")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.BabyCare, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.BabyCare[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();
        }
        else if (category.equals("Personal Care")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.PersonalCare, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.PersonalCare[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();
        }
        else if(category.equals("Cleaning")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.Cleaning, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.Cleaning[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();
        }
        else if(category.equals("Stationary")){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Pick Sub Category")
                    .setItems(Constants.Stationary, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sub_category_selected=Constants.Stationary[which];
                            et_sub_category.setText(sub_category_selected);
                        }
                    }).show();
        }


    }

    private String product_title,product_description,product_category,product_quantity,original_price,discount_price,discount_note,sub_category;
    private Boolean discount_available=false;
    private void inputData(){
        product_title=et_titile.getText().toString().trim();
        product_description=et_description.getText().toString().trim();
        product_category=et_category.getText().toString().trim();
        product_quantity=et_quantity.getText().toString().trim();
        original_price=et_price.getText().toString().trim();
        discount_available=switch_discount.isChecked();
        sub_category=et_sub_category.getText().toString().trim();



        if(TextUtils.isEmpty(product_title)){
            Toast.makeText(this, "Product title cannot be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(product_description)){
            Toast.makeText(this, "Product description cannot be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(product_category)){
            Toast.makeText(this, "Choose Product Category",Toast.LENGTH_SHORT).show();
            return;
        }
        if(discount_available){
            Log.d("Discount Available",""+discount_available);
            discount_price=et_discounted_price.getText().toString().trim();
            discount_note=et_discounted_note.getText().toString().trim();
            if(TextUtils.isEmpty(discount_price)) {
                Toast.makeText(this, "Discount Price cannot be empty on Discounted Product", Toast.LENGTH_SHORT).show();
                return;
            }}
            else{
                discount_price="0";
                discount_note=" ";
            }

        addProduct();
    }
    private void addProduct(){
        progressDialog.setMessage("Adding Product");
        progressDialog.show();
        final String timestamp=""+System.currentTimeMillis();
        String productId=timestamp;
        if(image_uri==null){
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("productId",""+timestamp);
            hashMap.put("productTitle",""+product_title);
            hashMap.put("productDescription",""+product_description);
            hashMap.put("productCategory",""+product_category);
            hashMap.put("productIcon","");
            hashMap.put("originalPrice",""+original_price);
            hashMap.put("discountPrice",""+discount_price);
            hashMap.put("discountNote",""+discount_note);
            hashMap.put("discountAvailable",""+discount_available);
            hashMap.put("subCategory",""+sub_category);
            hashMap.put("productQuantity",""+product_quantity);

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Products");
            reference.child(productId).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this,"Product Added Successfully ",Toast.LENGTH_SHORT).show();
                            clearData();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else{
            String filePathAndName="productImages/"+""+timestamp;
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri=uriTask.getResult();
                            if(uriTask.isSuccessful()){

                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("productId",""+timestamp);
                                hashMap.put("productTitle",""+product_title);
                                hashMap.put("productDescription",""+product_description);
                                hashMap.put("productCategory",""+product_category);
                                hashMap.put("productIcon",""+downloadImageUri);
                                hashMap.put("originalPrice",""+original_price);
                                hashMap.put("discountPrice",""+discount_price);
                                hashMap.put("discountNote",""+discount_note);
                                hashMap.put("discountAvailable",""+discount_available);
                                hashMap.put("subCategory",""+sub_category);
                                hashMap.put("productQuantity",""+product_quantity);


                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Products");
                                reference.child(productId).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this,"Product Added Successfully ",Toast.LENGTH_SHORT).show();
                                                clearData();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    });
        }
    }

    private void clearData() {
    et_titile.setText("");
    et_description.setText("");
    et_discounted_price.setText("");
    et_discounted_note.setText("");
    et_category.setText("");
    et_quantity.setText("");
    et_price.setText("");
    img_product.setImageResource(R.drawable.ic_add_product);
    image_uri=null;
    et_sub_category.setText("");
    }

    private void categoryDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(Constants.product_categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category_selected=Constants.product_categories[which];
                        et_category.setText(category_selected);
                        category=et_category.getText().toString().trim();
                        Log.d("Category",""+category);

                    }
                }).show();


    }
    private void showImagePickdialog() {
        String[] options={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            if(check_camera_permission()){
                                pick_from_camera();
                            }
                            else{
                                requestCameraPermission();
                            }

                        }
                        else{
                            if(check_storage_permission()){
                                pick_from_gallery();

                            }
                            else{
                                requestStoragePermission();
                            }

                        }
                    }
                }).show();
    }
    private void pick_from_gallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,image_pick_gallery_code);
    }

    private void pick_from_camera(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_ Image Description");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent=new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,image_pick_camera_code);
    }

    private boolean check_storage_permission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        Log.d("Result",""+result);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,storage_request_code);
        check_storage_permission();

    }

    private boolean check_camera_permission(){
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,camera_request_code);

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case camera_request_code: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("storage","Storage Accepted -"+storageAccepted);
                    Log.d("Camera","Camera Accepted -"+cameraAccepted);
                    Log.d("length","Grant Results -"+grantResults[0]);
                    Log.d("temp",""+PackageManager.PERMISSION_GRANTED);
                    if (cameraAccepted && storageAccepted) {
                        pick_from_camera();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Camera Permissions Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case storage_request_code: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("temp",""+PackageManager.PERMISSION_GRANTED);
                    Log.d("length","Grant Results -"+grantResults[0]);
                    Log.d("temp",""+PackageManager.PERMISSION_GRANTED);
                    if (storageAccepted) {
                        //Permission Allowed
                        Log.d("compiler","is at current position");
                        pick_from_gallery();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Storage Permission Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode==RESULT_OK){
            if(requestCode==image_pick_gallery_code){
                image_uri=data.getData();
                img_product.setImageURI(image_uri);

            }
            else if(requestCode==image_pick_camera_code){
                image_uri=data.getData();
                img_product.setImageURI(image_uri);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}