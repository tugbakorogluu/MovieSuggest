//Bu sınıf, her biri bir resim içerdiği varsayılan "SliderItems" nesnelerini oluşturur ve
// bu resimlere erişim sağlamak için getter ve setter metodları sağlar.

package com.example.project155.Domain;

public class SliderItems {
    private int image;

    public SliderItems(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
