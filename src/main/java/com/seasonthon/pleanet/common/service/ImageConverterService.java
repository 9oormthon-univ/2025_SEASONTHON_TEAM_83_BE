package com.seasonthon.pleanet.common.service;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImageConverterService {

    // HEIC 파일이면 JPEG으로 변환해서 반환, 그 외 파일은 그대로 byte[] 반환
    public ConvertedImage convertIfHeic(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IOException("Cannot convert an empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        boolean isHeic = originalFilename != null && originalFilename.toLowerCase().endsWith(".heic");

        if (isHeic) {
            // HEIC → JPEG 변환
            Path tempInputFile = Files.createTempFile("original_", ".heic");
            Path tempOutputFile = Files.createTempFile("converted_", ".jpeg");

            try {
                file.transferTo(tempInputFile);

                ConvertCmd cmd = new ConvertCmd();
                IMOperation op = new IMOperation();
                op.addImage(tempInputFile.toAbsolutePath().toString());
                op.addImage(tempOutputFile.toAbsolutePath().toString());

                cmd.run(op);

                byte[] bytes = Files.readAllBytes(tempOutputFile);

                // 원래 파일명 확장자를 .jpeg로 바꿔주기
                String convertedName = originalFilename.replaceAll("(?i)\\.heic$", ".jpeg");

                return new ConvertedImage(bytes, convertedName, "image/jpeg");

            } finally {
                Files.deleteIfExists(tempInputFile);
                Files.deleteIfExists(tempOutputFile);
            }
        } else {
            // HEIC이 아닌 경우 그대로 사용
            byte[] bytes = file.getBytes();
            return new ConvertedImage(bytes, originalFilename, file.getContentType());
        }
    }

    // 변환된 파일 정보를 담는 DTO
    public record ConvertedImage(byte[] bytes, String filename, String contentType) {}
}
