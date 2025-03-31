//Bu sayfa, bir film hakkında çeşitli bilgileri (film ID'si, başlık, poster yolu, özet,
// oylama ortalaması, çıkış tarihi, süre) saklayan ve bunlara erişim sağlayan bir TMDbMovie
// sınıfını tanımlar.

package com.example.project155.Domain;

import com.google.gson.annotations.SerializedName;

public class TMDbMovie {
    @SerializedName("id")
    private Integer id;
    @SerializedName("title")
    private String title;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("vote_average")
    private Double voteAverage;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("runtime")
    private Integer runtime;





    public TMDbMovie() {}
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public String getOverview() {
        return overview;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }
    public Double getVoteAverage() {
        return voteAverage != null ? voteAverage : 0.0;
    }
    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public Integer getRuntime() {
        return runtime != null ? runtime : 0;
    }
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }




}