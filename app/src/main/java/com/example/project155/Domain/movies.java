//Bu sınıf, bir filmin başlığını ve yönetmenini tutmak için kullanılan bir model sınıfıdır.

package com.example.project155.Domain;

public class movies {
    private String title;
    private String director;

    // Getter ve Setter metodlarını ekleyin
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }
}

