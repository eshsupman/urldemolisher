package org.dev.urldml.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Table(name = "urls",indexes = {@Index( name = "token_idx",columnList = "token")})
@Entity
@Data
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "token", nullable = false, unique = true)
    String token;
    @Column(name = "url", nullable = false, unique = true, columnDefinition = "TEXT")
    private String url;
    @Column(name = "created_at")
    private LocalDate createdAt;
    @Column(name = "expired_at")
    private LocalDate expiredAt;


    public UrlEntity(Long id,String token, String url, LocalDate createdAt) {
        this.id = id;
        this.token = token;
        this.url = url;
        this.createdAt = createdAt;
        this.expiredAt = createdAt.plusMonths(1);
    }

    public UrlEntity() {

    }
}
