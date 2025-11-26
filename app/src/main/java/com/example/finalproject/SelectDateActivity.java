package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

public class SelectDateActivity extends AppCompatActivity {

    CalendarView calendarView;
    Button btnContinue;
    String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDate);
        toolbar.setNavigationOnClickListener(v -> finish());

        String agentId = getIntent().getStringExtra("agentId");

        calendarView = findViewById(R.id.calendarView);
        btnContinue = findViewById(R.id.btnContinue);

        // Listen for date selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Convert into readable format: DD/MM/YYYY
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        });

        // Continue to Time screen
        btnContinue.setOnClickListener(v -> {

            // If no date selected, use today's date or force selection
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please confirm a date by tapping it.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(SelectDateActivity.this, SelectTimeActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            intent.putExtra("agentId", agentId);

            startActivity(intent);
        });
    }
}
