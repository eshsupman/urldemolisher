package org.dev.urldml.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls",indexes = {@Index( name = "token_idx",columnList = "token")})
@Data
public class UrlEntity {
    @Id
    private Long id;
    @Column(name = "token", nullable = false, unique = true)
    private String token;
    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;


    public UrlEntity(Long id, String token, String url, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.id = id;
        this.token = token;
        this.url = url;
        this.createdAt = createdAt;
        this.expiredAt = createdAt;
    }

    public UrlEntity() {

    }
}
