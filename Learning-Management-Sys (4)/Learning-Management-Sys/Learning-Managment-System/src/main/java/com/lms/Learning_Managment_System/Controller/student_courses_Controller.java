package com.lms.Learning_Managment_System.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.Learning_Managment_System.Service.student_coursesService;
@RestController
@RequestMapping("/student")
public class student_courses_Controller {
    @Autowired
    private student_coursesService student_coursesService;
    @Autowired
    private UserController UserController;

    @PostMapping("/enroll/{student_id}/{course_title}")
    public ResponseEntity<String> enrollInCourse(@PathVariable int student_id,
                                                 @PathVariable String course_title) {

        String response = student_coursesService.enroll_in_Course(student_id, course_title);
        return ResponseEntity.ok(response);
    }
}