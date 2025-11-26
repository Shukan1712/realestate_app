package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class BookingConfirmationActivity extends AppCompatActivity {

    TextView txtDate, txtTime;
    Button btnDone, btnGoHome;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        String agentId = getIntent().getStringExtra("agentId");
        String selectedDate = getIntent().getStringExtra("selectedDate");
        String selectedTime = getIntent().getStringExtra("selectedTime");

        // Validating inputs
        if (agentId == null) agentId = "unknown_agent";
        if (selectedDate == null) selectedDate = "unknown_date";
        if (selectedTime == null) selectedTime = "unknown_time";

        DatabaseReference bookingRef = FirebaseDatabase.getInstance()
                .getReference("bookings")
                .child(agentId)
                .child(selectedDate)
                .child(selectedTime.replace(" ", "_"));

        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        btnDone = findViewById(R.id.btnDone);
        btnGoHome = findViewById(R.id.btnGoHome);
        btnBack = findViewById(R.id.btnBack);

        txtDate.setText("Date: " + selectedDate);
        txtTime.setText("Time: " + selectedTime);

        // DONE BUTTON → go back
        btnDone.setOnClickListener(v -> finish());

        // GO HOME BUTTON → go to MainActivity
        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(BookingConfirmationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        
        // Back Button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Check existing booking
        String finalSelectedDate = selectedDate;
        String finalSelectedTime = selectedTime;
        
        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    txtDate.setText("Date: " + finalSelectedDate);
                    txtTime.setText("Time: " + finalSelectedTime + "  ⚠ Already Booked");

                    btnDone.setEnabled(false);

                    Toast.makeText(BookingConfirmationActivity.this,
                            "This time slot is already booked.",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Create new booking
                    bookingRef.setValue(true);
                    Toast.makeText(BookingConfirmationActivity.this,
                            "Booking Confirmed!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(BookingConfirmationActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
