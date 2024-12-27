package com.lms.Learning_Managment_System.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


@Service
    public class ReportService {

    private static final String BASE_DIRECTORY = Paths.get(System.getProperty("user.dir"), "generated-reports").toString();

    public ReportService() {
        File directory = new File(BASE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void saveWorkbook(Workbook workbook, String filePath) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath) ;
        workbook.write(fileOut);
        System.out.println("Report saved in project directory: " + filePath);

        workbook.close();
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

        // values rows
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
        //                                      student Data
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance Report");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Student Email");
        headerRow.createCell(1).setCellValue("First Name");
        headerRow.createCell(2).setCellValue("Last Name");
        headerRow.createCell(3).setCellValue("lesson");
        headerRow.createCell(4).setCellValue("Attendance");

        // values rows
        int rowIndex = 1;
        for (Map<String, Object> record : attendanceData) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue((String) record.get("email"));
            row.createCell(1).setCellValue((String) record.get("firstName"));
            row.createCell(2).setCellValue((String) record.get("lastName"));
            row.createCell(3).setCellValue((String) record.get("lesson"));
            row.createCell(4).setCellValue((Boolean) record.get("attendance") ? "Attended" : "Absent");
        }

        // Save the file
        String filePath = Paths.get(BASE_DIRECTORY, fileName + ".xlsx").toString();
        saveWorkbook(workbook, filePath);
        return filePath;
    }

    public String generateAssignmentGradesReport(List<Map<String, String>> assignmentGradesData, String fileName) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Assignment Grades Report");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Student Email");
        headerRow.createCell(1).setCellValue("First Name");
        headerRow.createCell(2).setCellValue("Last Name");
        headerRow.createCell(3).setCellValue("Assignment ID");
        headerRow.createCell(4).setCellValue("Grade");
        headerRow.createCell(5).setCellValue("Feedback");

        // values row
        int rowIndex = 1;
        for (Map<String, String> record : assignmentGradesData) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(record.get("email"));
            row.createCell(1).setCellValue(record.get("firstName"));
            row.createCell(2).setCellValue(record.get("lastName"));
            row.createCell(3).setCellValue(record.get("assignmentId"));
            row.createCell(4).setCellValue(record.get("grade")); // Assuming grade is String
            row.createCell(5).setCellValue(record.get("feedback"));
        }

        // Save the file
        String filePath = Paths.get(BASE_DIRECTORY, fileName + ".xlsx").toString();
        saveWorkbook(workbook, filePath);
        return filePath;
    }


}
