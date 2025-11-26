package com.example.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NewListingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    
    private EditText edtTitle, edtPrice, edtAddress, edtDescription, edtLat, edtLng;
    private NumberPicker npBedrooms, npBathrooms;
    private Button btnUploadImages, btnSubmit;
    private ImageView imgPreview, btnBack;
    
    private List<Uri> imageUris = new ArrayList<>();
    private List<String> uploadedImageUrls = new ArrayList<>();
    
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("listings");
        storageReference = FirebaseStorage.getInstance().getReference("listing_images");

        // Bind Views
        edtTitle = findViewById(R.id.edtTitle);
        edtPrice = findViewById(R.id.edtPrice);
        edtAddress = findViewById(R.id.edtAddress);
        edtDescription = findViewById(R.id.edtDescription);
        edtLat = findViewById(R.id.edtLat);
        edtLng = findViewById(R.id.edtLng);
        
        npBedrooms = findViewById(R.id.npBedrooms);
        npBathrooms = findViewById(R.id.npBathrooms);
        
        btnUploadImages = findViewById(R.id.btnUploadImages);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPreview = findViewById(R.id.imgPreview);
        btnBack = findViewById(R.id.btnBack);

        // Setup Number Pickers
        npBedrooms.setMinValue(0);
        npBedrooms.setMaxValue(10);
        npBathrooms.setMinValue(0);
        npBathrooms.setMaxValue(10);

        btnUploadImages.setOnClickListener(v -> openFileChooser());
        btnSubmit.setOnClickListener(v -> uploadImagesAndSave());
        
        // Back button functionality
        btnBack.setOnClickListener(v -> finish());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    imageUris.add(data.getClipData().getItemAt(i).getUri());
                }
                imgPreview.setImageURI(imageUris.get(0)); // Preview first image
                Toast.makeText(this, "Selected " + count + " images", Toast.LENGTH_SHORT).show();
            } else if (data.getData() != null) {
                imageUris.add(data.getData());
                imgPreview.setImageURI(data.getData());
            }
        }
    }

    private void uploadImagesAndSave() {
        if (imageUris.isEmpty()) {
            saveListing(); // Save without images if none selected (or handle as error)
            return;
        }

        Toast.makeText(this, "Uploading images...", Toast.LENGTH_SHORT).show();
        
        for (Uri imageUri : imageUris) {
            String fileName = UUID.randomUUID().toString();
            StorageReference fileRef = storageReference.child(fileName);
            
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadedImageUrls.add(uri.toString());
                        if (uploadedImageUrls.size() == imageUris.size()) {
                            saveListing();
                        }
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveListing() {
        String title = edtTitle.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String desc = edtDescription.getText().toString().trim();
        String latStr = edtLat.getText().toString().trim();
        String lngStr = edtLng.getText().toString().trim();
        
        if (title.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Title and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        double price = Double.parseDouble(priceStr);
        double lat = latStr.isEmpty() ? 0.0 : Double.parseDouble(latStr);
        double lng = lngStr.isEmpty() ? 0.0 : Double.parseDouble(lngStr);
        
        // Use default values for new fields to match new Listing constructor
        String city = "";
        String province = "";
        String postalCode = "";
        int sqFt = 0;
        String propertyType = "Unknown";
        String contactPhone = "";
        String contactEmail = "";
        long timestamp = System.currentTimeMillis();
        
        Listing listing = new Listing(id, title, price, address, city, province, postalCode, desc, 
                                      npBedrooms.getValue(), npBathrooms.getValue(), sqFt, propertyType,
                                      contactPhone, contactEmail, timestamp,
                                      lat, lng, false, uploadedImageUrls);
                                      
        if (id != null) {
            databaseReference.child(id).setValue(listing)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Listing Added!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
