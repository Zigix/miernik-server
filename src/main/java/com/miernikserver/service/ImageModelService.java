package com.miernikserver.service;

import com.miernikserver.domain.dto.DownloadImageDto;
import com.miernikserver.domain.dto.ImageInfoDto;
import com.miernikserver.domain.dto.RateImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageModelService {

    void uploadImage(MultipartFile imageFile);

    DownloadImageDto downloadImage(Long imageId);

    void rateListOfImages(List<RateImageDto> rateImageDtoList);

    ImageInfoDto getImageInfo(Long imageId);

    List<Long> getRandomImagesIds(int counter);
}
