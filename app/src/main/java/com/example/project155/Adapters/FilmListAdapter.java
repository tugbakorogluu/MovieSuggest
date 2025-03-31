//Bu sayfa, film listesini görüntüleyen bir RecyclerView adaptörü sağlar ve
// her bir filme tıklanıldığında, ilgili filmin detaylarını gösteren bir sayfaya yönlendirir.

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
import com.example.project155.Domain.ListFilm;
import com.example.project155.Domain.FilmItem;
import com.example.project155.Domain.TMDbMovie;
import com.example.project155.R;
import com.example.project155.Activities.DetailActivity;

public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.ViewHolder> {
    private ListFilm items;
    private Context context;
    public FilmListAdapter(ListFilm items) {
        this.items = items;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // Görünüm (View) oluşturma
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_film, parent, false);
        return new ViewHolder(inflate);
    }
    @NonNull
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilmItem filmItem = items.getResults().get(position);

        // FilmItem'dan TMDbMovie'ye dönüştürme
        TMDbMovie currentMovie = new TMDbMovie();
        currentMovie.setId(filmItem.getId());
        currentMovie.setTitle(filmItem.getTitle());
        currentMovie.setPosterPath(filmItem.getPoster());
        currentMovie.setVoteAverage(filmItem.getImdbRating() != null ? Double.valueOf(filmItem.getImdbRating()) : 0);

        holder.titleTxt.setText(currentMovie.getTitle());
        holder.scoreTxt.setText("" + currentMovie.getVoteAverage());

        String imagePath = "https://image.tmdb.org/t/p/w500" + currentMovie.getPosterPath();
        Glide.with(context)
                .load(imagePath)
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id", currentMovie.getId());
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return items.getResults() != null ? items.getResults().size() : 0;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, scoreTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            scoreTxt = itemView.findViewById(R.id.scoreTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}