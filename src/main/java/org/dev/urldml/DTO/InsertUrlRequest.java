package org.dev.urldml.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class InsertUrlRequest {
    @NotBlank(message = "URL must not be blank")
    @URL(message = "URL must be valid")
    String url;

    @Min(value = 4, message = "Length must be at least 4")
    @Max(value = 16, message = "Length must be at most 16")
    int len;
}
