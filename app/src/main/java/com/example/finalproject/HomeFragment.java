package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private RecyclerView rvListings;
    private ListingAdapter adapter;
    private ArrayList<Listing> listingList;
    private GoogleMap mMap;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvListings = view.findViewById(R.id.rvListings);
        rvListings.setLayoutManager(new LinearLayoutManager(getContext()));
        listingList = new ArrayList<>();
        adapter = new ListingAdapter(getContext(), listingList);
        rvListings.setAdapter(adapter);

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        loadListings();

        return view;
    }

    private void loadListings() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("listings");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listingList.clear();
                if (mMap != null) mMap.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Listing listing = snap.getValue(Listing.class);
                    if (listing != null) {
                        listingList.add(listing);
                        
                        // Add marker to map
                        if (mMap != null && listing.lat != null && listing.lng != null) {
                            LatLng loc = new LatLng(listing.lat, listing.lng);
                            mMap.addMarker(new MarkerOptions().position(loc).title(listing.title).snippet("$" + listing.price));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                
                // Focus map on first item if available
                if (!listingList.isEmpty() && mMap != null && listingList.get(0).lat != null) {
                     LatLng firstLoc = new LatLng(listingList.get(0).lat, listingList.get(0).lng);
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLoc, 10));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Failed to load listings", error.toException());
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Markers will be added when data is loaded
    }
}
