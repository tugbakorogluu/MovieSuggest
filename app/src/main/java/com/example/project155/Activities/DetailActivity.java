//Bu sayfa, bir film detayını gösteren ve kullanıcının film bilgilerini,
// oyuncu kadrosunu görmesini sağlayan, aynı zamanda favorilere ekleme/çıkarma
// işlevi sunan bir Android uygulaması sayfasıdır.

package com.example.project155.Activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import com.example.project155.Domain.FavoriteMovie;
import com.example.project155.Domain.Cast;
import com.example.project155.Domain.TMDbMovie;
import com.example.project155.Domain.CastResponse;
import com.example.project155.Adapters.ActorsListAdapter;
import com.example.project155.R;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;


public class DetailActivity extends AppCompatActivity {

    private RequestQueue mRequestQueue;
    private ProgressBar progressBar;
    private TextView titleTxt, movieRateTxt, movieTimeTxt, movieSummaryInfo, movieActorsInfo;
    private int idFilm;
    private ImageView pic2, backImg, imageView5;
    private boolean isFavorite = false; // favori durumunu takip etmek için
    private RecyclerView.Adapter adapterActorList, adapterCategory;
    private RecyclerView recyclerViewActors, recyclerViewCategory;
    private NestedScrollView scrollView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String currentUserId;
    private String favoriteDocumentId;
    private TMDbMovie currentMovie;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        databaseReference = FirebaseDatabase.getInstance().getReference("favorites");


        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        }

        idFilm = getIntent().getIntExtra("id", 0);
        initView();
        initFavoriteButton();
        if (isNetworkAvailable()) {
            sendRequest();
        } else {
            Toast.makeText(this, "Internet connection not found", Toast.LENGTH_LONG).show();
        }
    }
    private void checkIfFavorite() {
        if (currentUserId == null || idFilm == 0) {
            return;
        }

        if (imageView5 != null) {
            imageView5.setEnabled(false);
        }

        databaseReference.child(currentUserId)
                .child(String.valueOf(idFilm))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (imageView5 != null) {
                            imageView5.setEnabled(true);
                        }

                        if (snapshot.exists()) {
                            isFavorite = true;
                            if (imageView5 != null) {
                                imageView5.setColorFilter(Color.RED);
                            }
                        } else {
                            isFavorite = false;
                            if (imageView5 != null) {
                                imageView5.setColorFilter(Color.WHITE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (imageView5 != null) {
                            imageView5.setEnabled(true);
                        }
                        Toast.makeText(DetailActivity.this,
                                "Favorite status could not be checked: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    private void updateUI(TMDbMovie movie) {
        if (movie != null) {
            currentMovie = movie;
            titleTxt.setText(movie.getTitle() != null ? movie.getTitle() : "");
            movieSummaryInfo.setText(movie.getOverview() != null ? movie.getOverview() : "");
            movieRateTxt.setText(movie.getVoteAverage() != null ? String.valueOf(movie.getVoteAverage()) : "0.0");
            movieTimeTxt.setText(movie.getRuntime() != null ? movie.getRuntime() + " min" : "");

            if (movie.getPosterPath() != null) {
                String imageUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
                Glide.with(this)
                        .load(imageUrl)
                        .error(R.drawable.wide)
                        .into(pic2);
            }
            checkIfFavorite();
        }
    }
    private void sendRequest(){
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://api.themoviedb.org/3/movie/" + idFilm + "?api_key=---YourApıKey---";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        Gson gson = new Gson();
                        TMDbMovie movie = gson.fromJson(response, TMDbMovie.class);
                        updateUI(movie);
                        // Film detayları alındıktan sonra oyuncu bilgilerini al
                        getMovieCast();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(DetailActivity.this, "Data processing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    handleVolleyError(error);
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }
    private void handleVolleyError(VolleyError error) {
        String message = "An error occurred";
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            message = "Internet connection error";
        } else if (error instanceof AuthFailureError) {
            message = "Authentication error";
        } else if (error instanceof ServerError) {
            message = "Server error";
        } else if (error instanceof NetworkError) {
            message = "network error";
        } else if (error instanceof ParseError) {
            message = "Data processing error";
        }
        Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private void getMovieDetails(int movieId) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId +
                "?api_key=---YourApıKey---";

        StringRequest detailRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Gson gson = new Gson();
                        TMDbMovie movieDetail = gson.fromJson(response, TMDbMovie.class);

                        // Film süresini güncelle
                        if (movieDetail.getRuntime() != null && movieDetail.getRuntime() > 0) {
                            movieTimeTxt.setText(movieDetail.getRuntime() + " min");
                        } else {
                            movieTimeTxt.setText("Duration information not available");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        movieTimeTxt.setText("Duration information not available");
                    }
                },
                error -> {
                    error.printStackTrace();
                    movieTimeTxt.setText("Duration information not available");
                });

        mRequestQueue.add(detailRequest);
    }
    private void initView() {
        titleTxt = findViewById(R.id.movieNameTxt);
        progressBar = findViewById(R.id.progressBarDetail);
        scrollView = findViewById(R.id.scrollView2);
        pic2 = findViewById(R.id.picDetail);
        movieRateTxt = findViewById(R.id.movieStar);
        movieTimeTxt = findViewById(R.id.movieTime);
        movieSummaryInfo = findViewById(R.id.movieSummery);
        movieActorsInfo = findViewById(R.id.movieActorInfo);
        backImg = findViewById(R.id.backImg);
        recyclerViewCategory = findViewById(R.id.genreView);
        recyclerViewActors = findViewById(R.id.imagesRecycler);

        mRequestQueue = Volley.newRequestQueue(this);

        recyclerViewActors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        backImg.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        int movieId = intent.getIntExtra("id", -1);
        if (movieId != -1) {
            getMovieDetails(movieId);
        }
    }
    private void initFavoriteButton() {
        imageView5 = findViewById(R.id.imageView5);

        imageView5.setBackgroundResource(R.drawable.favorite_button_background);

        imageView5.setColorFilter(Color.WHITE);

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserId == null) {
                    Toast.makeText(DetailActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isNetworkAvailable()) {
                    Toast.makeText(DetailActivity.this, "Internet connection not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentMovie == null) {
                    Toast.makeText(DetailActivity.this, "Loading movie information, please try again", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isFavorite) {
                    removeFavorite();
                } else {
                    addFavorite();
                }

                // Animate the button
                imageView5.animate()
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .setDuration(100)
                        .withEndAction(() -> {
                            imageView5.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start();
                        })
                        .start();
            }
        });
    }
    private void addFavorite() {
        if (currentUserId == null || currentMovie == null) {
            Toast.makeText(this, "Movie information could not be loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> favoriteMovie = new HashMap<>();
        favoriteMovie.put("movieId", currentMovie.getId());
        favoriteMovie.put("title", currentMovie.getTitle());
        favoriteMovie.put("posterPath", currentMovie.getPosterPath());
        favoriteMovie.put("voteAverage", currentMovie.getVoteAverage());
        favoriteMovie.put("timestamp", ServerValue.TIMESTAMP);

        databaseReference.child(currentUserId)
                .child(String.valueOf(currentMovie.getId()))
                .setValue(favoriteMovie)
                .addOnSuccessListener(aVoid -> {
                    isFavorite = true;
                    imageView5.setColorFilter(Color.RED);
                    Toast.makeText(DetailActivity.this,
                            "Added to favorites",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DetailActivity.this,
                            "Could not add to favorites: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
    private void removeFavorite() {
        if (currentUserId == null || currentMovie == null) {
            return;
        }

        databaseReference.child(currentUserId)
                .child(String.valueOf(currentMovie.getId()))
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    isFavorite = false;
                    imageView5.setColorFilter(Color.WHITE);
                    Toast.makeText(DetailActivity.this,
                            "Removed from favorites",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DetailActivity.this,
                            "Could not remove from favorites: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
    private void getMovieCast() {
        String url = "https://api.themoviedb.org/3/movie/" + idFilm + "/credits?api_key=---YourApıKey---";

        StringRequest castRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Gson gson = new Gson();
                        CastResponse castResponse = gson.fromJson(response, CastResponse.class);
                        if (castResponse != null && castResponse.getCast() != null) {
                            setupActorsRecyclerView(castResponse.getCast());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(DetailActivity.this, "Player information could not be loaded", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(DetailActivity.this, "Player information could not be retrieved", Toast.LENGTH_SHORT).show();
                });

        mRequestQueue.add(castRequest);
    }
    private void setupActorsRecyclerView(List<Cast> castList) {
        if (castList != null && !castList.isEmpty()) {
            ActorsListAdapter adapter = new ActorsListAdapter(castList);
            recyclerViewActors.setAdapter(adapter);
        }
    }
}

