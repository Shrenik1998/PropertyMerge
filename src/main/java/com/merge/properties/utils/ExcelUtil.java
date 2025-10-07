package com.merge.properties.utils;

import com.merge.properties.entity.CommonKeys;
import com.merge.properties.entity.Keys;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {
    
    public static void updateAllSheetsInExcel(String filePath, 
                                           List<CommonKeys> commonKeys, 
                                           List<Keys> oldUniqueKeys, 
                                           List<Keys> newUniqueKeys) throws IOException {
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
            // Create sheets
            createCommonKeysSheet(workbook, "Common Keys", commonKeys);
            createKeysSheet(workbook, "Old Unique Keys", oldUniqueKeys);
            createKeysSheet(workbook, "New Unique Keys", newUniqueKeys);
            
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
    
    private static void createCommonKeysSheet(Workbook workbook, String sheetName, List<CommonKeys> commonKeys) {
        if (commonKeys == null || commonKeys.isEmpty()) return;
        
        // Create a new sheet
        Sheet sheet = workbook.createSheet(sheetName);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Key");
        headerRow.createCell(1).setCellValue("Old Value");
        headerRow.createCell(2).setCellValue("New Value");
        
        // Write data
        int rowNum = 1;
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
    }
    
    private static void createKeysSheet(Workbook workbook, String sheetName, List<Keys> keysList) {
        if (keysList == null || keysList.isEmpty()) return;
        
        // Create a new sheet
        Sheet sheet = workbook.createSheet(sheetName);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Key");
        headerRow.createCell(1).setCellValue("Value");
        
        // Write data
        int rowNum = 1;
        for (Keys key : keysList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(key.getKey());
            row.createCell(1).setCellValue(key.getValue() != null ? key.getValue() : "");
        }
        
        // Auto-size columns
        for (int i = 0; i < 2; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
