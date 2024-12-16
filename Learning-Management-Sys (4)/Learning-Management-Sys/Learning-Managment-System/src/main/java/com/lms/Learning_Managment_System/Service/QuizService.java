package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.Question;
import com.lms.Learning_Managment_System.Model.Quiz;
import com.lms.Learning_Managment_System.Model.course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private courseService courseService;

    private static final String JASON_QUIZ_FILE = "quizzes.json";
    private Map<String, List<Quiz>> quizzes = new HashMap<>();


    @Autowired
    private com.lms.Learning_Managment_System.Controller.student_courses_Controller student_courses_Controller;

    public QuizService() {
        loadFromJsonFile() ;
    }

    // ------------------

    public void loadFromJsonFile(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            File file = new File(JASON_QUIZ_FILE);
            if (file.exists())
                quizzes = mapper.readValue(file , mapper.getTypeFactory().constructMapType(HashMap.class , String.class , List.class));
            else
                System.out.println("File does not exist");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // ------------------

    public void validateCourse(String courseTitle){
        course crs = courseService.search_course(courseTitle);
        if (crs == null)
            throw new IllegalArgumentException("Course not found.");
    }

    // ------------------

    public void addQuestionsToBank(String courseTitle, List<Question> questions) {
        validateCourse(courseTitle);
        course crs = courseService.search_course(courseTitle);
        List<Question> questionsCourse = crs.getQuestionBank();
        questionsCourse.addAll(questions);
        crs.setQuestionBank(questionsCourse);
        courseService.saveCoursesToFile();
    }

    // ------------------

//    public void createQuiz(String courseTitle , Quiz quiz){
//        validateCourse(courseTitle);
//        quizzes.putIfAbsent(courseTitle , new ArrayList<>());
//        quizzes.get(courseTitle).add(quiz);
//        saveQuizzesToJsonFile();
//    }

    // ------------------

    public void createQuiz(String courseTitle, Quiz quiz) {
        validateCourse(courseTitle);
        course crs = courseService.search_course(courseTitle);

        if (crs.getQuestionBank().isEmpty())
            throw new IllegalArgumentException("No questions available in the course's question bank.");

        // select questions randommm
        Collections.shuffle(crs.getQuestionBank());
        List<Question> questionsCourse = crs.getQuestionBank().stream().limit(3).collect(Collectors.toList());
        quiz.setQuestions(questionsCourse);

        quizzes.putIfAbsent(courseTitle, new ArrayList<>());
        quizzes.get(courseTitle).add(quiz);
        saveQuizzesToJsonFile();
    }

    // ------------------

    public List<Quiz> getQuizzes(String courseTitle) {
        validateCourse(courseTitle);
        return quizzes.getOrDefault(courseTitle , new ArrayList<>());
    }

    // ------------------

    public Quiz getQuizById(String courseTitle , String quizId) {
        validateCourse(courseTitle);
        List<Quiz> crsQuizzes = quizzes.getOrDefault(courseTitle , new ArrayList<>());
        return crsQuizzes.stream().filter(quiz ->
                quiz.getAssessmentID().equals(quizId)).findFirst().orElseThrow(null);
    }

    // Instructors can create a questions bank per course.
    // Randomized question selection for each quiz attempt.
    // Performance Tracking Student Progress Tracking: ○ Instructors can track quiz scores
    // Adminsand Instructors can generate excel reports on student performance (including grades and attendance).

    // ------------------

    public void deleteQuiz(String courseTitle, String quizId){
        validateCourse(courseTitle);
        List<Quiz> crsQuizzes = quizzes.get(courseTitle);
        if (crsQuizzes != null) {
            for (Quiz quiz : crsQuizzes) {
                if (quiz.getAssessmentID().equals(quizId)) {
                    quizzes.remove(quiz.getCourseTitle());
                    break;
                }
            }
            if (crsQuizzes.isEmpty()) {
                quizzes.remove(courseTitle);
            }
            saveQuizzesToJsonFile();
        }
    }

    // ------------------

    public void clearQuizzesForCourse(String courseTitle){
        validateCourse(courseTitle);
        quizzes.remove(courseTitle);
        saveQuizzesToJsonFile();
    }

    // ------------------

    public void saveQuizzesToJsonFile(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            mapper.writeValue(new File(JASON_QUIZ_FILE) , quizzes);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // ------------------
}