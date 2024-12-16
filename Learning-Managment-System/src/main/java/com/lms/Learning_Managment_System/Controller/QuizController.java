package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.Quiz;
import com.lms.Learning_Managment_System.Service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/quizzess")
public class QuizController {

    @Autowired
    QuizService quizService;

    @GetMapping
    public ResponseEntity<List<Quiz>> getAllQuizzes() throws Exception {
        List<Quiz> quizzes = quizService.getQuizzes();
        return new ResponseEntity<>(quizzes , HttpStatus.OK);
    }
}
