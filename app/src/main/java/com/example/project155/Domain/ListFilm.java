//Bu sınıf, film verilerini (film listesi ve sayfa numarası) içeren bir yanıtı temsil eder.
package com.example.project155.Domain;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListFilm {
    @SerializedName("results")
    private List<FilmItem> results;

    @SerializedName("page")
    private int page;

    // Getter ve Setter metodları
    public List<FilmItem> getResults() {
        return results;
    }

    public void setResults(List<FilmItem> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}