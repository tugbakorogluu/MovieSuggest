//Bu sınıf, API'den alınan veri sayfalama bilgilerini
// (şu anki sayfa, sayfa başına öğe sayısı, toplam sayfa sayısı ve toplam öğe sayısı) temsil eder.

package com.example.project155.Domain;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Metadata {
    @SerializedName("current_page")
    @Expose
    private String currentPage;
    @SerializedName("per_page")
    @Expose
    private Integer perPage;
    @SerializedName("page_count")
    @Expose
    private Integer pageCount;
    @SerializedName("total_count")
    @Expose
    private Integer totalCount;

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
