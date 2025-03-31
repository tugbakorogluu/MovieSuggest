//kullanıcının belirlediği kriterlere (kategori, yıl, dil, oyuncu, süre, IMDB puanı gibi) göre film önerileri almasını sağlar
package com.example.project155.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.project155.Domain.Cast;
import com.example.project155.Domain.GenresItem;
import com.example.project155.Domain.Language;
import com.example.project155.Domain.TMDbMovie;
import com.example.project155.Domain.TMDbMovieResponse;
import com.example.project155.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SuggestActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private ProgressBar progressBar;
    private TextView titleTxt, movieRateTxt, movieTimeTxt, movieSummaryInfo, suggestTitleTxt;
    private ImageView movieImage;
    private Spinner categorySpinner, durationSpinner, ratingSpinner, yearSpinner, languageSpinner;
    private AutoCompleteTextView actorSearchView;
    private Switch adultSwitch;
    private Button suggestButton;
    private List<GenresItem> categories;
    private List<Language> languages;
    private List<String> years;
    private Integer[] durations = {60, 90, 120, 150, 180};
    private Double[] ratings = {5.0, 6.0, 7.0, 8.0, 9.0};
    private Integer selectedActorId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);

        initView();
        setupSpinners();
        loadCategories();
        setupLanguages();
        setupYears();
        setupActorSearch();
    }
    private void initView() {
        // Progress Bar
        progressBar = findViewById(R.id.progressBar);

        // TextViews
        suggestTitleTxt = findViewById(R.id.suggestTextView);
        titleTxt = findViewById(R.id.movieTitle);
        movieRateTxt = findViewById(R.id.movieRating);
        movieTimeTxt = findViewById(R.id.movieDuration);
        movieSummaryInfo = findViewById(R.id.movieDescription);

        // ImageView
        movieImage = findViewById(R.id.movieImage);

        // Spinners, AutoCompleteTextView ve Switch
        categorySpinner = findViewById(R.id.categorySpinner);
        durationSpinner = findViewById(R.id.durationSpinner);
        ratingSpinner = findViewById(R.id.ratingSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        languageSpinner = findViewById(R.id.languageSpinner);
        actorSearchView = findViewById(R.id.actorSearchView);
        adultSwitch = findViewById(R.id.adultSwitch);

        // Button
        suggestButton = findViewById(R.id.suggestButton);
        suggestButton.setOnClickListener(v -> getSuggestedMovie());

        // RequestQueue
        mRequestQueue = Volley.newRequestQueue(this);
    }
    private void setupSpinners() {
        // Duration Spinner setup
        List<String> durationList = new ArrayList<>();
        durationList.add("Choose duration"); // Varsayılan seçenek
        for (Integer duration : durations) {
            durationList.add(duration + " dakika");
        }
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, durationList);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(durationAdapter);

        // Rating Spinner setup
        // Rating Spinner setup devamı
        List<String> ratingList = new ArrayList<>();
        ratingList.add("Select IMDB rating"); // Varsayılan seçenek
        for (Double rating : ratings) {
            ratingList.add(rating + "+");
        }
        ArrayAdapter<String> ratingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, ratingList);
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(ratingAdapter);
    }
    private void setupYears() {
        // Yıl listesini oluştur (1900'den günümüze)
        years = new ArrayList<>();
        years.add("Select year"); // Varsayılan seçenek
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year >= 1900; year--) {
            years.add(String.valueOf(year));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
    }
    private void setupLanguages() {
        // Dil listesini oluştur
        languages = new ArrayList<>();
        languages.add(new Language("", "Select language")); // Varsayılan seçenek
        languages.add(new Language("en", "İngilizce"));
        languages.add(new Language("tr", "Türkçe"));
        languages.add(new Language("es", "İspanyolca"));
        languages.add(new Language("fr", "Fransızca"));
        languages.add(new Language("de", "Almanca"));
        languages.add(new Language("it", "İtalyanca"));
        languages.add(new Language("ja", "Japonca"));
        languages.add(new Language("ko", "Korece"));
        languages.add(new Language("zh", "Çince"));
        // Daha fazla dil eklenebilir

        ArrayAdapter<Language> languageAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
    }
    private void setupActorSearch() {
        actorSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    searchActors(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void searchActors(String query) {
        String url = String.format(Locale.US,
                "https://api.themoviedb.org/3/search/person?api_key=---YourApıKey---&query=%s",
                query);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
                        JsonArray resultsArray = jsonObject.getAsJsonArray("results");

                        List<Cast> actors = gson.fromJson(resultsArray,
                                new TypeToken<ArrayList<Cast>>(){}.getType());

                        // AutoCompleteTextView için oyuncu isimlerini hazırla
                        String[] actorNames = actors.stream()
                                .map(actor -> actor.getName())
                                .toArray(String[]::new);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_dropdown_item_1line, actorNames);
                        actorSearchView.setAdapter(adapter);

                        // Seçilen oyuncunun ID'sini sakla
                        actorSearchView.setOnItemClickListener((parent, view, position, id) -> {
                            String selectedName = (String) parent.getItemAtPosition(position);
                            selectedActorId = actors.stream()
                                    .filter(actor -> actor.getName().equals(selectedName))
                                    .map(actor -> actor.getId())
                                    .findFirst()
                                    .orElse(null);
                        });

                    } catch (Exception e) {
                        Log.e("SuggestActivity", "Oyuncu arama hatası: " + e.getMessage());
                    }
                },
                error -> Log.e("SuggestActivity", "API hatası: " + error.toString()));

        mRequestQueue.add(request);
    }
    private void loadCategories() {
        String url = "https://api.themoviedb.org/3/genre/movie/list?api_key=---YourApıKey---";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
                        JsonArray genresArray = jsonObject.getAsJsonArray("genres");
                        categories = gson.fromJson(genresArray,
                                new TypeToken<ArrayList<GenresItem>>(){}.getType());

                        // Kategori listesine varsayılan seçenek ekle
                        List<String> categoryNames = new ArrayList<>();
                        categoryNames.add("Select category"); // Varsayılan seçenek
                        categoryNames.addAll(categories.stream()
                                .map(GenresItem::getName)
                                .toList());

                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item,
                                categoryNames);
                        categoryAdapter.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item);
                        categorySpinner.setAdapter(categoryAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("SuggestActivity", "Kategori yükleme hatası: " + error.toString()));

        mRequestQueue.add(request);
    }
    private void getSuggestedMovie() {
        try {
            StringBuilder urlBuilder = new StringBuilder(
                    "https://api.themoviedb.org/3/discover/movie?api_key=---YourApıKey---"
            );

            // Kategori filtresi
            if (categorySpinner.getSelectedItemPosition() > 0) {
                int selectedGenreId = categories.get(categorySpinner.getSelectedItemPosition() - 1).getId();
                urlBuilder.append("&with_genres=").append(selectedGenreId);
            }

            // Yıl filtresi
            if (yearSpinner.getSelectedItemPosition() > 0) {
                String selectedYear = years.get(yearSpinner.getSelectedItemPosition());
                urlBuilder.append("&primary_release_year=").append(selectedYear);
            }

            // Dil filtresi
            if (languageSpinner.getSelectedItemPosition() > 0) {
                String selectedLanguage = languages.get(languageSpinner.getSelectedItemPosition()).getCode();
                urlBuilder.append("&with_original_language=").append(selectedLanguage);
            }

            // Oyuncu filtresi
            if (selectedActorId != null) {
                urlBuilder.append("&with_cast=").append(selectedActorId);
            }

            // IMDB puanı filtresi
            if (ratingSpinner.getSelectedItemPosition() > 0) {
                double minRating = ratings[ratingSpinner.getSelectedItemPosition() - 1];
                urlBuilder.append("&vote_average.gte=").append(minRating);
            }

            // Süre filtresi
            if (durationSpinner.getSelectedItemPosition() > 0) {
                int maxDuration = durations[durationSpinner.getSelectedItemPosition() - 1];
                urlBuilder.append("&with_runtime.lte=").append(maxDuration);
            }

            // Yetişkin içerik filtresi
            urlBuilder.append("&include_adult=").append(adultSwitch.isChecked());

            progressBar.setVisibility(View.VISIBLE);

            StringRequest request = new StringRequest(Request.Method.GET, urlBuilder.toString(),
                    response -> {
                        try {
                            Gson gson = new Gson();
                            TMDbMovieResponse movieResponse = gson.fromJson(response, TMDbMovieResponse.class);

                            if (movieResponse.getResults().isEmpty()) {
                                Toast.makeText(this, "Bu filtrelerle film bulunamadı",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                return;
                            }

                            Random random = new Random();
                            TMDbMovie movie = movieResponse.getResults().get(
                                    random.nextInt(movieResponse.getResults().size()));

                            String detailUrl = String.format(Locale.US,
                                    "https://api.themoviedb.org/3/movie/%d?api_key=---YourApıKey---",
                                    movie.getId());

                            StringRequest detailRequest = new StringRequest(Request.Method.GET, detailUrl,
                                    detailResponse -> {
                                        try {
                                            TMDbMovie detailedMovie = gson.fromJson(detailResponse, TMDbMovie.class);
                                            movie.setRuntime(detailedMovie.getRuntime());
                                            displayMovie(movie);
                                            suggestTitleTxt.setVisibility(View.VISIBLE);
                                        } catch (Exception e) {
                                            Log.e("SuggestActivity", "Film detay hatası: " + e.getMessage());
                                            Toast.makeText(this, "Film detayları alınırken hata oluştu",
                                                    Toast.LENGTH_SHORT).show();
                                        } finally {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    },
                                    error -> {
                                        Log.e("SuggestActivity", "Detay API hatası: " + error.toString());
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(this, "Film detayları alınırken hata oluştu",
                                                Toast.LENGTH_SHORT).show();
                                    });

                            mRequestQueue.add(detailRequest);

                        } catch (Exception e) {
                            Log.e("SuggestActivity", "Film parse hatası: " + e.getMessage());
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Film bilgileri alınırken hata oluştu",
                                    Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("SuggestActivity", "API hatası: " + error.toString());
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Film önerileri alınırken hata oluştu",
                                Toast.LENGTH_SHORT).show();
                    });

            mRequestQueue.add(request);

        } catch (Exception e) {
            Log.e("SuggestActivity", "Genel hata: " + e.getMessage());
            Toast.makeText(this, "Beklenmeyen bir hata oluştu",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void displayMovie(TMDbMovie movie) {
        titleTxt.setVisibility(View.VISIBLE);
        titleTxt.setText(movie.getTitle());
        movieRateTxt.setText(String.format(Locale.US, "Puan: %.1f", movie.getVoteAverage()));

        int runtime = movie.getRuntime() != null ? movie.getRuntime() : 0;
        movieTimeTxt.setText(String.format(Locale.US, "Süre: %d dakika", runtime));

        movieSummaryInfo.setText(movie.getOverview());

        if (movie.getPosterPath() != null) {
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                    .into(movieImage);
        }
    }
}