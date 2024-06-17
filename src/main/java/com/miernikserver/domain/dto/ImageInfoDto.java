package com.miernikserver.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageInfoDto {
    private Long imageId;
    private String category;
    private Integer allVotesCounter;
    private Integer isArtVotesCounter;
    private Integer isNotArtVotesCounter;
}
