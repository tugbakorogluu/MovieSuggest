//kullanıcının favori filmlerini listelemek için kullanılan bir adapter sınıfıdır.
//Her bir favori film, RecyclerView üzerinde gösterilir ve şunları içerir:
//Filmin adı ve puanı, Film posteri (Glide ile yüklenir), Favorilerden kaldırma butonu.

package com.example.project155.Adapters;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project155.Activities.DetailActivity;
import com.example.project155.Domain.FavoriteMovie;
import com.example.project155.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.ViewHolder> {
    private final List<FavoriteMovie> favoriteMovies;
    private final Context context;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public FavoriteMoviesAdapter(Context context, List<FavoriteMovie> favoriteMovies) {
        this.context = context;
        this.favoriteMovies = favoriteMovies;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_favorite_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteMovie movie = favoriteMovies.get(position);

        holder.titleTxt.setText(movie.getTitle());
        holder.rateTxt.setText(String.format("%.1f", movie.getRating()));

        // Load movie poster using Glide
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(holder.pic);

        // Handle click on movie item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id", Integer.parseInt(String.valueOf(movie.getMovieId())));
            context.startActivity(intent);
        });

        // Handle remove from favorites
        holder.removeBtn.setOnClickListener(v -> removeFromFavorites(movie, position));
    }

    private void removeFromFavorites(FavoriteMovie movie, int position) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("favorites")
                .document(userId + "_" + movie.getMovieId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    favoriteMovies.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, favoriteMovies.size());
                });
    }

    @Override
    public int getItemCount() {
        return favoriteMovies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, rateTxt;
        ImageView pic, removeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            rateTxt = itemView.findViewById(R.id.rateTxt);
            pic = itemView.findViewById(R.id.pic);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }
}