//Bu sınıf, TMDb API'sinden alınan film verilerini (film listesi, sayfa numarası,
// toplam sonuç ve sayfa sayısı) tutan bir yanıt modelidir.

package com.example.project155.Domain;

import java.util.List;

public class TMDbMovieResponse {
    private List<TMDbMovie> results;
    private int page;
    private int total_results;
    private int total_pages;

    public List<TMDbMovie> getResults() {
        return results;
    }

    public void setResults(List<TMDbMovie> results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return total_results;
    }

    public void setTotalResults(int total_results) {
        this.total_results = total_results;
    }

    public int getTotalPages() {
        return total_pages;
    }

    public void setTotalPages(int total_pages) {
        this.total_pages = total_pages;
    }

    // Boş constructor
    public TMDbMovieResponse() {}

    // Tüm alanları içeren constructor
    public TMDbMovieResponse(List<TMDbMovie> results, int page, int total_results, int total_pages) {
        this.results = results;
        this.page = page;
        this.total_results = total_results;
        this.total_pages = total_pages;
    }

    @Override
    public String toString() {
        return "TMDbMovieResponse{" +
                "results=" + results +
                ", page=" + page +
                ", total_results=" + total_results +
                ", total_pages=" + total_pages +
                '}';
    }
}
