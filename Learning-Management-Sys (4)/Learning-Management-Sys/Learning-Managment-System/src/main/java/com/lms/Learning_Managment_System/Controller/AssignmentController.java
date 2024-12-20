package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.Assignment;
import com.lms.Learning_Managment_System.Model.assignmentSubmission;
import com.lms.Learning_Managment_System.Model.enrolled_student;
import com.lms.Learning_Managment_System.Service.AssignmentService;
import com.lms.Learning_Managment_System.Service.EmailService;
import com.lms.Learning_Managment_System.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;
    private com.lms.Learning_Managment_System.Model.assignmentSubmission assignmentSubmission;
    @Autowired
    private UserController userController;
    @Autowired
    private com.lms.Learning_Managment_System.Service.student_coursesService student_coursesService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmailService mailService;
    @Autowired
    private EmailService emailService;

    // Inform student that there is an assignment
    @PostMapping("/{instructor_id}/addAssignment/{courseTitle}")
    public ResponseEntity<?> addAssignment(@PathVariable String courseTitle, @RequestBody Assignment assignment, @PathVariable int instructor_id) {
        try {
            if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in instructor to add assignments");
            }
            assignmentService.addAssignment(courseTitle, assignment);
            String message = "Dear Students,\n\nKindly be informed that a new assignment '"+assignment.getAssessmentName()+"' has been added to the course '"+ courseTitle+"'\nPlease ensure you submit it before the deadline: "+ assignment.getAssessmentDate();
            String subject = "New " +courseTitle +" Assignment!";
            List<enrolled_student> enrolledStudents = student_coursesService.getStudentsEnrolledInCourse(courseTitle);
            for (enrolled_student student : enrolledStudents) {
                Integer Userid = student.getEnrolled_student_id();
                String Usermail = student.getEnrolled_student_email();
                notificationService.add(message,Userid);
                mailService.sendMail(Usermail,subject,message);
            }
            return ResponseEntity.ok("Assignment added successfully");
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/{student_id}/upload-assignment/{courseTitle}/{assignmentId}")
    public ResponseEntity<?> uploadAssignmentFile(@PathVariable String courseTitle, @PathVariable String assignmentId,@PathVariable String student_id,
                                                       @RequestParam("file") MultipartFile file){
        try {
            if (!userController.getLoggedInStudents().containsValue(Integer.parseInt(student_id))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in student to upload assignments");
            }
            List<enrolled_student> enrolledStudents = student_coursesService.getStudentsEnrolledInCourse(courseTitle);
            boolean isEnrolled = enrolledStudents.stream().anyMatch(student -> student.getEnrolled_student_id() == Integer.parseInt(student_id));
            if (!isEnrolled) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: The student is not enrolled in this course");
            }
            assignmentService.saveFile(file, courseTitle, assignmentId, student_id);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
    @PutMapping("/{instructor_id}/gradeAssignment/{courseTitle}/{assignmentId}/{studentId}")
    public ResponseEntity<?> gradeAssignment(@PathVariable String courseTitle,
                                                  @PathVariable String assignmentId,
                                                  @PathVariable String studentId, @PathVariable int instructor_id,
                                                  @RequestParam("grade") String grade,
                                                  @RequestParam("feedback") String feedback) {
        try {
            if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in instructor to grade assignments");
            }
            List<enrolled_student> enrolledStudents = student_coursesService.getStudentsEnrolledInCourse(courseTitle);
            boolean isEnrolled = enrolledStudents.stream().anyMatch(student -> student.getEnrolled_student_id() == Integer.parseInt(studentId));
            if (!isEnrolled) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: The student is not enrolled in this course");
            }
            assignmentService.gradeAssignment(courseTitle, assignmentId, studentId, grade, feedback);
            String assignmentName =  assignmentService.getassinmentnameBYID(assignmentId,courseTitle);
            String message = "Dear student,\n\nkindly be informed that your assignment titled '"+ assignmentName +"' has been graded.\n\nPlease visit the assignment page to view your feedback and grade ";
            String subject = "Check you Grade!";
            String mail = getEmailByID(enrolledStudents,studentId);
            notificationService.add(message, Integer.parseInt(studentId));
            emailService.sendMail(mail,subject,message);
            return ResponseEntity.ok("Assignment graded successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //
    @GetMapping("/{student_id}/getFeedback/{courseTitle}/{assignmentId}")
    public ResponseEntity<?> getFeedback(@PathVariable String courseTitle,
                                              @PathVariable String assignmentId,
                                              @PathVariable String student_id) {
        try {
            if (!userController.getLoggedInStudents().containsValue(Integer.parseInt(student_id))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in student to receive feedback");
            }
            List<enrolled_student> enrolledStudents = student_coursesService.getStudentsEnrolledInCourse(courseTitle);
            boolean isEnrolled = enrolledStudents.stream().anyMatch(student -> student.getEnrolled_student_id() == Integer.parseInt(student_id));
            if (!isEnrolled) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You are not enrolled in this course");
            }
            List<assignmentSubmission> submissions = assignmentService.getSubmissionsWithFeedback(courseTitle, assignmentId);
            for (assignmentSubmission submission : submissions) {
                if (submission.getStudentId().equals(student_id)) {
                    return ResponseEntity.ok("Grade: " + submission.getGrade() + "\nFeedback: " + submission.getFeedback());
                }
            }
            return ResponseEntity.status(404).body("Submission not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    @GetMapping("/{instructor_id}/trackSubmissions/{courseTitle}/{assignmentId}")
    public ResponseEntity<?> trackSubmissions(@PathVariable int instructor_id,@PathVariable String courseTitle,
                                                                       @PathVariable String assignmentId) {
        try {
            if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in instructor to track assignments submissions");
            }
            List<assignmentSubmission> submissions = assignmentService.trackSubmissionsProgress(courseTitle, assignmentId);
            return ResponseEntity.ok(submissions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
    @GetMapping("/{instructor_id}/progress/{courseTitle}")
    public ResponseEntity<?> getCourseAnalytics(@PathVariable int instructor_id,@PathVariable String courseTitle) {
        try {
            if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in instructor to get Course Analytics");
            }
            Map<String, Object> progressData = assignmentService.getProgressAnalytics(courseTitle);
            return ResponseEntity.ok(progressData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/{student_id}/getAssignments/{courseTitle}")
    public ResponseEntity<?> getAssignments(@PathVariable String student_id,@PathVariable String courseTitle) {
        try {
            if (!userController.getLoggedInStudents().containsValue(Integer.parseInt(student_id))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in student to view assignments");
            }
            List<enrolled_student> enrolledStudents = student_coursesService.getStudentsEnrolledInCourse(courseTitle);
            for (enrolled_student enrolledStudent: enrolledStudents) {
                System.out.println(enrolledStudent.getEnrolled_student_id());
            }
            boolean isEnrolled = enrolledStudents.stream().anyMatch(student -> student.getEnrolled_student_id() == Integer.parseInt(student_id));
            if (!isEnrolled) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You are not enrolled in this course");
            }
            List<Assignment> assignments = assignmentService.getAssignments(courseTitle);
            if (assignments.isEmpty())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    public String getEmailByID(List<enrolled_student>enrolledStudents,String studentId){
        for (enrolled_student student : enrolledStudents) {
            if (student.getEnrolled_student_id() == Integer.parseInt(studentId)) {
                return student.getEnrolled_student_email();
            }

        }
        return null;
}
}
