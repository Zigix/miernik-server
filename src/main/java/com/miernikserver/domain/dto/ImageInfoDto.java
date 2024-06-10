package com.miernikserver.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageInfoDto {
    private Long imageId;
    private Integer allVotesCounter;
    private Integer isArtVotesCounter;
    private Integer isNotArtVotesCounter;
}
