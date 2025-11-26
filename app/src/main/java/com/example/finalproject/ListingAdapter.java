package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private Context context;
    private ArrayList<Listing> listingList;
    private Set<String> favouriteIds = new HashSet<>();
    private String userId = "testUser01";
    private boolean isFavouritesScreen;

    public ListingAdapter(Context context, ArrayList<Listing> listingList, boolean isFavouritesScreen) {
        this.context = context;
        this.listingList = listingList;
        this.isFavouritesScreen = isFavouritesScreen;
        loadUserFavourites();
    }
    
    public ListingAdapter(Context context, ArrayList<Listing> listingList) {
        this(context, listingList, false);
    }

    private void loadUserFavourites() {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favourites");
        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favouriteIds.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (Boolean.TRUE.equals(snap.getValue(Boolean.class))) {
                        favouriteIds.add(snap.getKey());
                    }
                }
                notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listing_item, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Listing listing = listingList.get(position);

        holder.txtTitle.setText(listing.title);
        holder.txtPrice.setText("$" + String.format("%,.0f", listing.price));
        holder.txtDescription.setText(listing.bedrooms + " Bed | " + listing.bathrooms + " Bath | " + listing.address);

        if (listing.photos != null && !listing.photos.isEmpty()) {
            Picasso.get()
                   .load(listing.photos.get(0))
                   .placeholder(R.drawable.ic_launcher_background)
                   .into(holder.imgThumbnail);
        }

        // Check favourite status from our local set
        boolean isFav = favouriteIds.contains(listing.id);
        if (isFav) {
            holder.imgHeart.setImageResource(android.R.drawable.star_big_on);
        } else {
            holder.imgHeart.setImageResource(android.R.drawable.star_big_off);
        }

        // Toggle favourite
        holder.imgHeart.setOnClickListener(v -> {
            boolean newState = !favouriteIds.contains(listing.id);
            
            DatabaseReference userFavRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favourites").child(listing.id);
            
            if (newState) {
                userFavRef.setValue(true);
                favouriteIds.add(listing.id);
                holder.imgHeart.setImageResource(android.R.drawable.star_big_on);
            } else {
                userFavRef.removeValue();
                favouriteIds.remove(listing.id);
                holder.imgHeart.setImageResource(android.R.drawable.star_big_off);
                
                // If we are in the Favourites screen, remove the item locally for instant feedback
                if (isFavouritesScreen) {
                    listingList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listingList.size());
                }
            }
        });

        // Handle click to open details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ListingDetailsActivity.class);
            intent.putExtra("listingId", listing.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listingList.size();
    }

    public static class ListingViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail, imgHeart;
        TextView txtTitle, txtPrice, txtDescription;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }
    }
}
