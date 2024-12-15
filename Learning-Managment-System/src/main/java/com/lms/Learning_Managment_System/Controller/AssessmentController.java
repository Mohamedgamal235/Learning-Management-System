package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.Assignment;
import com.lms.Learning_Managment_System.Model.assignmentSubmission;
import com.lms.Learning_Managment_System.Service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage_assignments")
public class AssessmentController {
    @Autowired
    private AssessmentService assessmentService;
    private assignmentSubmission assignmentSubmission;
    @PostMapping("/addAssignment/{courseTitle}")
    public ResponseEntity<String> addAssignment(@PathVariable String courseTitle, @RequestBody Assignment assignment) {
        try {
            assessmentService.addAssignment(courseTitle, assignment);
            return ResponseEntity.ok("Assignment added successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/upload-assignment/{courseTitle}/{assignmentId}")
    public ResponseEntity<String> uploadAssignmentFile(@PathVariable String courseTitle, @PathVariable String assignmentId,
                                                       @RequestParam("file") MultipartFile file,
                                                       @RequestParam("studentId") String studentId,
                                                       @RequestParam("studentName") String studentName) {
        try {
            assessmentService.saveFile(file, courseTitle, assignmentId, studentId, studentName);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
    @PutMapping("/gradeAssignment/{courseTitle}/{assignmentId}/{studentId}")
    public ResponseEntity<String> gradeAssignment(@PathVariable String courseTitle,
                                                  @PathVariable String assignmentId,
                                                  @PathVariable String studentId,
                                                  @RequestParam("grade") String grade,
                                                  @RequestParam("feedback") String feedback) {
        try {
            assessmentService.gradeAssignment(courseTitle, assignmentId, studentId, grade, feedback);
            return ResponseEntity.ok("Assignment graded successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/getFeedback/{courseTitle}/{assignmentId}/{studentId}")
    public ResponseEntity<String> getFeedback(@PathVariable String courseTitle,
                                              @PathVariable String assignmentId,
                                              @PathVariable String studentId) {
        try {
            List<assignmentSubmission> submissions = assessmentService.getSubmissionsWithFeedback(courseTitle, assignmentId);
            for (assignmentSubmission submission : submissions) {
                if (submission.getStudentId().equals(studentId)) {
                    return ResponseEntity.ok("Grade: " + submission.getGrade() + "\nFeedback: " + submission.getFeedback());
                }
            }
            return ResponseEntity.status(404).body("Submission not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    @GetMapping("/trackSubmissions/{courseTitle}/{assignmentId}")
    public ResponseEntity<List<assignmentSubmission>> trackSubmissions(@PathVariable String courseTitle,
                                                                       @PathVariable String assignmentId) {
        try {
            List<assignmentSubmission> submissions = assessmentService.trackSubmissionsProgress(courseTitle, assignmentId);
            return ResponseEntity.ok(submissions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
    @GetMapping("/progress/{courseTitle}")
    public ResponseEntity<Map<String, Object>> getCourseAnalytics(@PathVariable String courseTitle) {
        try {
            Map<String, Object> progressData = assessmentService.getProgressAnalytics(courseTitle);
            return ResponseEntity.ok(progressData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/getAssignments/{courseTitle}")
    public ResponseEntity<List<Assignment>> getAssignments(@PathVariable String courseTitle) {
        List<Assignment> assignments = assessmentService.getAssignments(courseTitle);
        if (assignments.isEmpty()) {
            System.out.println("No assignments found for course: " + courseTitle); // Debugging line
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(assignments);
    }

}
