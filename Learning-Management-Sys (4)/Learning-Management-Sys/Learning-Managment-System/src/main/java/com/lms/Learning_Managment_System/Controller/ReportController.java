package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Service.GradeReportService;
import com.lms.Learning_Managment_System.Service.courseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lms.Learning_Managment_System.Model.* ;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private GradeReportService gradeReportService;

    @Autowired
    private UserController userController;

    @Autowired
    private courseService courseservice;

    @PostMapping("/quiz/grades")
    public ResponseEntity<String> generateGradesReport(@RequestParam String courseTitle,
                                                       @RequestParam int userID,
                                                       @RequestParam String fileName) {
        boolean isInstructor = userController.getLoggedInInstructors().containsValue(userID);
        boolean isAdmin = userController.getLoggedInAdmins().containsValue(userID);

        if (!isInstructor && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You must be a logged-in instructor or admin to add a new course.");
        }

        try {
            List<course> crss = courseservice.getAllCourses();
            int instructorID = -1 ;
            for (course crs : crss){
                if (crs.getCourse_title().equalsIgnoreCase(courseTitle)){
                    instructorID = crs.getInstructor_id();
                }
            }
            if (instructorID == -1)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("This course not Found");

            String filePath = gradeReportService.generateGradeReportForCourse(courseTitle, instructorID, fileName);
            return new ResponseEntity<>("Grades report generated: " + filePath , HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to generate attendance report
    @PostMapping("/attendance")
    public ResponseEntity<String> generateAttendanceReport(@RequestParam String fileName,
                                                           @RequestParam int userID) {

        boolean isInstructor = userController.getLoggedInInstructors().containsValue(userID);
        boolean isAdmin = userController.getLoggedInAdmins().containsValue(userID);

        if (!isInstructor && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You must be a logged-in instructor or admin to add a new course.");
        }

        try {
            String filePath = gradeReportService.generateAttendanceReportForLessons(fileName);
            return new ResponseEntity<>("Attendance report generated: " + filePath , HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/assignments/grades")
    public ResponseEntity<String> generateAssignmentGradesReport(@RequestParam String courseTitle,
                                                                 @RequestParam int userID,
                                                                 @RequestParam String fileName) {

        boolean isInstructor = userController.getLoggedInInstructors().containsValue(userID);
        boolean isAdmin = userController.getLoggedInAdmins().containsValue(userID);

        if (!isInstructor && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You must be a logged-in instructor or admin to add a new course.");
        }

        List<course> crss = courseservice.getAllCourses();
        boolean found = false ;
        for (course crs : crss){
            if (crs.getCourse_title().equalsIgnoreCase(courseTitle)){
                found = true ;
                break;
            }
        }
        if (!found)
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("This course not Found");

        try {
            String filePath = gradeReportService.generateAssignmentGradesReport(courseTitle, fileName);
            return new ResponseEntity<>("Assignment Grades Report generated: " + filePath, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
