package org.dev.urldml.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsertUrlResponse {
    String token;
    String shortUrl;
    String originalUrl;
}
