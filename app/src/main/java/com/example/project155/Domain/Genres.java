//nesnelerinin bir listesi olan bir türler alanı içerir. Sınıf, türler listesini almak ve güncellemek için getter ve setter yöntemlerine sahiptir.
package com.example.project155.Domain;


import java.util.List;


public class Genres {

  private List<GenresItem> genres;

    public List<GenresItem> getGenres() {
        return genres;
    }

    public void setGenres(List<GenresItem> genres) {
        this.genres = genres;
    }
}
