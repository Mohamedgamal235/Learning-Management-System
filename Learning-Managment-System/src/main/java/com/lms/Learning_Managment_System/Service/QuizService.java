package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.Question;
import com.lms.Learning_Managment_System.Model.Quiz;
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

    public QuizService() {
        loadFromJsonFile() ;
    }


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

    public void createQuiz(String courseTitle , Quiz quiz){
        quizzes.putIfAbsent(courseTitle , new ArrayList<>());
        quizzes.get(courseTitle).add(quiz);
        saveQuizzesToJsonFile();
    }

    public List<Quiz> getQuizzes() {
        return null ;
    }

    public Quiz getQuizById(String courseTitle){

        return null;
    }

    public void deleteQuiz(){

    }

    public void clearAllQuizzes(){

    }

    public void saveQuizzesToJsonFile(){

    }

}
