package org.example.urlshortner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrl {

    @Id
    private String shortcode;

    @Column(nullable = false)
    private String originalUrl;

    private LocalDateTime createdAt;

    private LocalDateTime expiry;

    private int redirectCount = 0;
}
