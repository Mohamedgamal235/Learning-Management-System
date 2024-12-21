package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Service.GradeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private GradeReportService gradeReportService;

    // Endpoint to generate grades report
    @PostMapping("/grades")
    public ResponseEntity<String> generateGradesReport(@RequestParam String courseTitle,
                                                       @RequestParam int instructorId,
                                                       @RequestParam String fileName) {
        try {
            String filePath = gradeReportService.generateGradeReportForCourse(courseTitle, instructorId, fileName);
            return new ResponseEntity<>("Grades report generated: " + filePath , HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to generate attendance report
    @PostMapping("/attendance")
    public ResponseEntity<String> generateAttendanceReport(@RequestParam String fileName) {

        try {
            String filePath = gradeReportService.generateAttendanceReportForLessons(fileName);
            return new ResponseEntity<>("Attendance report generated: " + filePath , HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
