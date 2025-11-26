package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Update fake listings with static images so they don't change on every load
        // This runs once to fix the database, then you can comment it out if you want.
        addFakeListings(); 

        // Custom Toolbar Logic
        ImageView btnProfile = findViewById(R.id.btnProfile);
        ImageView btnAddListing = findViewById(R.id.btnAddListing);
        ImageView btnCart = findViewById(R.id.btnCart);
        ImageView btnFilter = findViewById(R.id.btnFilter);
        ImageView btnMenu = findViewById(R.id.btnMenu);

        // Default Fragment
        loadFragment(new HomeFragment());

        // Listeners
        btnAddListing.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddListingActivity.class); // Updated to new Activity
            startActivity(intent);
        });

        btnMenu.setOnClickListener(this::showPopupMenu);
        
        // Open UserProfileActivity on Profile Click
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });
        
        btnCart.setOnClickListener(v -> Toast.makeText(this, "Cart Clicked", Toast.LENGTH_SHORT).show());
        btnFilter.setOnClickListener(v -> Toast.makeText(this, "Filter Clicked", Toast.LENGTH_SHORT).show());
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_kelowna) {
                // Load ListingsFragment (Listings in Kelowna)
                Toast.makeText(this, "Listings in Kelowna", Toast.LENGTH_SHORT).show();
                // loadFragment(new ListingsFragment()); 
                return true;
            } else if (id == R.id.action_favourites) {
                // Check login before showing favourites
                boolean isLoggedIn = getSharedPreferences("AppPrefs", MODE_PRIVATE).getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    Intent intent = new Intent(MainActivity.this, FavouritesActivity.class);
                    startActivity(intent);
                } else {
                    // Not logged in, redirect to Profile to login/signup
                    Toast.makeText(this, "Please login to view favourites", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                }
                return true;
            } else if (id == R.id.action_agents) {
                // Load Agents Fragment (LocalExpertsFragment as requested before)
                loadFragment(new LocalExpertsFragment());
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // This method adds fake data. 
    // Using 'seed' in picsum URLs ensures the image remains consistent (static) for that seed.
    private void addFakeListings() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("listings");
        
        // Listing 1: Luxury Lakefront
        String id1 = "kelowna_01";
        Listing l1 = new Listing(id1, "Luxury Lakefront Villa", 1250000.0, "123 Abbott St", "Kelowna", "BC", "V1Y 1A1", 
                "Stunning views of Okanagan Lake. 4 bedrooms, modern kitchen.", 
                4, 3, 2500, "House", "555-0101", "agent1@example.com", System.currentTimeMillis(),
                49.8821, -119.4956, false, 
                Arrays.asList("https://picsum.photos/seed/k1/400/300", "https://picsum.photos/seed/k1_2/400/300"));
        
        // Listing 2: Downtown Condo
        String id2 = "kelowna_02";
        Listing l2 = new Listing(id2, "Modern Downtown Condo", 450000.0, "456 Bernard Ave", "Kelowna", "BC", "V1Y 6N6", 
                "Steps away from restaurants and the beach. Perfect for young professionals.", 
                2, 1, 900, "Condo", "555-0102", "agent2@example.com", System.currentTimeMillis(),
                49.8880, -119.4940, false, 
                Arrays.asList("https://picsum.photos/seed/k2/401/300"));

        // Listing 3: Family Home in Rutland
        String id3 = "kelowna_03";
        Listing l3 = new Listing(id3, "Cozy Family Home", 675000.0, "789 Rutland Rd N", "Kelowna", "BC", "V1X 3B8", 
                "Spacious backyard, close to schools and parks. Recently renovated.", 
                3, 2, 1800, "House", "555-0103", "agent3@example.com", System.currentTimeMillis(),
                49.8950, -119.3800, false, 
                Arrays.asList("https://picsum.photos/seed/k3/402/300"));

        // Listing 4: Mission Estate
        String id4 = "kelowna_04";
        Listing l4 = new Listing(id4, "Exclusive Mission Estate", 1850000.0, "101 Lakeshore Rd", "Kelowna", "BC", "V1W 3S9", 
                "Private estate with pool and vineyard access.", 
                5, 4, 3500, "House", "555-0104", "agent4@example.com", System.currentTimeMillis(),
                49.8350, -119.4850, false, 
                Arrays.asList("https://picsum.photos/seed/k4/403/300"));

        // Listing 5: Student Rental near UBCO
        String id5 = "kelowna_05";
        Listing l5 = new Listing(id5, "Investment Property / Student Rental", 550000.0, "Academy Way", "Kelowna", "BC", "V1V 2Z9", 
                "Walking distance to UBCO. Great rental income potential.", 
                2, 2, 1100, "Apartment", "555-0105", "agent5@example.com", System.currentTimeMillis(),
                49.9390, -119.3950, false, 
                Arrays.asList("https://picsum.photos/seed/k5/404/300"));

        // Listing 6: Glenmore Townhouse
        String id6 = "kelowna_06";
        Listing l6 = new Listing(id6, "Spacious Glenmore Townhouse", 620000.0, "300 Yates Rd", "Kelowna", "BC", "V1V 2R6",
                "Modern townhouse in a family-friendly neighborhood. Close to schools and parks.",
                3, 2, 1500, "Townhouse", "555-0106", "agent6@example.com", System.currentTimeMillis(),
                49.9050, -119.4450, false,
                Arrays.asList("https://picsum.photos/seed/k6/405/300"));

        // Listing 7: Black Mountain View Home
        String id7 = "kelowna_07";
        Listing l7 = new Listing(id7, "Black Mountain View Home", 950000.0, "1250 Black Mountain Dr", "Kelowna", "BC", "V1P 1P7",
                "Panoramic valley views. Large deck and open concept living area.",
                4, 3, 2800, "House", "555-0107", "agent7@example.com", System.currentTimeMillis(),
                49.8800, -119.3500, false,
                Arrays.asList("https://picsum.photos/seed/k7/406/300"));

        // Listing 8: West Kelowna Winery Estate
        String id8 = "kelowna_08";
        Listing l8 = new Listing(id8, "Vineyard Estate", 2100000.0, "2200 Boucherie Rd", "West Kelowna", "BC", "V1Z 2E6",
                "Live among the vines. Private wine cellar and tasting room.",
                5, 5, 4000, "Other", "555-0108", "agent8@example.com", System.currentTimeMillis(),
                49.8400, -119.5500, false,
                Arrays.asList("https://picsum.photos/seed/k8/407/300"));

        // Listing 9: Lower Mission Cottage
        String id9 = "kelowna_09";
        Listing l9 = new Listing(id9, "Charming Lower Mission Cottage", 800000.0, "4400 Gordon Dr", "Kelowna", "BC", "V1W 1S6",
                "Short walk to the beach and H2O center. Heritage style home.",
                3, 2, 1600, "House", "555-0109", "agent9@example.com", System.currentTimeMillis(),
                49.8500, -119.4800, false,
                Arrays.asList("https://picsum.photos/seed/k9/408/300"));

        // Listing 10: Kettle Valley Family Home
        String id10 = "kelowna_10";
        Listing l10 = new Listing(id10, "Kettle Valley Dream Home", 1100000.0, "5600 Chute Lake Rd", "Kelowna", "BC", "V1W 4L8",
                "Award winning community. Parks, trails and school nearby.",
                4, 4, 3200, "House", "555-0110", "agent10@example.com", System.currentTimeMillis(),
                49.8100, -119.4900, false,
                Arrays.asList("https://picsum.photos/seed/k10/409/300"));

        // Listing 11: Dilworth Mountain Executive
        String id11 = "kelowna_11";
        Listing l11 = new Listing(id11, "Dilworth Executive Home", 1300000.0, "900 Dilworth Dr", "Kelowna", "BC", "V1Y 9N8",
                "Central location with city views. High end finishes throughout.",
                5, 3, 3000, "House", "555-0111", "agent11@example.com", System.currentTimeMillis(),
                49.8900, -119.4200, false,
                Arrays.asList("https://picsum.photos/seed/k11/410/300"));

        // Listing 12: Big White Ski Condo
        String id12 = "kelowna_12";
        Listing l12 = new Listing(id12, "Ski-in/Ski-out Condo", 420000.0, "Big White Rd", "Kelowna", "BC", "V1P 1P3",
                "Perfect winter getaway. Hot tub and fireplace.",
                2, 1, 850, "Condo", "555-0112", "agent12@example.com", System.currentTimeMillis(),
                49.7200, -118.9300, false,
                Arrays.asList("https://picsum.photos/seed/k12/411/300"));


        ref.child(id1).setValue(l1);
        ref.child(id2).setValue(l2);
        ref.child(id3).setValue(l3);
        ref.child(id4).setValue(l4);
        ref.child(id5).setValue(l5);
        ref.child(id6).setValue(l6);
        ref.child(id7).setValue(l7);
        ref.child(id8).setValue(l8);
        ref.child(id9).setValue(l9);
        ref.child(id10).setValue(l10);
        ref.child(id11).setValue(l11);
        ref.child(id12).setValue(l12);
    }
}
