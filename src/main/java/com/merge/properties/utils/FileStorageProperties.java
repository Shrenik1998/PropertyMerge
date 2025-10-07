package com.merge.properties.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Configuration
@ConfigurationProperties(prefix = "merged")
public class FileStorageProperties {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageProperties.class);
    
    private String outputPath;

    @PostConstruct
    public void init() {
        logger.info("Initializing FileStorageProperties with outputPath: {}", outputPath);
        if (outputPath == null || outputPath.trim().isEmpty()) {
            logger.error("Output path is not configured. Please set 'merged.output.path' in application.properties");
        } else {
            // Ensure the path ends with a separator
            if (!outputPath.endsWith(File.separator)) {
                outputPath = outputPath + File.separator;
            }
            logger.info("Final output path: {}", outputPath);
            
            // Try to create the directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (created) {
                    logger.info("Created output directory: {}", outputPath);
                } else {
                    logger.warn("Failed to create output directory: {}", outputPath);
                }
            }
            
            // Check if the path is writable
            if (!dir.canWrite()) {
                logger.error("Output directory is not writable: {}", outputPath);
            }
        }
    }

    public String getOutputPath() {
        if (outputPath == null || outputPath.trim().isEmpty()) {
            throw new IllegalStateException("Output directory is not configured. Please check 'merged.output.path' in application.properties");
        }
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
