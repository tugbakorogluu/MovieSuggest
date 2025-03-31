//Bu sayfa, popüler filmleri, yaklaşan filmleri ve film kategorilerini gösteren
// bir ana sayfa olup, ayrıca kullanıcıların film araması yapmalarını ve
// profillerine erişmelerini sağlar.

package com.example.project155.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project155.Adapters.CategoryListAdapter;
import com.example.project155.Adapters.FilmListAdapter;
import com.example.project155.Adapters.SliderAdapters;
import com.example.project155.Domain.GenresItem;
import com.example.project155.Domain.ListFilm;
import com.example.project155.Domain.SliderItems;
import com.example.project155.Domain.TMDbMovie;
import com.example.project155.Domain.FilmItem;
import com.example.project155.Utils.PopularMoviesHelper;

import com.example.project155.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterBestMovies, adapterUpComming, adapterCategory;
    private RecyclerView recyclerViewBestMovies, recyclerviewUpcomming, recyclerviewCategory;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest, mStringRequest2, mStringRequest3;
    private ProgressBar loading1, loading2, loading3;
    private ViewPager2 viewPager2;
    private Handler slideHandler = new Handler();
    private EditText searchEditText;
    private RecyclerView searchRecyclerView;
    private FilmListAdapter searchAdapter;
    private List<TMDbMovie> searchResults;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean isDestroyed = false;
    private List<GenresItem> catList;  // GenresItem, kategori verilerini tutan model sınıfı


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Authentication başlatma
        mAuth = FirebaseAuth.getInstance();
        setupAuthListener();

        initView();
        setupVolleyQueue();
        banners();
        sendRequestBestMovies();
        sendRequestUpComming();
        sendRequestCategory();
        catList = new ArrayList<>();


    }
    private void setupAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null && !isDestroyed) {
                    // Kullanıcı oturumu kapanmışsa Login sayfasına yönlendir
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }
    private void setupVolleyQueue() {
        mRequestQueue = Volley.newRequestQueue(this);
        // Volley için default timeout ve retry policy ayarları
        int socketTimeout = 30000; // 30 saniye
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        mRequestQueue.start();
    }
    private void sendRequestUpComming() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading3.setVisibility(View.VISIBLE);

        // TMDb API'den popüler filmleri çekmek için URL
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=---YourApıKey---&page=1";

        mStringRequest3 = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Gson gson = new Gson();
                loading3.setVisibility(View.GONE);

                // Tüm popüler filmleri al
                ListFilm allMovies = gson.fromJson(response, ListFilm.class);

                // Filmleri filtrele ve rastgele seç
                ListFilm filteredMovies = PopularMoviesHelper.filterAndRandomizeMovies(
                        allMovies,    // orijinal film listesi
                        20,          // istenen film sayısı
                        300.0        // minimum popularity değeri
                );

                // RecyclerView adaptörünü güncelle
                adapterUpComming = new FilmListAdapter(filteredMovies);
                recyclerviewUpcomming.setAdapter(adapterUpComming);

            } catch (Exception e) {
                loading3.setVisibility(View.GONE);
                Log.e("MainActivity", "Error processing movies: " + e.getMessage());
                Toast.makeText(MainActivity.this,
                        "Error loading popular movies", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            loading3.setVisibility(View.GONE);
            Log.i("TMDbError", "onErrorResponse: " + error.toString());
            Toast.makeText(MainActivity.this,
                    "Error loading popular movies", Toast.LENGTH_SHORT).show();
        });

        // Request timeout süresini artır
        mStringRequest3.setRetryPolicy(new DefaultRetryPolicy(
                30000,  // 30 saniye timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        mRequestQueue.add(mStringRequest3);
    }
    private void sendRequestCategory() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading2.setVisibility(View.VISIBLE);

        // TMDb genres endpoint'ini kullanıyoruz
        String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=---YourApıKey---";

        mStringRequest2 = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
                JsonArray genresArray = jsonObject.getAsJsonArray("genres");
                ArrayList<GenresItem> catList = gson.fromJson(genresArray, new TypeToken<ArrayList<GenresItem>>() {
                }.getType());

                loading2.setVisibility(View.GONE);
                adapterCategory = new CategoryListAdapter(catList, new CategoryListAdapter.OnCategoryClickListener() {
                    @Override
                    public void onCategoryClick(GenresItem category) {
                        // Burada kategori tıklandığında yapılacak işlemleri yazın
                        Intent intent = new Intent(MainActivity.this, CategoryMoviesActivity.class);
                        intent.putExtra("category_id", category.getId());
                        intent.putExtra("category_name", category.getName());
                        startActivity(intent);
                    }
                });
                recyclerviewCategory.setAdapter(adapterCategory);
            } catch (Exception e) {
                loading2.setVisibility(View.GONE);
                Log.e("MainActivity", "Category parsing error: " + e.toString());
            }
        }, error -> {
            loading2.setVisibility(View.GONE);
            Log.e("MainActivity", "Category request error: " + error.toString());
        });

        mRequestQueue.add(mStringRequest2);
    }
    private void sendRequestBestMovies() {
        mRequestQueue = Volley.newRequestQueue(this);
        loading1.setVisibility(View.VISIBLE);

        // IMDB puanı filtresini 7.5'a düşürelim ve daha fazla film alalım
        String url = "https://api.themoviedb.org/3/discover/movie?"
                + "api_key=---YourApıKey---"
                + "&language=en-US"
                + "&sort_by=vote_average.desc"
                + "&vote_count.gte=1000"        // En az 1000 oy almış filmler
                + "&vote_average.gte=7.5"       // IMDB puanını 7.5 ve üzeri olarak değiştirdik
                + "&page=1"
                + "&include_adult=false";

        mStringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                // API yanıtını logla
                Log.d("BestMovies", "API Response: " + response);

                Gson gson = new Gson();
                ListFilm allMovies = gson.fromJson(response, ListFilm.class);

                if (allMovies != null && allMovies.getResults() != null) {
                    List<FilmItem> movieList = new ArrayList<>(allMovies.getResults());

                    // Gelen film sayısını logla
                    Log.d("BestMovies", "Total movies received: " + movieList.size());

                    if (movieList.isEmpty()) {
                        Log.e("BestMovies", "No movies found in the response");
                        return;
                    }

                    Collections.shuffle(movieList);

                    int endIndex = Math.min(15, movieList.size());
                    List<FilmItem> randomizedMovies = movieList.subList(0, endIndex);

                    // Seçilen film sayısını logla
                    Log.d("BestMovies", "Selected movies count: " + randomizedMovies.size());

                    ListFilm randomizedList = new ListFilm();
                    randomizedList.setResults(randomizedMovies);

                    adapterBestMovies = new FilmListAdapter(randomizedList);
                    recyclerViewBestMovies.setAdapter(adapterBestMovies);

                    // Adapter'a veri set edildiğini logla
                    Log.d("BestMovies", "Adapter set with movies");
                } else {
                    Log.e("BestMovies", "allMovies or results is null");
                }

                loading1.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e("BestMovies", "Error parsing response: " + e.getMessage());
                e.printStackTrace();
                loading1.setVisibility(View.GONE);
            }
        }, error -> {
            loading1.setVisibility(View.GONE);
            Log.e("TMDbError", "Error: " + error.toString());
            // Volley hata detaylarını logla
            if (error.networkResponse != null) {
                Log.e("TMDbError", "Status Code: " + error.networkResponse.statusCode);
                Log.e("TMDbError", "Data: " + new String(error.networkResponse.data));
            }
        });

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(mStringRequest);
    }
    private void banners() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide));
        sliderItems.add(new SliderItems(R.drawable.wide3));
        sliderItems.add(new SliderItems(R.drawable.wide1));

        viewPager2.setAdapter(new SliderAdapters(sliderItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(sliderRunnable);
            }
        });
    }
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }
    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable, 2000);
    }
    private void initView() {
        viewPager2 = findViewById(R.id.viewpagerSlider);
        recyclerViewBestMovies = findViewById(R.id.view1);
        recyclerViewBestMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerviewUpcomming = findViewById(R.id.view3);
        recyclerviewUpcomming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerviewCategory = findViewById(R.id.view2);
        recyclerviewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loading1 = findViewById(R.id.progressBar1);
        loading2 = findViewById(R.id.progressBar2);
        loading3 = findViewById(R.id.progressBar3);

        searchEditText = findViewById(R.id.editTextText2);
        searchResults = new ArrayList<>();

        // SearchRecyclerView'ı layout'a eklememiz gerekiyor
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new FilmListAdapter(new ListFilm()); // Boş liste ile başlat
        searchRecyclerView.setAdapter(searchAdapter);

        setupSearchBar();


    }
    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchMovies(s.toString());
                    searchRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    searchRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void searchMovies(String query) {
        String url = "https://api.themoviedb.org/3/search/movie?api_key=---YourApıKey---&query=" + query;

        StringRequest searchRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ListFilm searchResults = gson.fromJson(response, ListFilm.class);
                    searchAdapter = new FilmListAdapter(searchResults);
                    searchRecyclerView.setAdapter(searchAdapter);
                },
                error -> {
                    Toast.makeText(MainActivity.this, "Search error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });

        mRequestQueue.add(searchRequest);
    }
    public void openProfile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("USER_NAME", "John Doe");
        intent.putExtra("USER_EMAIL", "john.doe@example.com");
        startActivity(intent);
    }
    public void openSuggest(View view) {
        Intent intent = new Intent(this, SuggestActivity.class);
        startActivity(intent);
    }
    public void openFavorite(View view) {
        Intent intent = new Intent(this, FavoriteActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth != null && mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null && mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(request -> true);
        }
        if (slideHandler != null) {
            slideHandler.removeCallbacksAndMessages(null);
        }
    }
}




