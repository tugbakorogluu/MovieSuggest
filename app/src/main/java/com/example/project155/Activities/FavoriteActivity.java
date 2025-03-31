//Bu sayfa, kullanıcıların favori filmlerini Firebase veritabanından alarak bir
// liste halinde gösteren bir ekran sağlar.

package com.example.project155.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project155.Adapters.FavoriteMoviesAdapter;
import com.example.project155.Domain.FavoriteMovie;
import com.example.project155.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteMoviesAdapter adapter;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView emptyText;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        initViews();
        loadFavoriteMovies();
    }
    private void initViews() {
        recyclerView = findViewById(R.id.favoriteRecyclerView);
        progressBar = findViewById(R.id.favoriteProgressBar);
        emptyText = findViewById(R.id.emptyFavoriteText);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("favorites");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    private void loadFavoriteMovies() {
        if (auth.getCurrentUser() == null) {
            emptyText.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String userId = auth.getCurrentUser().getUid();

        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FavoriteMovie> favoriteMovies = new ArrayList<>();

                for (DataSnapshot movieSnapshot : snapshot.getChildren()) {
                    FavoriteMovie movie = new FavoriteMovie();
                    movie.setMovieId(movieSnapshot.child("movieId").getValue(Integer.class));
                    movie.setTitle(movieSnapshot.child("title").getValue(String.class));
                    movie.setPosterPath(movieSnapshot.child("posterPath").getValue(String.class));
                    movie.setVoteAverage(movieSnapshot.child("voteAverage").getValue(Double.class));
                    movie.setUserId(userId);
                    favoriteMovies.add(movie);
                }

                adapter = new FavoriteMoviesAdapter(FavoriteActivity.this, favoriteMovies);
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
                emptyText.setVisibility(favoriteMovies.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                emptyText.setVisibility(View.VISIBLE);
                Toast.makeText(FavoriteActivity.this,
                        "Veriler yüklenemedi: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}