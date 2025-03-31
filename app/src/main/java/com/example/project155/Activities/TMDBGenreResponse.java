//API'den alınan tür verilerini saklamak ve işlemek için kullanılır.

package com.example.project155.Domain;

import java.util.ArrayList;

public class TMDBGenreResponse {
    private ArrayList<GenresItem> genres;
    public ArrayList<GenresItem> getGenres() {
        return genres;
    }
    public void setGenres(ArrayList<GenresItem> genres) {
        this.genres = genres;
    }
}