package com.miernikserver.service;

import com.miernikserver.domain.dto.DownloadImageDto;
import com.miernikserver.domain.dto.ImageInfoDto;
import com.miernikserver.domain.dto.RateImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    void uploadImage(MultipartFile imageFile, String category);

    DownloadImageDto downloadImage(Long imageId);

    List<ImageInfoDto> rateListOfImages(List<RateImageDto> rateImageDtoList);

    ImageInfoDto getImageInfo(Long imageId);

    List<ImageInfoDto> getImagesInfos(List<Long> imagesIds);

    List<Long> getRandomImagesIds(int counter);
}
