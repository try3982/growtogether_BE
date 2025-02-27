package com.campfiredev.growtogether.member.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class AmazonS3Config {
    private Credentials credentials = new Credentials();
    private String region= "ap-southeast-2";

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(credentials.getAccessKey(), credentials.getSecretKey());
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
    @Getter
    @Setter
    public static class Credentials {
        @Value("${cloud.aws.credentials.access-key}")
        private String accessKey;

        @Value("${cloud.aws.credentials.secret-key}")
        private String secretKey;
    }
}