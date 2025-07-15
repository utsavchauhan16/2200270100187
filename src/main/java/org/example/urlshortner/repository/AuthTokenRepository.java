package org.example.urlshortner.repository;

import org.example.urlshortner.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findTopByOrderByIdDesc();
}
