package com.jarvanlee.youpipekafka.service;

import com.jarvanlee.youpipekafka.common.EncodedOssConfiguration;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class PushVideoService {

    private MinioClient minioClient;

    @Autowired
    private EncodedOssConfiguration encodedOssConfiguration;

    public boolean push(File file, String url) {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(encodedOssConfiguration.getEndpoint())
                    .credentials(encodedOssConfiguration.getAccessKey(), encodedOssConfiguration.getSecretKey())
                    .build();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(encodedOssConfiguration.getBucket())
                            .object(url)
                            .stream(file.toURI().toURL().openStream(), file.length(), -1)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
