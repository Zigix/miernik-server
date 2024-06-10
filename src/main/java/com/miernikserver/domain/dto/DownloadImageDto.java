package com.miernikserver.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadImageDto {
    private MediaType imageMediaType;
    private ByteArrayResource imageData;
}
