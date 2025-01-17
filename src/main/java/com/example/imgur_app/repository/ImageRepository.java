package com.example.imgur_app.repository;

import com.example.imgur_app.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Object> findByDeleteHash(String deleteHash);
}
