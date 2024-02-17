package com.jarvanlee.youpipekafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class VideoEncodingService {

    @Autowired
    private PullVideoService pullVideoService;

    @Autowired
    private PushVideoService pushVideoService;

    @KafkaListener(topics = "video-encode-topic", groupId = "video-encode-group")
    public void listenForVideoFiles(String message) {
        log.info("Received message for video encoding: {}", message);
        process(message);
    }

    private void process(String videoFilePath) {
        File videoFile = pullVideoService.pull(videoFilePath);
        if (videoFile != null) {
            File encodedVideoFile = encodeVideo(videoFile);
            if (encodedVideoFile != null) {
                boolean pushResult = pushVideoService.push(encodedVideoFile, "encoded_" + videoFilePath);
                if (pushResult) {
                    log.info("Encoded video pushed successfully: {}", "encoded_" + videoFilePath);
                } else {
                    log.error("Failed to push encoded video: {}", "encoded_" + videoFilePath);
                }
            } else {
                log.error("Video encoding failed for: {}", videoFilePath);
            }
        } else {
            log.error("Failed to pull video: {}", videoFilePath);
        }
    }

    private File encodeVideo(File videoFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", videoFile.getAbsolutePath(), "-c:v", "libx264", "-crf", "23", "-c:a", "aac", "-b:a", "192k", "-strict", "experimental", "output.mp4");
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return new File("output.mp4");
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Exception occurred during video encoding", e);
        }
        return null;
    }
}
