//Bu sayfa, kullanıcıdan e-posta, şifre ve kullanıcı adı alarak
// Firebase Authentication ile kayıt işlemi yapar ve kullanıcı bilgilerini
// Firebase Realtime Database'e kaydeder.

package com.example.project155.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;


import com.example.project155.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText  emailEdt, passwordEdt, userNameEdt;
    private String  emailTxt, passwordTxt, userNameTxt;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private HashMap <String, Object> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEdt = (EditText)findViewById(R.id.editTextEmail);
        passwordEdt = (EditText)findViewById(R.id.editTextPassword);
        userNameEdt = (EditText)findViewById(R.id.editTextUserName);

        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference();

    }
    public void sign_up(View v)  {

        userNameTxt = userNameEdt.getText().toString();
        emailTxt = emailEdt.getText().toString();
        passwordTxt = passwordEdt.getText().toString();

        if(!TextUtils.isEmpty(userNameTxt) && !TextUtils.isEmpty(emailTxt) && !TextUtils.isEmpty(passwordTxt) ){
            mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mUser = mAuth.getCurrentUser();
                                mData= new HashMap<>();
                                mData.put("UserName", userNameTxt);
                                mData.put("UserMailAddresses", emailTxt);
                                mData.put("UserPassword", passwordTxt);
                                mData.put("UserId", mUser.getUid());


                                mReference.child("Kullanıcılar").child(mUser.getUid())
                                        .setValue(mData)
                                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                    Toast.makeText(RegisterActivity.this, "Registration is successful.", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                            else
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }else
            Toast.makeText(this, "Şifre ve Mail adresi boş olamaz!!", Toast.LENGTH_SHORT).show();
    }
}
