package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    // Dummy Logic for "Mock" Authentication
    // In a real app, you would use FirebaseAuth.
    // Here we simulate it with SharedPreferences or a static variable, or just simple logic.
    // For this demo, we'll just use a simple local flag or check if user entered details.
    
    private LinearLayout layoutLogin, layoutProfile;
    private EditText etName, etEmail, etPhone, etPassword; // Password is dummy here
    private Button btnLogin, btnSignup, btnLogout;
    private TextView txtProfileName, txtProfileEmail;
    private ImageView btnBack;
    
    // We will store a simple "isLoggedIn" state in SharedPreferences for persistence across app restarts
    // But for simplicity in this prompt's context, we can just use a static user ID.
    // However, to meet the requirement "if not logged in, ask them", we need a way to check.
    // Let's use a simple shared preference helper or just local logic.
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        layoutLogin = findViewById(R.id.layoutLogin);
        layoutProfile = findViewById(R.id.layoutProfile);
        
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup); // We'll treat login/signup similarly for this mock
        btnLogout = findViewById(R.id.btnLogout);
        
        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        
        btnBack = findViewById(R.id.btnBack);

        checkLoginStatus();

        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // "Sign Up" logic - save to Firebase under a dummy or generated ID
            // For this mock, we'll use a fixed ID 'testUser01' or generate one, but the prompt says
            // "if they put id pass, they can see all saved favs".
            // We'll assume 'testUser01' is the shared account for simplicity of the "Favs" feature we built earlier.
            
            String userId = "testUser01"; 
            
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("profile");
            UserProfile profile = new UserProfile(userId, name, email, phone);
            
            userRef.setValue(profile).addOnSuccessListener(aVoid -> {
                // Save local state
                getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("userId", userId)
                        .putString("userName", name)
                        .putString("userEmail", email)
                        .apply();
                        
                Toast.makeText(this, "Profile Created!", Toast.LENGTH_SHORT).show();
                checkLoginStatus();
            });
        });
        
        btnLogin.setOnClickListener(v -> {
             // Mock login - just checking if fields are not empty
             String email = etEmail.getText().toString();
             String password = etPassword.getText().toString();
             
             if(email.isEmpty() || password.isEmpty()) {
                 Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                 return;
             }
             
             // In real app -> FirebaseAuth.signInWithEmailAndPassword
             // Here -> Just success
             String userId = "testUser01"; 
             getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("userId", userId)
                        .putString("userEmail", email)
                        .apply();
             Toast.makeText(this, "Logged In!", Toast.LENGTH_SHORT).show();
             checkLoginStatus();
        });

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().clear().apply();
            checkLoginStatus();
        });
        
        findViewById(R.id.btnViewFavs).setOnClickListener(v -> {
            startActivity(new Intent(this, FavouritesActivity.class));
        });
        
        // Back button listener
        btnBack.setOnClickListener(v -> finish());
    }

    private void checkLoginStatus() {
        boolean isLoggedIn = getSharedPreferences("AppPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false);
        
        if (isLoggedIn) {
            layoutLogin.setVisibility(View.GONE);
            layoutProfile.setVisibility(View.VISIBLE);
            
            String name = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("userName", "User");
            String email = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("userEmail", "");
            
            txtProfileName.setText("Welcome, " + name);
            txtProfileEmail.setText(email);
            
        } else {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutProfile.setVisibility(View.GONE);
        }
    }
}
