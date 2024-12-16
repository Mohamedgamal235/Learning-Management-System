package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.Quiz;
import com.lms.Learning_Managment_System.Model.course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class QuizService {

    @Autowired
    private courseService courseService;

    private static final String JASON_QUIZ_FILE = "quizzes.json";
    private Map<String , List<Quiz>> quizzes = new HashMap<>();
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

    public void createQuiz(String courseTitle , Quiz quiz){
        validateCourse(courseTitle);
        quizzes.putIfAbsent(courseTitle , new ArrayList<>());
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