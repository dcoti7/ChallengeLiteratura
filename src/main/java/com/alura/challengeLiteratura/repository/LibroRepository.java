package com.alura.challengeLiteratura.repository;

import com.alura.challengeLiteratura.model.LibroRepo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<LibroRepo,Long> {
    Optional<LibroRepo> findByNombreLibro(String nombreLibro);
    List<LibroRepo> findByLenguaje (String lenguaje);
}
