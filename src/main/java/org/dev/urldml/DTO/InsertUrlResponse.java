package org.dev.urldml.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsertUrlResponse {
    String token;
    String shortUrl;
    String originalUrl;
}
