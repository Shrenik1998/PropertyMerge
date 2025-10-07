package com.merge.properties.utils;

import com.merge.properties.entity.CommonKeys;
import com.merge.properties.entity.Keys;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {
    
    public static void updateCommonKeysInExcel(String filePath, List<CommonKeys> commonKeys) throws IOException {
    // Create parent directories if they don't exist
    File file = new File(filePath);
    File parentDir = file.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
        if (!parentDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
        }
    }
    
    // Create a new workbook
    try (Workbook workbook = new XSSFWorkbook()) {
        // Create a new sheet
        Sheet sheet = workbook.createSheet("Common");
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Key");
        headerRow.createCell(1).setCellValue("Old Value");
        headerRow.createCell(2).setCellValue("New Value");
        
        // Start writing from row 1 (0-based index, so row 1 is the second row)
        int rowNum = 1;
        
        // Write common keys data
        for (CommonKeys key : commonKeys) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(key.getKey());
            row.createCell(1).setCellValue(key.getOldValue() != null ? key.getOldValue() : "");
            row.createCell(2).setCellValue(key.getNewValue() != null ? key.getNewValue() : "");
        }
        
        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Ensure parent directories exist
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean dirsCreated = parent.mkdirs();
            if (!dirsCreated) {
                throw new IOException("Failed to create parent directories: " + parent.getAbsolutePath());
            }
            System.out.println("Created parent directories: " + parent.getAbsolutePath());
        }
        
        // Write the output to the file
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            System.out.println("Writing Excel file to: " + file.getAbsolutePath());
            workbook.write(outputStream);
            outputStream.flush();
            System.out.println("Successfully wrote " + file.length() + " bytes to file");
        }
    } catch (Exception e) {
        throw new IOException("Error creating Excel file: " + e.getMessage(), e);
    }
}
    public static void updateUniqueKeysInExcel(String filePath, List<Keys> keysList, String keyType) throws IOException {
        // Create parent directories if they don't exist
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }
        // Create a new workbook
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create a new sheet
            Sheet sheet = workbook.createSheet(keyType);

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Key");
            headerRow.createCell(1).setCellValue("Value");

            // Start writing from row 1 (0-based index, so row 1 is the second row)
            int rowNum = 1;

            // Write common keys data
            for (Keys key : keysList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(key.getKey());
                row.createCell(1).setCellValue(key.getValue() != null ? key.getValue() : "");
            }

            // Auto-size columns
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ensure parent directories exist
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean dirsCreated = parent.mkdirs();
                if (!dirsCreated) {
                    throw new IOException("Failed to create parent directories: " + parent.getAbsolutePath());
                }
                System.out.println("Created parent directories: " + parent.getAbsolutePath());
            }

            // Write the output to the file
            try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
                System.out.println("Writing Excel file to: " + file.getAbsolutePath());
                workbook.write(outputStream);
                outputStream.flush();
                System.out.println("Successfully wrote " + file.length() + " bytes to file");
            }
        } catch (Exception e) {
            throw new IOException("Error creating Excel file: " + e.getMessage(), e);
        }
    }
}
