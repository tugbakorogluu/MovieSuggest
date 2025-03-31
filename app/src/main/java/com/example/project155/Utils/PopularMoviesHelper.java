//Bu sınıf, popülerlik değeri belirli bir eşiğin üzerinde olan filmleri filtreler,
// karıştırır ve belirli sayıda seçip döndürür.

package com.example.project155.Utils;

import com.example.project155.Domain.FilmItem;
import com.example.project155.Domain.ListFilm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PopularMoviesHelper {
    public static ListFilm filterAndRandomizeMovies(ListFilm originalList, int count, double minPopularity) {
        List<FilmItem> filteredMovies = new ArrayList<>();

        // Filter movies by popularity
        for (FilmItem movie : originalList.getResults()) {
            if (movie.getPopularity() > minPopularity) {
                filteredMovies.add(movie);
            }
        }

        // Shuffle the filtered list
        Collections.shuffle(filteredMovies, new Random(System.currentTimeMillis()));

        // Take only required number of movies
        int endIndex = Math.min(count, filteredMovies.size());
        List<FilmItem> selectedMovies = new ArrayList<>(
                filteredMovies.subList(0, endIndex)
        );

        // Create new ListFilm object with selected movies
        ListFilm result = new ListFilm();
        result.setResults(selectedMovies);
        return result;
    }
}