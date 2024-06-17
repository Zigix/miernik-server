package com.miernikserver.service;

import com.miernikserver.domain.dto.DownloadImageDto;
import com.miernikserver.domain.dto.ImageInfoDto;
import com.miernikserver.domain.dto.RateImageDto;
import com.miernikserver.domain.model.ImageModel;
import com.miernikserver.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicImageService implements ImageService {

    private static final String IMAGES_DIR = "images";

    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public void uploadImage(MultipartFile imageFile, String category) {
        try (ZipInputStream zipInputStream = new ZipInputStream(imageFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File file = convertZipEntryToFile(entry, zipInputStream);

                ImageModel imageModel = new ImageModel();
                imageModel.setLocation(file.getPath());
                imageModel.setCategory(category);
                imageRepository.save(imageModel);
            }
        } catch (IOException e) {
            log.warn("Problem with saving file {}", imageFile);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public DownloadImageDto downloadImage(Long imageId) {
        ImageModel imageModel = imageRepository.findById(imageId)
                .orElseThrow();

        File file = new File(imageModel.getLocation());

        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));

            MediaType mediaType = getMediaTypeForFileName(file.getName());

            return DownloadImageDto.builder()
                    .imageMediaType(mediaType)
                    .imageData(resource)
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void rateListOfImages(List<RateImageDto> rateImageDtoList) {
        rateImageDtoList.forEach(i -> imageRepository.addVoteToImage(i.getImageId(), i.getVote()));
    }

    @Override
    @Transactional
    public ImageInfoDto getImageInfo(Long imageId) {
        ImageModel imageModel = imageRepository.findById(imageId)
                .orElseThrow();
        List<Integer> votesList = imageModel.getVotes();

        return ImageInfoDto.builder()
                .imageId(imageModel.getId())
                .category(imageModel.getCategory())
                .allVotesCounter(votesList.size())
                .isArtVotesCounter((int) (votesList.stream().filter(v -> v == 1).count()))
                .isNotArtVotesCounter((int) (votesList.stream().filter(v -> v == 0).count()))
                .build();
    }

    @Override
    public List<Long> getRandomImagesIds(int counter) {
        List<Long> idList = imageRepository.findAllIds();
        Collections.shuffle(idList);
        return counter >= idList.size() ? idList : idList.subList(0, counter);
    }

    private File convertZipEntryToFile(ZipEntry zipEntry, ZipInputStream zipInputStream) throws IOException {
        Path targetDir = Paths.get(IMAGES_DIR);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        File file = new File(targetDir.toFile(), String.format("%s-%s", System.currentTimeMillis(), zipEntry.getName()));

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = zipInputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
        return file;
    }

    private MediaType getMediaTypeForFileName(String fileName) {
        String fileExtension = getFileExtension(fileName);
        return switch (fileExtension.toLowerCase()) {
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
}
