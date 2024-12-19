package com.lms.Learning_Managment_System.Controller;
import com.lms.Learning_Managment_System.Model.course;
import com.lms.Learning_Managment_System.Service.NotificationService;
import com.lms.Learning_Managment_System.Service.courseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.Learning_Managment_System.Service.student_coursesService;

import java.security.Provider;
import java.util.List;

@RestController
@RequestMapping("/student")
public class student_courses_Controller {
    @Autowired
    private student_coursesService student_coursesService;
    @Autowired
    private UserController UserController;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private courseService courseService;

    @PostMapping("/enroll/{student_id}/{course_title}")
    public ResponseEntity<String> enrollInCourse(@PathVariable int student_id,
                                                 @PathVariable String course_title) {
        if(!UserController.getLoggedInStudents().containsValue(student_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in students to enroll in courses");
        }
        course crs = courseService.search_course(course_title);
        if (crs == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Course: " + course_title+" does not exist ");
        }
        if (!crs.isAvailableForRegistration()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(" Course: " + course_title + " is not available for registration");
        }
        String response = student_coursesService.enroll_in_Course(student_id, course_title);
        String StudentMessage = "We are pleased to inform you that you have successfully enrolled in the course "+ course_title + " We encourage you to actively engage with the course materials and participate in all activities to maximize your learning experience.";
        notificationService.add(StudentMessage,student_id);

        String InstructorMessage ="Kindly be informed that a new student, "+UserController.getStudentNameById(student_id)+"has enrolled in your course, "+course_title;
        notificationService.add(InstructorMessage,courseService.getInstructorId(course_title));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{student_id}/View_Available_courses_forRegistration")
    public ResponseEntity<?> viewAvailableCoursesForRegistration(@PathVariable int student_id) {
        if(!UserController.getLoggedInStudents().containsValue(student_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in students to view available courses");
        }
        List<course>courses= student_coursesService.getAvail_courses();
        return ResponseEntity.ok(courses);

    }
}