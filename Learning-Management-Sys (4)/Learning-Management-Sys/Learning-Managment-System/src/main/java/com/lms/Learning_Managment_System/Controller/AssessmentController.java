package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.Assignment;
import com.lms.Learning_Managment_System.Model.assignmentSubmission;
import com.lms.Learning_Managment_System.Service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage_assessment")
public class AssessmentController {

    @GetMapping("/{type}")
    public ResponseEntity<String> typeAssessment(@PathVariable String type) {
        if ("assignment".equalsIgnoreCase(type)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/assignments")
                    .body("Redirecting to /assignment");
        } else if ("quiz".equalsIgnoreCase(type)) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/quizzes")
                    .body("Redirecting to /quizzes");
        }
        return ResponseEntity.badRequest().body("Unknown assessment type.");
    }

}