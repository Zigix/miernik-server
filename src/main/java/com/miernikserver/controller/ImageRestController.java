package com.miernikserver.controller;

import com.miernikserver.domain.dto.DownloadImageDto;
import com.miernikserver.domain.dto.ImageInfoDto;
import com.miernikserver.domain.dto.RateImageDto;
import com.miernikserver.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
@CrossOrigin
@RequiredArgsConstructor
public class ImageRestController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("category") String category) {
        imageService.uploadImage(file, category);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) throws IOException {
        DownloadImageDto downloadImageDto = imageService.downloadImage(id);

        return ResponseEntity.ok()
                .contentType(downloadImageDto.getImageMediaType())
                .contentLength(downloadImageDto.getImageData().contentLength())
                .body(downloadImageDto.getImageData());
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<ImageInfoDto> getImageInfo(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getImageInfo(id));
    }

    @GetMapping("/random")
    public ResponseEntity<List<Long>> getRandomImagesIds(@RequestParam("counter") Integer counter) {
        return ResponseEntity.ok(imageService.getRandomImagesIds(counter));
    }

    @PostMapping("/vote")
    public ResponseEntity<?> rateImage(@RequestBody List<RateImageDto> rateImageDtoList) {
        imageService.rateListOfImages(rateImageDtoList);
        return ResponseEntity.ok().build();
    }
}
