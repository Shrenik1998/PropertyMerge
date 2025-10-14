package com.merge.properties.controller;

import com.merge.properties.entity.CommonKeys;
import com.merge.properties.entity.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.nio.file.Files;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import com.merge.properties.utils.ExcelUtil;
@RestController
@RequestMapping("/api")
public class MergeController {

    @Value("${output.directory}")
    private String outputPath;

    @PostMapping("/merge")
    public ResponseEntity<?> mergeProperties(
        @RequestParam("file1") MultipartFile file1,
        @RequestParam("file2") MultipartFile file2) {

        if (file1.isEmpty() || file2.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Both files are required");
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }

        if (!file1.getOriginalFilename().endsWith(".properties") ||
                !file2.getOriginalFilename().endsWith(".properties")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Both files must be .properties files");
            return ResponseEntity.badRequest().body(Map.of("error", error));
        }

        Map<String, String> preUpgMap = new HashMap<>();
        Map<String, String> postUpgMap = new HashMap<>();
        
        try (InputStream input1 = file1.getInputStream();
             InputStream input2 = file2.getInputStream()) {
            
            // Load first properties file into map
            Properties preUpg = new Properties();
            preUpg.load(input1);
            preUpg.stringPropertyNames()
                  .forEach(key -> preUpgMap.put(key, preUpg.getProperty(key)));
            
            // Load second properties file into map
            Properties postUpg = new Properties();
            postUpg.load(input2);
            postUpg.stringPropertyNames()
                  .forEach(key -> postUpgMap.put(key, postUpg.getProperty(key)));

            List<CommonKeys> commonKeys = new ArrayList<>();
            List<Keys> oldUniqueKeys = new ArrayList<>();
            List<Keys> newUniqueKeys = new ArrayList<>();

            //keys that are there in old but not in new
            for (Object key : preUpg.keySet()) {
                if (postUpg.containsKey(key)) {
                    commonKeys.add(new CommonKeys((String)key, preUpg.getProperty((String)key), postUpg.getProperty((String)key)));
                }else{
                    System.out.println("test test");
                    oldUniqueKeys.add(new Keys((String)key, preUpg.getProperty((String)key)));
                }
            }
            //keys that are there in old but not in new
            for (Object key : postUpg.keySet()) {
                if (!preUpg.containsKey(key)) {
                    newUniqueKeys.add(new Keys((String)key, postUpg.getProperty((String)key)));
                }
            }


            File excelFile = null;
            try {
                // Create the output directory if it doesn't exist
                String outputDir = outputPath;
                File outputDirFile = new File(outputDir);
                if (!outputDirFile.exists()) {
                    boolean dirCreated = outputDirFile.mkdirs();
                    if (!dirCreated) {
                        throw new IOException("Failed to create output directory: " + outputDir);
                    }
                    System.out.println("Created output directory: " + outputDirFile.getAbsolutePath());
                }
                
                // Create Excel file in the output directory
                String excelFileName = "merged_properties_" + System.currentTimeMillis() + ".xlsx";
                String excelFilePath = outputDir + "/" + excelFileName;
                excelFile = new File(excelFilePath);
                
                System.out.println("Attempting to create Excel file at: " + excelFile.getAbsolutePath());
                
                // Update the Excel file with all sheets
                ExcelUtil.updateAllSheetsInExcel(excelFilePath, commonKeys, oldUniqueKeys, newUniqueKeys);
                
                // Verify the file was created and has content
                if (!excelFile.exists() || excelFile.length() == 0) {
                    throw new IOException("Failed to generate Excel file or file is empty");
                }
                
                // Read the file into a byte array
                byte[] fileContent = Files.readAllBytes(excelFile.toPath());
                
                // Set up response headers for file download
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", excelFileName);
                headers.setContentLength(fileContent.length);
                
                System.out.println("Successfully generated Excel file at: " + excelFile.getAbsolutePath());
                
                // Return the file as a response
                return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
                    
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error processing Excel file: " + e.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Error processing Excel file: " + e.getMessage());
                errorResponse.put("details", e.toString());
                return ResponseEntity.status(500).body(errorResponse);
            }
            // Removed the finally block that was deleting the file
            
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error processing files: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
