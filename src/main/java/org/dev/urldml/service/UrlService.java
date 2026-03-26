package org.dev.urldml.service;

import org.dev.urldml.exeption.ExpiredShortUrlException;
import org.dev.urldml.exeption.NotFoundException;
import org.dev.urldml.model.UrlEntity;
import org.dev.urldml.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UrlService {
    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Duration MAX_REDIS_TTL = Duration.ofHours(24);
    private final StringRedisTemplate redisTemplate;
    private final UrlRepository urlRepository;
    private final Duration urlTtl;

    public UrlService(StringRedisTemplate redisTemplate, UrlRepository urlRepository, @Value("${APP_URL_TTL:30d}")
    Duration urlTtl) {
        this.redisTemplate = redisTemplate;
        this.urlRepository = urlRepository;
        if (urlTtl.isNegative() || urlTtl.isZero()) {
            log.error("Invalid URL TTL configuration: {}", urlTtl);
            throw new IllegalArgumentException("url ttl must be a positive integer");
        }
        this.urlTtl = urlTtl;
        log.info("UrlService initialized with TTL: {}", urlTtl);
    }

    public UrlEntity create(String url, int len) {
        log.debug("Creating short URL for: {}, requested length: {}", url, len);
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime expiredTime = createTime.plus(urlTtl);
        long id = urlRepository.getNextSequenceValue();
        String token = toBase62(id);
        if (token.length() > len) {
            log.warn("Generated token length {} exceeds requested length {} for id {}",
                    token.length(), len, id);
            throw new IllegalArgumentException("required length is to small");
        }
        UrlEntity savedUrl = urlRepository.save(new UrlEntity(id, leftPadWithZero(token, len), url, createTime, expiredTime));
        cacheUrl(savedUrl.getToken(), savedUrl.getUrl(), createTime, expiredTime);
        log.info("Created short URL: {}", savedUrl);
        return savedUrl;
    }

    public String getUrl(String token) {
        log.info("Looking up URL for token: {}", token);
        String cachedUrl = redisTemplate.opsForValue().get(token);
        if (cachedUrl != null) {
            log.info("Cache hit for token: {}", token);
            return cachedUrl;
        }
        UrlEntity url = urlRepository.getUrlEntityByToken(token).orElseThrow(() -> {
            log.warn("Token not found: {}", token);
            return new NotFoundException("cant find url mapped to this token");
        });
        if (url.getExpiredAt().isAfter(LocalDateTime.now())) {
            log.warn("Expired token accessed: {}, expired at {}", token, url.getExpiredAt());
            throw new ExpiredShortUrlException("token mapped to this url was expired");
        }
        cacheUrl(token, url.getUrl(), LocalDateTime.now(), url.getExpiredAt());
        log.info("Resolved token {} to URL: {}", token, url.getUrl());
        return url.getUrl();
    }

    private String toBase62(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Value must be positive");
        }

        StringBuilder builder = new StringBuilder();
        long current = value;
        while (current > 0) {
            int index = (int) (current % ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
            current = current / ALPHABET.length();
        }
        return builder.reverse().toString();
    }

    private String leftPadWithZero(String code, int requiredLength) {
        if (code.length() >= requiredLength) {
            return code;
        }
        return "0".repeat(requiredLength - code.length()) + code;
    }
    private void cacheUrl(String token, String originalUrl,LocalDateTime now, LocalDateTime expiresAt) {
        Duration ttl= Duration.between(now, expiresAt);
        if (ttl.isZero() || ttl.isNegative()) {
            return;
        }
        Duration effectiveTtl = ttl.compareTo(MAX_REDIS_TTL) < 0 ? ttl : MAX_REDIS_TTL;
        redisTemplate.opsForValue().set(token, originalUrl, effectiveTtl);
        log.debug("Cached token {} with TTL {} ", token, effectiveTtl);
    }

}
