package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.Quiz;
import com.lms.Learning_Managment_System.Model.User;
import com.lms.Learning_Managment_System.Model.course;
import com.lms.Learning_Managment_System.Service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    QuizService quizService;

    @Autowired
    UserController userController;

    @Autowired
    com.lms.Learning_Managment_System.Service.courseService courseService;

    // -----------------------------------------------------------------------------

    private ResponseEntity<?> validateInstructorForCourse(int instructorId) {
        List<Integer> instr = userController.getInstructorsId();

        boolean found = false;
        for (int i = 0; i < instr.size(); i++) {
            if (instr.get(i) == instructorId) {
                found = true;
                break;
            }
        }

        if (!found)
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You must log in as an instructor.");

        return null;
    }

    // -----------------------------------------------------------------------------

    @PostMapping("/{instructorId}/{courseTitle}/addQuiz")
    public ResponseEntity<?> addQuiz(@PathVariable("instructorId") int instructorId,
                                     @PathVariable("courseTitle") String courseTitle,
                                     @RequestBody Quiz quiz) {
        ResponseEntity<?> validationResponse = validateInstructorForCourse(instructorId);
        if (validationResponse != null)
            return validationResponse;

        try {
            quizService.createQuiz(courseTitle, quiz, instructorId);
            return new ResponseEntity<>(quiz, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -----------------------------------------------------------------------------

    @GetMapping("/{instructorId}/{courseTitle}/getQuiz/{quizId}")
    public ResponseEntity<?> getQuiz(@PathVariable("instructorId") int instructorId,
                                     @PathVariable("courseTitle") String courseTitle,
                                     @PathVariable("quizId") String quizId) {
        // Validate instructor access
        ResponseEntity<?> validationResponse = validateInstructorForCourse(instructorId);
        if (validationResponse != null) return validationResponse;

        try {
            Quiz quiz = quizService.getQuizById(courseTitle, quizId, instructorId);
            if (quiz == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Quiz with ID: " + quizId + " not found.");
            }
            return new ResponseEntity<>(quiz, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -----------------------------------------------------------------------------

    @GetMapping("/{instructorId}/{courseTitle}")
    public ResponseEntity<?> getQuizzesByInstructor(@PathVariable("instructorId") int instructorId,
                                                    @PathVariable("courseTitle") String courseTitle) {
        ResponseEntity<?> validationResponse = validateInstructorForCourse(instructorId);
        if (validationResponse != null) return validationResponse;

        try {
            List<Quiz> quizzes = quizService.getQuizzesForInstructor(instructorId, courseTitle);
            return new ResponseEntity<>(quizzes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -----------------------------------------------------------------------------

    @DeleteMapping("/deleteQuiz/{courseTitle}/{instructorId}/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable("courseTitle") String courseTitle,
                                        @PathVariable("instructorId") int instructorId,
                                        @PathVariable("quizId") String quizId) {
        ResponseEntity<?> validationResponse = validateInstructorForCourse(instructorId);
        if (validationResponse != null) return validationResponse;

        try {
            quizService.deleteQuiz(courseTitle, quizId, instructorId);
            return new ResponseEntity<>("Quiz deleted successfully.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -----------------------------------------------------------------------------

    @DeleteMapping("/{courseTitle}/{instructorId}/clearQuizzes")
    public ResponseEntity<?> clearQuizzes(@PathVariable("courseTitle") String courseTitle,
                                          @PathVariable("instructorId") int instructorId) {
        ResponseEntity<?> validationResponse = validateInstructorForCourse(instructorId);
        if (validationResponse != null) return validationResponse;

        try {
            quizService.clearQuizzesForCourse(courseTitle, instructorId);
            return new ResponseEntity<>("All quizzes cleared for the course.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -----------------------------------------------------------------------------

    @GetMapping("/{courseTitle}/{quizId}/studentFeedback/{studentId}")
    public ResponseEntity<?> getStudentFeedback(@PathVariable("courseTitle") String courseTitle,
                                                @PathVariable("quizId") String quizId,
                                                @PathVariable("studentId") int studentId) {
        try {
            Quiz quiz = quizService.getQuizById(courseTitle, quizId); // No instructor validation for students
            Integer score = quiz.getStudentScores().get(studentId);

            if (score == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No score found for student ID: " + studentId);
            }

            String feedback = quizService.generateFeedback(score, quiz.getQuestions().size());
            return new ResponseEntity<>(Map.of("feedback", feedback, "score", score), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ----------------------------------
    // Endpoint: Student Attempts a Quiz
    // ----------------------------------

    @PostMapping("/{courseTitle}/{quizId}/attemptQuiz")
    public ResponseEntity<?> attemptQuiz(@PathVariable("courseTitle") String courseTitle ,
                                         @PathVariable("quizId") String quizId ,
                                         @RequestParam int studnetId ,
                                         @RequestBody Map<Integer, String> studentAnswers) {
        try{
            Map<String , Object> res = quizService.attemptQuiz(courseTitle, quizId, studnetId, studentAnswers);
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{courseTitle}/{quizId}/feedback/{studentId}")
    public ResponseEntity<?> getFeedbackAndScore(@PathVariable("courseTitle") String courseTitle,
                                                 @PathVariable("quizId") String quizId,
                                                 @PathVariable("studentId") int studentId) {
        try{
            Quiz quiz = quizService.getQuizById(courseTitle, quizId);

            if (!quiz.getStudentScores().containsKey(studentId))
                return new ResponseEntity<>( "No score or feedback found for student ID: " + studentId , HttpStatus.NOT_FOUND);

            int score = quiz.getStudentScores().get(studentId);
            String feedback = quizService.generateFeedback(score, quiz.getQuestions().size());
            return ResponseEntity.ok(Map.of("score", score, "feedback", feedback));
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{courseTitle}/{instructorId}/studentGrades")
    public ResponseEntity<?> getStudentGradesForCourse(@PathVariable("courseTitle")  String courseTitle ,
                                                       @PathVariable("instructorId") int instructorId) {
        try{
            Map<Integer, Map<String, Integer>> studentGrades = quizService.getAllStudentGradesForCourse(courseTitle, instructorId);
            return new ResponseEntity<>(studentGrades, HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
