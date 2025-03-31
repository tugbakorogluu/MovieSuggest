//kullanıcının favori listesine eklediği filmlerle ilgili bilgileri depolamak

package com.example.project155.Domain;

public class FavoriteMovie {
    private String documentId;
    private Integer movieId;
    private String title;
    private String posterPath;
    private Double voteAverage;
    private String userId;
    private Long timestamp;

    public FavoriteMovie() {}

    public FavoriteMovie(TMDbMovie movie, String userId) {
        this.movieId = movie.getId();
        this.title = movie.getTitle();
        this.posterPath = movie.getPosterPath();
        this.voteAverage = movie.getVoteAverage();
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }

    public FavoriteMovie(Integer movieId, String title, String posterPath, Double voteAverage, String userId) {
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Double getVoteAverage() {
        return voteAverage != null ? voteAverage : 0.0;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getRating() {
        return getVoteAverage();
    }

    public String getFullPosterPath() {
        return posterPath != null ? "https://image.tmdb.org/t/p/w500" + posterPath : null;
    }
}