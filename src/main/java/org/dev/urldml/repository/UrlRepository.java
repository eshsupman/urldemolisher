package org.dev.urldml.repository;

import org.dev.urldml.model.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {
    Optional<UrlEntity> getUrlEntityByToken(String token);
    @Query(value = "SELECT nextval('urls_seq')", nativeQuery = true)
    Long getNextSequenceValue();
}
