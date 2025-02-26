package com.campfiredev.growtogether.member.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage()); // 커스텀 예외 처리로 변경 필요
        }
    }

    public void deleteFile(String fileKey) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileKey));
        } catch (AmazonServiceException e) {
            throw new RuntimeException("S3에서 파일 삭제 실패: " + e.getMessage());
        }
    }


    // S3 URL 반환
    public String getFileUrl(String fileKey) {
        return amazonS3.getUrl(bucketName, fileKey).toString();
    }


    //   URL → 파일 키 변환
    public String extractFileKeyFromUrl(String fileUrl) {
        String prefix = amazonS3.getUrl(bucketName, "").toString();
        return fileUrl.replace(prefix, "");
    }

    // 파일 키 생성
    private String generateFileKey(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }
}
