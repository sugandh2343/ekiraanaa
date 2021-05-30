package com.example.ekiraanaa.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ekiraanaa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SellerRegistrationActivity extends AppCompatActivity implements LocationListener {
    private ImageButton btn_back, btn_gps;
    private ImageView btn_profile_pic;
    private EditText et_full_name, et_shop_name, et_deliver_fee, et_mobile, et_country, et_state, et_city, et_address, et_email, et_pswrd, et_c_pswrd;
    private Button btn_register;
    private TextView txt_seller_account;
    //Permission Constants
    private static final int Location_Request_code = 100;
    private static final int Camera_Request_code = 200;
    private static final int Storage_Request_code = 300;
    private static final int Image_pick_gallery_code=400;
    private static final int Image_pick_camera_code=500;


    //Permisssion Arrrays
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private Uri image_uri;

    private LocationManager locationManager;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);
        btn_back = findViewById(R.id.btn_back);
        btn_gps = findViewById(R.id.btn_gps);
        btn_profile_pic = findViewById(R.id.btn_profile_pic);
        et_full_name = findViewById(R.id.et_full_name);
        et_address = findViewById(R.id.et_address);
        et_shop_name = findViewById(R.id.et_shop_name);
        et_deliver_fee = findViewById(R.id.et_delivery_fee);
        et_mobile = findViewById(R.id.et_mobile);
        et_country = findViewById(R.id.et_country);
        et_state = findViewById(R.id.et_state);
        et_city = findViewById(R.id.et_city);
        et_email = findViewById(R.id.et_email);
        et_pswrd = findViewById(R.id.et_pswrd);
        et_c_pswrd = findViewById(R.id.et_c_pswrd);
        txt_seller_account = findViewById(R.id.txt_seller_account);

        btn_register = findViewById(R.id.btn_seller_register);


        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //accessloocation
                if (checkLocationPermission()) {
                    detectLocation();
                } else {
                    requestLocationPermission();
                }

            }
        });
        btn_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take photo
                showImagePickdialog();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });


    }
    private String fullName,shopName,phoneNo,deliveryFee,country,state,city,address,email,password,confirmPasseord;
    private void inputData() {
        fullName=et_full_name.getText().toString().trim();
        shopName=et_shop_name.getText().toString().trim();
        phoneNo=et_mobile.getText().toString().trim();
        deliveryFee=et_deliver_fee.getText().toString().trim();
        country=et_country.getText().toString().trim();
        state=et_state.getText().toString().trim();
        city=et_city.getText().toString().trim();
        address=et_address.getText().toString().trim();
        email=et_email.getText().toString().trim();
        password=et_pswrd.getText().toString().trim();
        confirmPasseord=et_c_pswrd.getText().toString().trim();
        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this,"Name could not be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(shopName)){
            Toast.makeText(this,"Shop Name could not be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phoneNo)){
            Toast.makeText(this,"Mobile could not be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(deliveryFee)){
            Toast.makeText(this,"Delivery Fee could not be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(latitude==0.0||longitude==0.0){
            Toast.makeText(this,"Please Cilck on GPS button to Access Location",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(this,"PasswordMust be atleast 6 characters long",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(confirmPasseord)){
            Toast.makeText(this,"password does not match",Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Creating Account......");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saveFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SellerRegistrationActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveFirebaseData() {
        progressDialog.setMessage("Sacing Account Info....");
        progressDialog.show();
        String timestamp=""+System.currentTimeMillis();
        if(image_uri==null){
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("name",""+fullName);
            hashMap.put("shopName",""+shopName);
            hashMap.put("phone",""+phoneNo);
            hashMap.put("deliveryFee",""+deliveryFee);
            hashMap.put("country",""+country);
            hashMap.put("state",""+state);
            hashMap.put("city",""+city);
            hashMap.put("address",""+address);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);
            hashMap.put("timeStamp",""+timestamp);
            hashMap.put("accountType","Seller");
            hashMap.put("online","true");
            hashMap.put("shopOpen","true");
            hashMap.put("profileImage","");
            //Saving to database

            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            startActivity(new Intent(SellerRegistrationActivity.this,MainSellerActivity.class));
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(SellerRegistrationActivity.this,MainSellerActivity.class));
                            finish();

                        }
                    });
        }
        else{
            String filePathAndName="profile_images/"+""+firebaseAuth.getUid();
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    Uri downloadImageUri=uriTask.getResult();
                    if(uriTask.isSuccessful()){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("uid",""+firebaseAuth.getUid());
                        hashMap.put("email",""+email);
                        hashMap.put("name",""+fullName);
                        hashMap.put("shopName",""+shopName);
                        hashMap.put("phone",""+phoneNo);
                        hashMap.put("deliveryFee",""+deliveryFee);
                        hashMap.put("country",""+country);
                        hashMap.put("state",""+state);
                        hashMap.put("city",""+city);
                        hashMap.put("address",""+address);
                        hashMap.put("latitude",""+latitude);
                        hashMap.put("longitude",""+longitude);
                        hashMap.put("timeStamp",""+timestamp);
                        hashMap.put("accountType","Seller");
                        hashMap.put("online","true");
                        hashMap.put("shopOpen","true");
                        hashMap.put("profileImage",""+downloadImageUri);
                        //Saving to database

                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
                        ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(SellerRegistrationActivity.this,MainSellerActivity.class));
                                        finish();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(SellerRegistrationActivity.this,MainSellerActivity.class));
                                        finish();

                                    }
                                });

                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                      Toast.makeText(SellerRegistrationActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void detectLocation() {
        Toast.makeText(this, "Detecting Location", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        private Boolean checkLocationPermission () {
            boolean result = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
            return result;
        }


    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, Location_Request_code);
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
        startActivityForResult(intent,Image_pick_gallery_code);
    }

    private void pick_from_camera(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_ Image Description");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent=new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,Image_pick_camera_code);
    }

    private boolean check_storage_permission(){
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,Storage_Request_code);

    }

    private boolean check_camera_permission(){
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,Camera_Request_code);

    }

    private void findAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String Country = addresses.get(0).getCountryName();
            et_country.setText(Country);
            et_state.setText(state);
            et_city.setText(city);
            et_address.setText(address);

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        findAddress();

    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //permission denied
        Toast.makeText(this, "Enable Location Service", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Location_Request_code: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        //Permission Allowed
                        detectLocation();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Location Permission Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case Camera_Request_code: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pick_from_camera();
                        //Permission Allowed
                    } else {
                        //permission denied

                        Toast.makeText(this, "Camera Permissions Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case Storage_Request_code: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("storage value",""+storageAccepted);
                    Log.d("grant results",""+grantResults[1]);
                    Log.d("package manager",""+PackageManager.PERMISSION_GRANTED);
                    if (storageAccepted) {
                        //Permission Allowed
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
            if(requestCode==Image_pick_gallery_code){
                image_uri=data.getData();
                btn_profile_pic.setImageURI(image_uri);

            }
            else if(requestCode==Image_pick_camera_code){
                image_uri=data.getData();
                btn_profile_pic.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}




