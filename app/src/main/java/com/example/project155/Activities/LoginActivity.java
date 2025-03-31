//Bu sayfa, kullanıcıların e-posta ve şifre ile giriş yapmalarını sağlayan bir giriş ekranıdır.

package com.example.project155.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project155.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText userEdt, passEdt;
    private String  emailTxt, passwordTxt;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    public Button loginBtn, registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        initView();
    }
    private void initView() {
        userEdt = (EditText) findViewById(R.id.editTextText);
        passEdt = (EditText) findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        //startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

        loginBtn.setOnClickListener(v -> {

            emailTxt = userEdt.getText().toString();
            passwordTxt = passEdt.getText().toString();


            if (!userEdt.getText().toString().isEmpty() && !passEdt.getText().toString().isEmpty()) {
                mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                        .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                mUser= mAuth.getCurrentUser();
                                assert mUser != null;
                                System.out.println("Kullanıcı Adı: " +mUser.getDisplayName());
                                System.out.println("Kullanıcı Email : " +mUser.getEmail());
                                System.out.println("Kullanıcı Uid: " +mUser.getUid());

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }).addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            } else if (userEdt.getText().toString().isEmpty() || passEdt.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill your user and password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Your email and password is not correct", Toast.LENGTH_SHORT).show();
            }
        });




        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}



