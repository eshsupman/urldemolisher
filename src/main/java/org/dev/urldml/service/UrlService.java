package org.dev.urldml.service;

import org.dev.urldml.model.UrlEntity;
import org.dev.urldml.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UrlService {
    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final UrlRepository urlRepository;
    private final Duration urlTtl;

    public UrlService(UrlRepository urlRepository, @Value("${APP_URL_TTL:30d}")
    Duration urlTtl) {
        this.urlRepository = urlRepository;
        if (urlTtl.isNegative() || urlTtl.isZero()) {
            throw new IllegalArgumentException("url ttl must be a positive integer");
        }
        this.urlTtl = urlTtl;
    }

    public UrlEntity create(String url, int len) {
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime expiredTime = createTime.plus(urlTtl);
        long id = urlRepository.getNextSequenceValue();
        String token = toBase62(id);
        if (token.length() > len) {
            throw new IllegalArgumentException("required length is to small");
        }
        UrlEntity savedUrl = urlRepository.save(new UrlEntity(id, leftPadWithZero(token, len), url, createTime, expiredTime));
        return savedUrl;
    }

    public String getUrl(String token)  {
        UrlEntity url = urlRepository.getUrlEntityByToken(token).orElseThrow(() -> new RuntimeException());
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


}
