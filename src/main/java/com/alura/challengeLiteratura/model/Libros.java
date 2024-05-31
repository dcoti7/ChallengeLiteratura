package com.alura.challengeLiteratura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Libros(
        @JsonAlias("title") String nombreLibro,
        @JsonAlias("authors") List<Autor> autor,
        @JsonAlias("languages") List<String> lenguaje,
        @JsonAlias("download_count") Integer numeroDescargas
) {
}
