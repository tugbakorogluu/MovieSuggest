//bir kategoriye ait filmleri gösteren bir etkinliktir ve seçilen kategoriye göre filmleri
// TMDb API'sinden çeker ve RecyclerView içinde görüntüler.

package com.example.project155.Activities;

import com.example.project155.Domain.ListFilm;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.project155.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project155.Adapters.FilmListAdapter;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;

/**
 * Shows movies for a selected category.
 */
public class CategoryMoviesActivity extends AppCompatActivity {
    private RecyclerView recyclerView; // Displays the list of movies
    private FilmListAdapter adapter;  // Adapter for RecyclerView
    private ProgressBar progressBar;  // Shows loading status
    private RequestQueue requestQueue; // Manages API requests

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_movies);

        // Initialize views
        recyclerView = findViewById(R.id.categoryRecyclerView);
        progressBar = findViewById(R.id.categoryProgressBar);

        // Set layout for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get category details from intent
        int categoryId = getIntent().getIntExtra("category_id", -1);
        String categoryName = getIntent().getStringExtra("category_name");

        // Set the title and back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load movies for the selected category
        loadCategoryMovies(categoryId);
    }

    /**
     * Fetch movies from TMDb API based on category ID.
     */
    private void loadCategoryMovies(int categoryId) {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // API URL for fetching movies by category
        String url = "https://api.themoviedb.org/3/discover/movie" +
                "?api_key=---YourApıKey---" +
                "&with_genres=" + categoryId +
                "&sort_by=popularity.desc";

        requestQueue = Volley.newRequestQueue(this);

        // Create an API request
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        // Parse JSON response
                        Gson gson = new Gson();
                        ListFilm movies = gson.fromJson(response, ListFilm.class);

                        // Set the adapter with movie data
                        adapter = new FilmListAdapter(movies);
                        recyclerView.setAdapter(adapter);
                    } catch (Exception e) {
                        // Show error if data parsing fails
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Hide loading indicator and show error
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading movies", Toast.LENGTH_SHORT).show();
                });

        // Add the request to the queue
        requestQueue.add(request);
    }

    /**
     * Handle back button in action bar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Cancel all API requests when activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(req -> true);
        }
    }
}
