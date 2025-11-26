package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Adapter class to bind Agent data to RecyclerView items
public class AgentAdapter extends RecyclerView.Adapter<AgentAdapter.AgentViewHolder> {

    Context context;
    ArrayList<Agent> list;

    public AgentAdapter(Context context, ArrayList<Agent> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AgentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the agent_item layout for each row
        View v = LayoutInflater.from(context).inflate(R.layout.agent_item, parent, false);
        return new AgentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentViewHolder holder, int position) {
        Agent agent = list.get(position);

        if (agent == null) return;

        // Bind name
        holder.txtName.setText(agent.name != null ? agent.name : "Unknown");

        // Bind rating
        holder.txtRating.setText("⭐ " + (agent.rating != null ? agent.rating : "N/A"));

        // Bind image using Picasso library
        if (agent.imageUrl != null && !agent.imageUrl.isEmpty()) {
            Picasso.get()
                    .load(agent.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgAgent);
        } else {
            holder.imgAgent.setImageResource(R.drawable.ic_launcher_background);
        }

        // Handle item click events
        holder.itemView.setOnClickListener(v -> {
            // Navigate to AgentDetailsActivity with agent data
            Intent intent = new Intent(context, AgentDetailsActivity.class);
            intent.putExtra("agentId", agent.id);
            intent.putExtra("agentName", agent.name);
            intent.putExtra("agentRating", agent.rating);
            intent.putExtra("agentBio", agent.bio);
            intent.putExtra("agentImage", agent.imageUrl);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ViewHolder class to cache view references
    public static class AgentViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAgent;
        TextView txtName, txtRating;

        public AgentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAgent = itemView.findViewById(R.id.imgAgent);
            txtName = itemView.findViewById(R.id.txtName);
            txtRating = itemView.findViewById(R.id.txtRating);
        }
    }
}
