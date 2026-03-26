package org.dev.urldml.controller;

import jakarta.validation.Valid;
import org.dev.urldml.DTO.InsertUrlRequest;
import org.dev.urldml.DTO.InsertUrlResponse;
import org.dev.urldml.model.UrlEntity;
import org.dev.urldml.service.UrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {
    private static final Logger log = LoggerFactory.getLogger(UrlController.class);
    private final UrlService urlService;

    public UrlController(UrlService urlService){
        this.urlService = urlService;
    }

    @PostMapping("/api/urls")
    public ResponseEntity<InsertUrlResponse> createShortUrl(@Valid @RequestBody InsertUrlRequest insertUrlRequest){
        log.info("request {}", insertUrlRequest);
        UrlEntity createdUrl = urlService.create(insertUrlRequest.getUrl(),insertUrlRequest.getLen());
        String token = createdUrl.getToken();
        String originalUrl = createdUrl.getUrl();
        String shortUrl = "/" + token;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new InsertUrlResponse(token,shortUrl, originalUrl));
    }

    @GetMapping("/{token}")
    public ResponseEntity<Void> redirect(@PathVariable String token){
        String foundedUrl = urlService.getUrl(token);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(foundedUrl)).build();

    }
}
