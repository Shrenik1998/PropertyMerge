package com.merge.properties.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelFileInitializer {
    
    public static void initializeExcelFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            try (Workbook workbook = new XSSFWorkbook()) {
                // Create the Common sheet with headers
                Sheet sheet = workbook.createSheet("Common");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Key");
                headerRow.createCell(1).setCellValue("Old Value");
                headerRow.createCell(2).setCellValue("New Value");
                
                // Auto-size columns
                for (int i = 0; i < 3; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // Write the workbook to the file
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                }
            }
        }
    }
}
