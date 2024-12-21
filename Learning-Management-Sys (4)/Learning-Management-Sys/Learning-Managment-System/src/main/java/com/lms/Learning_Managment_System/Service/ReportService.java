package com.lms.Learning_Managment_System.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import java.util.List;

@Service
    public class ReportService {

    private static final String BASE_DIRECTORY = Paths.get(System.getProperty("user.dir"), "generated-reports").toString();

    public ReportService() {
        java.io.File directory = new java.io.File(BASE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void saveWorkbook(Workbook workbook, String filePath) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            System.out.println("Report saved in project directory: " + filePath);
        } finally {
            workbook.close();
        }
    }

    public String generateGradesReport(List<Map<String, Object>> gradesData, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Grades Report");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Student Email");
        headerRow.createCell(1).setCellValue("First Name");
        headerRow.createCell(2).setCellValue("Last Name");
        headerRow.createCell(3).setCellValue("Quiz ID");
        headerRow.createCell(4).setCellValue("Grade");

        // Populate rows with grades data
        int rowIndex = 1;
        for (Map<String, Object> record : gradesData) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue((String) record.get("email"));
            row.createCell(1).setCellValue((String) record.get("firstName"));
            row.createCell(2).setCellValue((String) record.get("lastName"));
            row.createCell(3).setCellValue((String) record.get("quizId"));
            row.createCell(4).setCellValue((Integer) record.get("grade"));
        }

        // Save the file
        String filePath = Paths.get(BASE_DIRECTORY, fileName + ".xlsx").toString();
        saveWorkbook(workbook, filePath);
        return filePath;
    }


    // -------------------------------------------------

    public String generateAttendanceReport(List<Map<String, Object>> attendanceData, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance Report");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Student Email");
        headerRow.createCell(1).setCellValue("First Name");
        headerRow.createCell(2).setCellValue("Last Name");
        headerRow.createCell(3).setCellValue("Attendance");

        // Populate rows with attendance data
        int rowIndex = 1;
        for (Map<String, Object> record : attendanceData) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue((String) record.get("email"));
            row.createCell(1).setCellValue((String) record.get("firstName"));
            row.createCell(2).setCellValue((String) record.get("lastName"));
            row.createCell(3).setCellValue((Boolean) record.get("attendance") ? "Attended" : "Absent");
        }

        // Save the file
        String filePath = Paths.get(BASE_DIRECTORY, fileName + ".xlsx").toString();
        saveWorkbook(workbook, filePath);
        return filePath;
    }

}
