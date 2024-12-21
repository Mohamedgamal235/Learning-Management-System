package com.lms.Learning_Managment_System;

import com.lms.Learning_Managment_System.Model.Quiz;
import com.lms.Learning_Managment_System.Model.Question;
import com.lms.Learning_Managment_System.Service.QuizService;
import com.lms.Learning_Managment_System.Service.courseService;
import com.lms.Learning_Managment_System.Model.course;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuizTest {

    private static QuizService quizService;
    private static courseService courseService;

    private static final String courseTitle = "Math";
    private static final int instructorId = 4;

    private static Quiz quiz ;

    @Test
    @Order(1)
    void setUp() {
        courseService = new courseService();
        quizService = new QuizService();
        quizService.setCourseService(courseService);

        course crs = new course();
        crs.setCourse_title(courseTitle);
        crs.setInstructor_id(instructorId);
        crs.setCrs_duration_inMonths(3);
        crs.setCourse_description("Basic Math Course");
        crs.setAvailableForRegistration(true);

        List<Question> questions = new ArrayList<>();
        questions.add(new Question(1, "MCQ", "What is 2 + 2?", Arrays.asList("3", "4", "5"), "4"));
        questions.add(new Question(2, "TRUE_FALSE", "Is the sky blue?", null, "true"));
        questions.add(new Question(3, "MCQ", "What is 5 + 5?", Arrays.asList("10", "25", "20"), "10"));
        crs.setQuestionBank(questions);

        courseService.addCourse(crs);


    }


    @Test
    @Order(2)
    void creatQuizTest(){
        quiz = new Quiz();
        quiz.setType("quiz");
        quiz.setAssessmentID("Q111");
        quiz.setAssessmentName("Math Quiz");

        quizService.setIdOfInstructorCourses(instructorId , courseTitle);
        quizService.createQuiz(courseTitle, quiz, instructorId);

        assertNotNull(quiz.getQuestions(), "Quiz questions should not be null");
        assertEquals(3, quiz.getQuestions().size(), "Quiz should have 3 questions");

        List<Quiz> quizzes = quizService.getQuizzesForInstructor(instructorId, courseTitle);
        for (Quiz q : quizzes)
            System.out.println(q.toString());
        assertEquals(1, quizzes.size(), "Instructor should have 1 quiz");
        assertEquals("Q111", quizzes.get(0).getAssessmentID(), "Quiz ID should match");
    }

    @Test
    @Order(3)
    void getQuiz() {
        Quiz retrievedQuiz = quizService.getQuizById(courseTitle, "Q111", instructorId);
        assertNotNull(retrievedQuiz, "Quiz should not be null");
        assertEquals("Q111", retrievedQuiz.getAssessmentID(), "Quiz ID should match");
    }

    @Test
    @Order(4)
    void attemptQuizAndReceiveFeedback() {
        Map<Integer, String> studentAnswers = new HashMap<>();
        studentAnswers.put(1, "4"); // Correct answer
        studentAnswers.put(2, "true"); // Correct answer
        studentAnswers.put(3, "25"); // Incorrect answer

        int studentId = 1001;
        Map<String, Object> result = quizService.attemptQuiz(courseTitle, "Q111", studentId, studentAnswers);

        assertNotNull(result, "Result should not be null");
        assertEquals(66, result.get("score"), "Score should be 66%");
        assertEquals("Good!", result.get("feedback"), "Feedback should match expected value");
    }

    @Test
    @Order(5)
    void generateFeedback() {
        assertEquals("Excellent work!", quizService.generateFeedback(90, 10));
        assertEquals("Very Good!", quizService.generateFeedback(75, 10));
        assertEquals("Good!", quizService.generateFeedback(60, 10));
        assertEquals("You passed", quizService.generateFeedback(50, 10));
        assertEquals("Oh, You not pass , Don't give up and try again!", quizService.generateFeedback(40, 10));
    }

    @Test
    @Order(6)
    void studentGrades() {
        Map<Integer, Map<String, Integer>> grades = quizService.getAllStudentGradesForCourse(courseTitle, instructorId);

        assertNotNull(grades, "Grades map should not be null");
        assertTrue(grades.containsKey(1001), "Student ID 1001 should exist");
        assertTrue(grades.get(1001).containsKey("Q111"), "Quiz ID Q111 should exist for student 1001");
        assertEquals(66, grades.get(1001).get("Q111"), "Score for student 1001 should be 66%");
    }

    @Test
    @Order(10)
    void deleteQuizTest() {
        quizService.deleteQuiz("Math", "Q111", 4);
        List<Quiz> quizzes = quizService.getQuizzesForInstructor(4 , "Math");
        assertTrue(quizzes.isEmpty(), "Quiz list should be empty after deletion");
    }

}
