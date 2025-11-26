package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// This fragment displays the main dashboard of agents, categorized into different lists
public class LocalExpertsFragment extends Fragment {

    // RecyclerViews for different categories
    RecyclerView rvLocal, rvActive, rvAll, rvRental;
    // Adapters for binding data to the RecyclerViews
    AgentAdapter localAdapter, activeAdapter, allAdapter, rentalAdapter;
    // Lists to hold Agent objects for each category
    ArrayList<Agent> localList, activeList, allList, rentalList;

    public LocalExpertsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("APP_FLOW", "LocalExpertsFragment onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local_experts, container, false);

        // Initialize RecyclerViews
        rvLocal = view.findViewById(R.id.rvLocalExperts);
        rvActive = view.findViewById(R.id.rvActiveAgents);
        rvAll = view.findViewById(R.id.rvAllAgents);
        rvRental = view.findViewById(R.id.rvRentalAgents);

        // Set layout managers for horizontal scrolling
        rvLocal.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvActive.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvAll.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRental.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        // Initialize data lists
        localList = new ArrayList<>();
        activeList = new ArrayList<>();
        allList = new ArrayList<>();
        rentalList = new ArrayList<>();

        // Initialize adapters and attach them to RecyclerViews
        localAdapter = new AgentAdapter(getContext(), localList);
        activeAdapter = new AgentAdapter(getContext(), activeList);
        allAdapter = new AgentAdapter(getContext(), allList);
        rentalAdapter = new AgentAdapter(getContext(), rentalList);

        rvLocal.setAdapter(localAdapter);
        rvActive.setAdapter(activeAdapter);
        rvAll.setAdapter(allAdapter);
        rvRental.setAdapter(rentalAdapter);

        // Fetch data from Firebase
        loadAgents();

        return view;
    }

    // Method to load agents from Firebase Realtime Database
    private void loadAgents() {
        Log.d("FIREBASE_READ", "Attempting to load agents from Firebase...");
        
        // Get reference to the "agents" node in Firebase
        // Using standard instance - ensures it uses google-services.json configuration
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("agents");

        // Listen for data changes
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FIREBASE_READ", "onDataChange called. Children count: " + snapshot.getChildrenCount());
                
                // Temporary list to hold ALL agents from Firebase first
                ArrayList<Agent> rawList = new ArrayList<>();
                
                // Clear existing lists to avoid duplication
                activeList.clear();
                localList.clear(); 
                allList.clear();   
                rentalList.clear(); 

                // Iterate through all children in the snapshot
                for (DataSnapshot snap : snapshot.getChildren()) {
                    try {
                        Agent agent = snap.getValue(Agent.class);
                        if (agent != null) {
                            rawList.add(agent);
                            
                            // Logic 1: Most Active Agents (First 4 active agents found)
                            if (Boolean.TRUE.equals(agent.active)) {
                                if (activeList.size() < 4) {
                                    activeList.add(agent);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("AGENT_PARSE", "Error parsing agent: " + e.getMessage());
                    }
                }

                // Logic 2: Local Experts (Last 4 agents in the database)
                if (rawList.size() > 4) {
                    List<Agent> lastFour = rawList.subList(rawList.size() - 4, rawList.size());
                    localList.addAll(lastFour);
                } else {
                    localList.addAll(rawList);
                }

                // Logic 3: All Agents (Specifically agents from index 3 to 15, i.e., 4th to 16th)
                int start = 3; 
                int end = 16; 

                if (rawList.size() > start) {
                    int actualEnd = Math.min(rawList.size(), end);
                    List<Agent> middleSection = rawList.subList(start, actualEnd);
                    allList.addAll(middleSection);
                }

                // Logic 4: Top Agents in Rental (Every even-numbered agent: 2nd, 4th, 6th, etc.)
                for (int i = 0; i < rawList.size(); i++) {
                    // i + 1 is the 1-based position (1st, 2nd...)
                    // If position is even, add to list
                    if ((i + 1) % 2 == 0) {
                        rentalList.add(rawList.get(i));
                    }
                }

                Log.d("LIST_UPDATE", "Local: " + localList.size() + ", Active: " + activeList.size() + ", All: " + allList.size() + ", Rental: " + rentalList.size());
                
                // Notify adapters that data has changed so UI updates
                localAdapter.notifyDataSetChanged();
                activeAdapter.notifyDataSetChanged();
                allAdapter.notifyDataSetChanged();
                rentalAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE_READ", "Database error: " + error.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
