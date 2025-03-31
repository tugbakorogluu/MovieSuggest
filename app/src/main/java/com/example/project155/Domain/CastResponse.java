//, bir filmdeki oyuncu listesiyle ilgili API yanıtını temsil eder.

package com.example.project155.Domain;

import java.util.List;

public class CastResponse {
    private List<Cast> cast;

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }
}