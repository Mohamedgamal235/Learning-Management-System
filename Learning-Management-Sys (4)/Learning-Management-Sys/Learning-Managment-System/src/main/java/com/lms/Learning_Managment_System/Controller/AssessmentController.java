package com.lms.Learning_Managment_System.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/manage_assessment")
public class AssessmentController {

    @GetMapping("/{type}")
    public ResponseEntity<String> typeAssessment(@PathVariable String type) {
        if ("assignment".equalsIgnoreCase(type)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/assignments")
                    .body("Redirecting to /assignments");
        } else if ("quiz".equalsIgnoreCase(type)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/quizzes")
                    .body("Redirecting to /quizzes");
        }
        return ResponseEntity.badRequest().body("Unknown assessment type.");
    }

}