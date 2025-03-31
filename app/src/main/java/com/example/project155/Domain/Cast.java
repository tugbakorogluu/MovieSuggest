//Bu sınıf, bir filmin oyuncularını (cast) temsil eder.

package com.example.project155.Domain;

public class Cast {
    private int id;
    private String name;
    private String profile_path; // Bu field tanımı eksikti
    private String character;

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public String getCharacter() {
        return character;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}