//Bu sayfa, belirli bir kategoriye ait filmleri listeleyip gösteren bir ekran sağlar.

package com.example.project155.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project155.Adapters.FilmListAdapter;
import com.example.project155.Domain.ListFilm;
import com.example.project155.R;
import com.google.gson.Gson;

/**
 * Shows movies in a selected category.
 */
public class CategoryDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView; // Shows movie list
    private FilmListAdapter adapter; // Adapter for RecyclerView
    private ProgressBar progressBar; // Shows loading status
    private TextView categoryTitleTxt; // Displays category name
    private RequestQueue requestQueue; // Manages API requests

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        // Initialize views
        initView();

        // Get category ID and name from intent
        Integer categoryId = getIntent().getIntExtra("categoryId", 0);
        String categoryName = getIntent().getStringExtra("categoryName");

        // Log the category information
        Log.d("CategoryDetail", "Category ID: " + categoryId + ", Name: " + categoryName);

        // Set the category name in the title
        categoryTitleTxt.setText(categoryName + " Movies");

        // Fetch movies if category ID is valid
        if (categoryId != null && categoryId != 0) {
            fetchMoviesByGenre(categoryId);
        } else {
            Toast.makeText(this, "Invalid category ID", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initialize views like RecyclerView, ProgressBar, and TextView.
     */
    private void initView() {
        recyclerView = findViewById(R.id.categoryDetailRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        categoryTitleTxt = findViewById(R.id.categoryTitleTxt);

        // Set layout for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize RequestQueue for API requests
        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * Fetch movies from API based on genre ID.
     * @param genreId Genre ID for filtering movies
     */
    private void fetchMoviesByGenre(Integer genreId) {
        // Show loading
        progressBar.setVisibility(View.VISIBLE);

        // API URL to get movies
        String url = "https://api.themoviedb.org/3/discover/movie" +
                "?api_key=---YourApiKey---" +
                "&with_genres=" + genreId +
                "&page=1"; // Fetch first page only

        // Create API request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Hide loading
                        progressBar.setVisibility(View.GONE);
                        // Parse JSON response
                        Gson gson = new Gson();
                        ListFilm moviesList = gson.fromJson(response, ListFilm.class);

                        // If movies are found, display them
                        if (moviesList != null && moviesList.getResults() != null && !moviesList.getResults().isEmpty()) {
                            adapter = new FilmListAdapter(moviesList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            // Show message if no movies found
                            Toast.makeText(this, "No movies found for this category", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        // Show error message if JSON parsing fails
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading movies: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Hide loading and show network error
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Set timeout for the request
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,  // Timeout after 10 seconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Default retry count
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Default multiplier
        ));

        // Add request to the queue
        requestQueue.add(stringRequest);
    }
}
