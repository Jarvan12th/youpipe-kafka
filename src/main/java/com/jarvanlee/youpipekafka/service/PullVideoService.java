package com.jarvanlee.youpipekafka.service;

import com.jarvanlee.youpipekafka.common.RawOssConfiguration;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class PullVideoService {

    private MinioClient minioClient;

    @Autowired
    private RawOssConfiguration rawOssConfiguration;

    public File pull(String url) {
        File outputFile = new File("temp.mp4");
        try {
            minioClient = MinioClient.builder()
                    .endpoint(rawOssConfiguration.getEndpoint())
                    .credentials(rawOssConfiguration.getAccessKey(), rawOssConfiguration.getSecretKey())
                    .build();

            try (GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(rawOssConfiguration.getBucket())
                            .object(url)
                            .build());
                 OutputStream os = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                InputStream inputStream = response;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
            outputFile.delete(); // Clean up if there was an error
            return null;
        }
    }
}
