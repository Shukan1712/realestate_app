package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListingDetailsActivity extends AppCompatActivity {

    private String listingId;
    private DatabaseReference listingsRef;
    private DatabaseReference userFavRef;
    private Listing currentListing;
    private List<String> allListingIds = new ArrayList<>();
    private String userId = "testUser01";
    private boolean isFavourite = false;

    // UI Elements
    private TextView txtTitle, txtBedBath, txtPrice, txtAddress, txtDescription, txtMonthlyPayment;
    private ImageView btnBack, btnHome, btnFavourite, btnCall, btnEmail;
    private Button btnScheduleVisit, btnPrevListing, btnNextListing, btnCalculate;
    private ViewPager2 viewPager;
    private EditText edtHomePrice, edtDownPayment, edtInterestRate, edtLoanTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);

        listingId = getIntent().getStringExtra("listingId");
        if (listingId == null) {
            Toast.makeText(this, "Error: No listing found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Views
        txtTitle = findViewById(R.id.txtTitle);
        txtBedBath = findViewById(R.id.txtBedBath);
        txtPrice = findViewById(R.id.txtPrice);
        txtAddress = findViewById(R.id.txtAddress);
        txtDescription = findViewById(R.id.txtDescription);
        viewPager = findViewById(R.id.viewPager);
        
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
        btnFavourite = findViewById(R.id.btnFavourite);
        btnCall = findViewById(R.id.btnCall);
        btnEmail = findViewById(R.id.btnEmail);
        btnScheduleVisit = findViewById(R.id.btnScheduleVisit);
        
        btnPrevListing = findViewById(R.id.btnPrevListing);
        btnNextListing = findViewById(R.id.btnNextListing);
        
        // Mortgage Calculator Views
        edtHomePrice = findViewById(R.id.edtHomePrice);
        edtDownPayment = findViewById(R.id.edtDownPayment);
        edtInterestRate = findViewById(R.id.edtInterestRate);
        edtLoanTerm = findViewById(R.id.edtLoanTerm);
        btnCalculate = findViewById(R.id.btnCalculate);
        txtMonthlyPayment = findViewById(R.id.txtMonthlyPayment);

        // Initialize Firebase
        listingsRef = FirebaseDatabase.getInstance().getReference("listings");
        userFavRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favourites").child(listingId);

        loadListingDetails(listingId);
        loadFavouriteStatus();
        loadAllListingIds();

        // Navigation Listeners
        btnBack.setOnClickListener(v -> finish());
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Toggle Favourite
        btnFavourite.setOnClickListener(v -> toggleFavourite());

        // Call Action
        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            // Use fake number if none exists
            String phone = "5551234567"; 
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        // Email Action
        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); 
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"agent@example.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry about " + (currentListing != null ? currentListing.title : "Listing"));
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        // Schedule Visit
        btnScheduleVisit.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectDateActivity.class);
            intent.putExtra("listingId", listingId); 
            startActivity(intent);
        });
        
        // Mortgage Calculation
        btnCalculate.setOnClickListener(v -> calculateMortgage());
        
        // Next/Prev Navigation
        btnNextListing.setOnClickListener(v -> navigateListing(1));
        btnPrevListing.setOnClickListener(v -> navigateListing(-1));
    }

    private void loadListingDetails(String id) {
        listingsRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentListing = snapshot.getValue(Listing.class);
                if (currentListing != null) {
                    currentListing.id = snapshot.getKey();
                    updateUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListingDetailsActivity.this, "Failed to load listing", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFavouriteStatus() {
        userFavRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isFavourite = snapshot.exists();
                updateFavouriteIcon();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    
    private void loadAllListingIds() {
        listingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allListingIds.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    allListingIds.add(snap.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateUI() {
        txtTitle.setText(currentListing.title);
        txtBedBath.setText(currentListing.bedrooms + " Bed, " + currentListing.bathrooms + " Bath");
        txtPrice.setText("$" + String.format("%,.0f", currentListing.price));
        txtAddress.setText(currentListing.address);
        txtDescription.setText(currentListing.description);

        // Image Carousel
        // FIX: Use 'photos' field instead of 'imageUrls'
        if (currentListing.photos != null && !currentListing.photos.isEmpty()) {
            ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, currentListing.photos);
            viewPager.setAdapter(sliderAdapter);
        }
        
        // Pre-fill Mortgage Calculator
        edtHomePrice.setText(String.valueOf(currentListing.price));
        edtDownPayment.setText(String.valueOf(currentListing.price * 0.20)); // 20% down default
        edtInterestRate.setText("5.5"); // Default rate
        edtLoanTerm.setText("30"); // Default term
    }

    private void updateFavouriteIcon() {
        if (isFavourite) {
            btnFavourite.setImageResource(android.R.drawable.star_big_on);
        } else {
            btnFavourite.setImageResource(android.R.drawable.star_big_off);
        }
    }

    private void toggleFavourite() {
        boolean newState = !isFavourite;
        
        if (newState) {
            userFavRef.setValue(true);
            showSnackbar("✔ Added to favourites", true);
        } else {
            userFavRef.removeValue();
            showSnackbar("Removed from favourites", false);
        }
    }

    private void showSnackbar(String message, boolean added) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                    // Revert state
                    if (added) {
                        userFavRef.removeValue(); // Undo add -> remove
                    } else {
                        userFavRef.setValue(true); // Undo remove -> add
                    }
                })
                .show();
    }
    
    private void navigateListing(int direction) {
        if (allListingIds.isEmpty()) return;
        
        int currentIndex = allListingIds.indexOf(listingId);
        if (currentIndex == -1) return;
        
        int newIndex = currentIndex + direction;
        
        if (newIndex >= 0 && newIndex < allListingIds.size()) {
            String newId = allListingIds.get(newIndex);
            Intent intent = new Intent(this, ListingDetailsActivity.class);
            intent.putExtra("listingId", newId);
            startActivity(intent);
            finish(); 
        } else {
            Toast.makeText(this, direction > 0 ? "End of list" : "Start of list", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void calculateMortgage() {
        try {
            double price = Double.parseDouble(edtHomePrice.getText().toString());
            double downPayment = Double.parseDouble(edtDownPayment.getText().toString());
            double rate = Double.parseDouble(edtInterestRate.getText().toString());
            int years = Integer.parseInt(edtLoanTerm.getText().toString());
            
            double principal = price - downPayment;
            double monthlyRate = rate / 100 / 12;
            int months = years * 12;
            
            if (monthlyRate == 0) {
                double payment = principal / months;
                txtMonthlyPayment.setText("Monthly Payment: $" + String.format("%,.2f", payment));
            } else {
                double payment = (principal * monthlyRate * Math.pow(1 + monthlyRate, months)) / (Math.pow(1 + monthlyRate, months) - 1);
                txtMonthlyPayment.setText("Monthly Payment: $" + String.format("%,.2f", payment));
            }
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}
