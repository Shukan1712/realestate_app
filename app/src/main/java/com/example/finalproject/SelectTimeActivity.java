package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectTimeActivity extends AppCompatActivity {

    TextView txtSelectedDate;
    String selectedDate = "";
    String agentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        MaterialToolbar toolbar = findViewById(R.id.toolbarTime);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Read values from previous screen
        agentId = getIntent().getStringExtra("agentId");
        selectedDate = getIntent().getStringExtra("selectedDate");

        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        txtSelectedDate.setText("Selected Date: " + selectedDate);

        // Time buttons
        Button btn9to10 = findViewById(R.id.btn9to10);
        Button btn10to11 = findViewById(R.id.btn10to11);
        Button btn11to12 = findViewById(R.id.btn11to12);
        Button btn12to1 = findViewById(R.id.btn12to1);

        // Firebase reference
        // Checking if this slot is already booked
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("bookings")
                .child(agentId != null ? agentId : "unknown_agent")
                .child(selectedDate != null ? selectedDate : "unknown_date");

        // Disable already booked slots
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("9:00_AM_-_10:00_AM")) disableSlot(btn9to10);
                if (snapshot.hasChild("10:00_AM_-_11:00_AM")) disableSlot(btn10to11);
                if (snapshot.hasChild("11:00_AM_-_12:00_PM")) disableSlot(btn11to12);
                if (snapshot.hasChild("12:00_PM_-_1:00_PM")) disableSlot(btn12to1);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Time slot click listeners
        btn9to10.setOnClickListener(v -> openConfirmation("9:00 AM - 10:00 AM"));
        btn10to11.setOnClickListener(v -> openConfirmation("10:00 AM - 11:00 AM"));
        btn11to12.setOnClickListener(v -> openConfirmation("11:00 AM - 12:00 PM"));
        btn12to1.setOnClickListener(v -> openConfirmation("12:00 PM - 1:00 PM"));
    }

    private void openConfirmation(String time) {
        Intent intent = new Intent(SelectTimeActivity.this, BookingConfirmationActivity.class);
        intent.putExtra("selectedDate", selectedDate);
        intent.putExtra("selectedTime", time);
        intent.putExtra("agentId", agentId);
        startActivity(intent);
    }

    private void disableSlot(Button btn) {
        btn.setEnabled(false);
        btn.setAlpha(0.4f);
        btn.setText("Booked");
    }
}
