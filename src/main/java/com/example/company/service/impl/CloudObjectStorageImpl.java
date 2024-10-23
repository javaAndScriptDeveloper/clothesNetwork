package com.example.company.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.company.service.CloudObjectStorage;
import org.springframework.beans.factory.annotation.Value;

public class CloudObjectStorageImpl implements CloudObjectStorage {

    private final AmazonS3 s3Client;

    CloudObjectStorageImpl(
            @Value("${api.s3.accessKey}") String accessKey, @Value("${api.s3.secretKey}") String secretKey) {
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
    }
}
