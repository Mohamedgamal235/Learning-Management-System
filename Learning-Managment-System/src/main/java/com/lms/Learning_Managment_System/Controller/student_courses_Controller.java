package com.lms.Learning_Managment_System.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.lms.Learning_Managment_System.Service.student_coursesService;
@RestController
@RequestMapping("/student/{enrolled_student_id}")
public class student_courses_Controller {
@Autowired
    private student_coursesService student_coursesService;

    @PostMapping("/enroll/{course_title}")
    public ResponseEntity<String> enrollInCourse(@PathVariable int enrolled_student_id, @PathVariable String course_title) {
        String response = student_coursesService.enroll_in_Course(enrolled_student_id, course_title);
        return ResponseEntity.ok(response);

}
}
