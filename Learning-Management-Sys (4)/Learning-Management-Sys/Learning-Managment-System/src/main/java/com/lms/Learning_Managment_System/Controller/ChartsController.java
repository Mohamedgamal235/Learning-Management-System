package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Service.ChartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/charts")
public class ChartsController {
    @Autowired
    private ChartsService chartsService;

    @Autowired
    private UserController userController;

    @GetMapping("/{instructor_id}/{courseTitle}")
    public ResponseEntity<?> generateAnalytics(@PathVariable int instructor_id,
                                               @PathVariable String courseTitle) {
        if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You must be a logged in instructor to view analytics");
        }
        try {
            chartsService.generateAssignmentChart(courseTitle);
            chartsService.generateAverageGradesChart(courseTitle);
            chartsService.generateCourseCompletionRateChart(courseTitle);
            Map<String, String> response = new HashMap<>();
            response.put("assignmentChart", "./charts/" + courseTitle + "_assignments.png");
            response.put("averageGradeChart", "./charts/" + courseTitle + "_average_grades.png");
            response.put("completionRate", "./charts/" + courseTitle + "_course_completion_rate.png");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating charts: " + e.getMessage());
        }
    }
}