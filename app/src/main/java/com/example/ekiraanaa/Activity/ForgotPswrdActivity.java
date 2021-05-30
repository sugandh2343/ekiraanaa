package com.example.ekiraanaa.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ekiraanaa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPswrdActivity extends AppCompatActivity {
    private ImageButton btn_back;
    private EditText et_email;
    private Button btn_forgot_pswrd;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pswrd);
        btn_back=findViewById(R.id.btn_back);
        et_email=findViewById(R.id.et_email);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        btn_forgot_pswrd=findViewById(R.id.btn_forgot_pswrd);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_forgot_pswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Recovery Email Bhejna Hai
                recoverPassword();
            }
        });
    }
    private String email;

    private void recoverPassword() {
        email=et_email.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Sending Instructions to Reset password");
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ForgotPswrdActivity.this,"Reset Password Link sent to mail",Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPswrdActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }
}