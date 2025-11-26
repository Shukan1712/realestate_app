package com.example.finalproject;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView rvFavourites;
    private ListingAdapter adapter;
    private ArrayList<Listing> favouriteListings;
    private LinearLayout emptyState;
    
    private DatabaseReference userFavRef;
    private DatabaseReference listingsRef;
    private String userId = "testUser01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // We use custom title in layout
        }

        rvFavourites = findViewById(R.id.rvFavourites);
        emptyState = findViewById(R.id.emptyState);
        
        rvFavourites.setLayoutManager(new LinearLayoutManager(this));
        favouriteListings = new ArrayList<>();
        // Pass true to indicate this is the Favourites screen
        adapter = new ListingAdapter(this, favouriteListings, true);
        rvFavourites.setAdapter(adapter);

        userFavRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favourites");
        listingsRef = FirebaseDatabase.getInstance().getReference("listings");

        loadFavourites();
    }

    private void loadFavourites() {
        userFavRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favouriteListings.clear();
                final ArrayList<String> favIds = new ArrayList<>();
                
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (Boolean.TRUE.equals(snap.getValue(Boolean.class))) {
                        favIds.add(snap.getKey());
                    }
                }

                if (favIds.isEmpty()) {
                    updateUI();
                } else {
                    // Fetch details for each ID
                    for (String id : favIds) {
                        fetchListingDetails(id, favIds.size());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private int loadedCount = 0;
    private void fetchListingDetails(String listingId, int total) {
        listingsRef.child(listingId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Listing listing = snapshot.getValue(Listing.class);
                if (listing != null) {
                    listing.id = snapshot.getKey();
                    favouriteListings.add(listing);
                }
                
                loadedCount++;
                // When all possible fetches are done (even if some null), update UI
                // Note: In a real app with many items, this sync approach might be improved.
                // For now, we just notify adapter as items come in or use a simpler counter.
                adapter.notifyDataSetChanged();
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateUI() {
        if (favouriteListings.isEmpty()) {
            rvFavourites.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvFavourites.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
