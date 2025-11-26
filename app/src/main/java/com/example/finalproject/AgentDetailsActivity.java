package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

public class AgentDetailsActivity extends AppCompatActivity {

    ImageView imgLarge, btnCall, btnEmail;
    TextView txtName, txtRating, txtBio;
    Button btnSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_details);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        // This already handles the back button if the navigation icon is set in XML
        // or we can force set it here.
        toolbar.setNavigationIcon(R.drawable.ic_back); // Ensure we use our black back arrow
        toolbar.setNavigationOnClickListener(v -> finish());

        // Connect XML views
        imgLarge = findViewById(R.id.imgAgentLarge);
        txtName = findViewById(R.id.txtAgentName);
        txtRating = findViewById(R.id.txtAgentRating);
        txtBio = findViewById(R.id.txtAgentBio);
        btnCall = findViewById(R.id.btnCall);
        btnEmail = findViewById(R.id.btnEmail);
        btnSchedule = findViewById(R.id.btnSchedule);

        // Receive data from adapter
        String agentId = getIntent().getStringExtra("agentId");
        String name = getIntent().getStringExtra("agentName");
        double rating = getIntent().getDoubleExtra("agentRating", 0);
        String bio = getIntent().getStringExtra("agentBio");
        String image = getIntent().getStringExtra("agentImage");

        // Set values to UI
        txtName.setText(name);
        txtRating.setText("⭐ " + rating);
        txtBio.setText(bio != null ? bio : "No bio available.");

        // Load image
        if (image != null && !image.isEmpty()) {
            Picasso.get().load(image).placeholder(R.drawable.ic_launcher_background).into(imgLarge);
        }

        // Call button
        btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(android.net.Uri.parse("tel:+15555555555")); // Placeholder
            startActivity(intent);
        });


        // Email button
        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"agent@example.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry");
            try {
                startActivity(Intent.createChooser(intent, "Send email..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
            }
        });

        // Schedule Visit button
        btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(AgentDetailsActivity.this, SelectDateActivity.class);
            intent.putExtra("agentId", agentId);
            startActivity(intent);
        });
    }
}
