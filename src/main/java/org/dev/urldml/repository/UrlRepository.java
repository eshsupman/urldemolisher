package org.dev.urldml.repository;

import jakarta.transaction.Transactional;
import org.dev.urldml.model.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> getUrlEntityByToken(String token);
    
    @Query(value = "SELECT nextval('urls_seq')", nativeQuery = true)
    Long getNextSequenceValue();

    @Modifying
    @Transactional
    @Query(value = """
    DELETE FROM urls
    WHERE id IN (
        SELECT id FROM urls
        WHERE expired_at <= :now
        LIMIT 1000
    )
    """, nativeQuery = true)
    int deleteExpired(@Param("now") LocalDateTime now);
}
