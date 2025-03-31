//Bu sayfa, bir filmdeki oyuncuları listeleyen bir RecyclerView adaptörüdür ve her oyuncunun ismini görüntüler.

package com.example.project155.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project155.Domain.Cast;
import com.example.project155.R;

import java.util.List;

public class ActorsListAdapter extends RecyclerView.Adapter<ActorsListAdapter.ViewHolder> {
    private List<Cast> castList;
    private Context context;

    public ActorsListAdapter(List<Cast> castList) {
        this.castList = castList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cast cast = castList.get(position);
        holder.actorName.setText(cast.getName());
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView actorName;

        public ViewHolder(View itemView) {
            super(itemView);
            actorName = itemView.findViewById(R.id.actorName);
        }
    }
}