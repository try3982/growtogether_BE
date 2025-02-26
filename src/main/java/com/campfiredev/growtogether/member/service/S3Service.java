package com.campfiredev.growtogether.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 파일 업로드 (파일 키 반환)
    public String uploadFile(MultipartFile file) {
        String fileKey = generateFileKey(file.getOriginalFilename());

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return fileKey;  // 파일 키 반환

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
        }
    }

    // S3 URL 반환 (파일 키를 기반으로 URL 생성)
    public String getFileUrl(String fileKey) {
        return amazonS3.getUrl(bucketName, fileKey).toString();
    }

    // 🔥 URL → 파일 키 변환 (추가된 부분)
    public String extractFileKeyFromUrl(String fileUrl) {
        String prefix = amazonS3.getUrl(bucketName, "").toString(); // S3 기본 URL
        return fileUrl.replace(prefix, ""); // 파일 키 추출
    }

    // 파일 키 생성 (랜덤 UUID + 확장자 유지)
    private String generateFileKey(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }
}
