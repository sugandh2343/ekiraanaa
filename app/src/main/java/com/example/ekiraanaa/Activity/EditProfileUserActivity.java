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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ekiraanaa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditProfileUserActivity extends AppCompatActivity implements LocationListener {
    private ImageButton btn_back, btn_gps;
    private ImageView btn_profile;
    private EditText et_full_name, et_mobile, et_country, et_state, et_city, et_address;
    private Button btn_update;

    //Permission Constants
    private static final int location_request_code = 100;
    private static final int camera_request_code = 200;
    private static final int storage_request_code = 300;
    //image,lcation pick constants
    private static final int image_pick_gallery_code = 400;
    private static final int image_pick_camera__code = 500;
    //permission array
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private FirebaseAuth firebaseAuth;
    private LocationManager locationManager;
    private ProgressDialog progressDialog;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_user);
        btn_back = findViewById(R.id.btn_back);
        btn_gps = findViewById(R.id.btn_gps);
        btn_profile =findViewById(R.id.btn_profile);
        btn_update=findViewById(R.id.btn_update);
        et_full_name = findViewById(R.id.et_full_name);
        et_mobile = findViewById(R.id.et_mobile);
        et_country = findViewById(R.id.et_country);
        et_state = findViewById(R.id.et_state);
        et_city = findViewById(R.id.et_city);
        et_address = findViewById(R.id.et_address);
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkuser();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detectLoacation
                if (!checkLocationPermission()) {
                    //already allowed
                    detectLocation();
                } else {
                    //request permission
                    requestLocationPermission();
                }
            }
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update
                inputData();
            }
        });

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update Image
                showImagePickDialog();
            }
        });
    }
    private String name,phone,country,state,city,address;
    private void inputData(){
        name=et_full_name.getText().toString().trim();

        phone=et_mobile.getText().toString().trim();

        country=et_country.getText().toString().trim();
        state=et_state.getText().toString().trim();
        city=et_city.getText().toString().trim();
        address=et_address.getText().toString().trim();

        updateProfile();

    }
    private void updateProfile(){
        progressDialog.setMessage("Updationg Profile");
        progressDialog.show();

        if(image_uri==null){
            //without image
            HashMap<String, Object> hashMap=new HashMap<>();
            hashMap.put("name",""+name);
            hashMap.put("phone",""+phone);
            hashMap.put("country",""+country);
            hashMap.put("state",""+state);
            hashMap.put("city",""+city);
            hashMap.put("address",""+address);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);


            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileUserActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfileUserActivity.this,MainUserActivity.class));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileUserActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }
        else{
            //with Image
            String filePathandName="profile_images/"+""+firebaseAuth.getUid();
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathandName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri=uriTask.getResult();
                            if(uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap=new HashMap<>();
                                hashMap.put("name",""+name);
                                hashMap.put("phone",""+phone);
                                hashMap.put("country",""+country);
                                hashMap.put("state",""+state);
                                hashMap.put("city",""+city);
                                hashMap.put("address",""+address);
                                hashMap.put("latitude",""+latitude);
                                hashMap.put("longitude",""+longitude);
                                hashMap.put("profileImage",""+downloadImageUri);

                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditProfileUserActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditProfileUserActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileUserActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
    private void checkuser(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        else{
            loadMyinfo();
        }
    }
    private void loadMyinfo(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String accountType=""+ds.child("accountType").getValue();
                            String city=""+ds.child("city").getValue();
                            String country=""+ds.child("country").getValue();
                            String state=""+ds.child("state").getValue();
                            String address=""+ds.child("address").getValue();
                            String email=""+ds.child("email").getValue();
                            latitude= Double.parseDouble(""+ds.child("latitude").getValue());
                            longitude= Double.parseDouble(""+ds.child("longitude").getValue());
                            String name=""+ds.child("name").getValue();
                            String online=""+ds.child("online").getValue();
                            String phone=""+ds.child("phone").getValue();
                            String profileImage=""+ds.child("profileImage").getValue();
                            String timeStamp=""+ds.child("timeStamp").getValue();

                            String uid=""+ds.child("uid").getValue();
                            et_full_name.setText(name);
                            et_mobile.setText(phone);
                            et_country.setText(country);
                            et_state.setText(state);
                            et_city.setText(city);
                            et_address.setText(address);



                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store).into(btn_profile);
                            }
                            catch(Exception e){
                                btn_profile.setImageResource(R.drawable.ic_person_100_dp);

                            }




                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, location_request_code);

    }

    private void detectLocation() {
        Toast.makeText(this,"Detecting Location",Toast.LENGTH_SHORT).show();
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


    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);


        return result;
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
    private void showImagePickDialog() {
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

    private boolean check_camera_permission() {
        boolean result=ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

    return result && result1;
    }

    private void requestStoragePermission() {

    }

    private void pick_from_gallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,image_pick_camera__code);
    }

    private boolean check_storage_permission() {
        return  true;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,locationPermissions,location_request_code);

    }

    private void pick_from_camera(){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Image Titile");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Image Description");

        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,image_pick_camera__code);

    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
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
        Toast.makeText(this, "Enable Location Service", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case location_request_code: {
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
            case camera_request_code: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //Permission Allowed
                        pick_from_camera();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Camera  Permissions are Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case storage_request_code: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        //Permission Allowed
                        pick_from_gallery();
                    } else {
                        //permission denied
                        Toast.makeText(this, "Storage Permissuion Necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }


            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            if(resultCode==RESULT_OK){
            if(resultCode==image_pick_gallery_code){
                image_uri=data.getData();
                btn_profile.setImageURI(image_uri);

            }
            else if(requestCode==image_pick_camera__code){
                btn_profile.setImageURI(image_uri);

            }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

