package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.Quiz;
import com.lms.Learning_Managment_System.Service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    QuizService quizService;

    @PostMapping("/addQuiz/{courseTitle}")
    public ResponseEntity<Quiz> addQuiz(@PathVariable("courseTitle") String courseTitle ,
                                        @RequestBody Quiz quiz) {
        try {
            quizService.createQuiz(courseTitle, quiz);
            return new ResponseEntity<>(quiz, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------

    @GetMapping("/getQuiz/{courseTitle}/{quizeId}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable("courseTitle") String courseTitle ,
                                              @PathVariable("quizeId") String quizeId) {
        Quiz q = quizService.getQuizById(courseTitle, quizeId);
        if (q == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(q , HttpStatus.OK);
    }

    // ------------------

    @GetMapping("/{courseTitle}")
    public ResponseEntity<List<Quiz>> getAllQuizzes(@PathVariable("courseTitle") String courseTitle) {
        try {
            List<Quiz> quizzes = quizService.getQuizzes(courseTitle);
            if (quizzes.isEmpty())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(quizzes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ------------------

    @DeleteMapping("/deleteQuize/{courseTitle}/{quizeId}")
    public ResponseEntity<String> deleteQuize(@PathVariable("courseTitle") String courseTitle ,
                                              @PathVariable("quizeId") String quizeId) {
        quizService.deleteQuiz(courseTitle, quizeId);
        return new ResponseEntity<>("Deleted Quiz", HttpStatus.OK);
    }

    // ------------------

    @DeleteMapping("/{courseTitle}/clearQuizes")
    public ResponseEntity<String> clearQuize(@PathVariable("courseTitle") String courseTitle) {
        quizService.clearQuizzesForCourse(courseTitle);
        return new ResponseEntity<>("Deleted All Quizzes from this course", HttpStatus.OK);
    }

    // --------------------


}
