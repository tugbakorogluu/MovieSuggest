//Bu sayfa, kullanıcıların profil bilgilerini görüntüleyip güncellemelerini sağlayan,
// şifre değiştirme ve çıkış yapma işlemlerini gerçekleştiren bir profil yönetim ekranıdır.

package com.example.project155.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project155.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView profileName, profileEmail;
    private EditText editProfileName, editProfilePassword;
    private Button saveChangesBtn, logoutBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private FirebaseUser mUser;
    private String currentEmail;
    private String currentPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("Kullanıcılar");

        // Initialize views
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        editProfileName = findViewById(R.id.editProfileName);
        editProfilePassword = findViewById(R.id.editProfilePassword);
        saveChangesBtn = findViewById(R.id.saveChangesBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // EditText'in default değerini temizle
        editProfileName.setText("");

        // Load user data
        loadUserData();

        // Set up click listeners
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, IntroActivity.class));
                finish();
            }
        });
    }
    private void loadUserData() {
        if (mUser != null) {
            String userId = mUser.getUid();
            currentEmail = mUser.getEmail();

            mReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName = snapshot.child("UserName").getValue(String.class);
                        String userEmail = snapshot.child("UserMailAddresses").getValue(String.class);
                        currentPassword = snapshot.child("UserPassword").getValue(String.class);

                        profileName.setText("Name: " + (userName != null ? userName : "No Name"));
                        profileEmail.setText("Email: " + (userEmail != null ? userEmail : "No Email"));

                        // Hint olarak mevcut kullanıcı adını ayarla
                        editProfileName.setHint("Edit your name");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void updateUserProfile() {
        final String newName = editProfileName.getText().toString().trim();
        final String newPassword = editProfilePassword.getText().toString().trim();

        if (mUser != null) {
            String userId = mUser.getUid();

            // Update name if changed
            if (!TextUtils.isEmpty(newName)) {
                mReference.child(userId).child("UserName").setValue(newName)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    profileName.setText("Name: " + newName);
                                    editProfileName.setText("");
                                    Toast.makeText(ProfileActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            // Update password if changed
            if (!TextUtils.isEmpty(newPassword)) {
                // Kullanıcının mevcut email ve şifresi ile reauthentication
                AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, currentPassword);

                mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update password in Authentication
                            mUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Update password in Realtime Database
                                        mReference.child(userId).child("UserPassword").setValue(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            editProfilePassword.setText("");
                                                            Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();

                                                            // Sign out user after password change
                                                            mAuth.signOut();
                                                            startActivity(new Intent(ProfileActivity.this, IntroActivity.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(ProfileActivity.this, "Failed to update password in database", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Failed to update password in authentication", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}