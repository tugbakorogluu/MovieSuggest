//, bir film türünü temsil eder ve türün adını (name) ve benzersiz kimliğini (id) tutar.
package com.example.project155.Domain;

public class GenresItem {
    private String name;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
