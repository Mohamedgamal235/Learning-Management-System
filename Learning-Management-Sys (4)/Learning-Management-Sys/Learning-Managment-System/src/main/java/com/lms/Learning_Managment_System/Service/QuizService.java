package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Controller.UserController;
import com.lms.Learning_Managment_System.Model.Assessment;
import com.lms.Learning_Managment_System.Model.Quiz;
import com.lms.Learning_Managment_System.Model.Question;
import com.lms.Learning_Managment_System.Model.course;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class QuizService {

    @Autowired
    private courseService courseService;

    private static final String JSON_QUIZ_FILE = "quizzes.json";
    private Map<Integer, List<Quiz>> instructorQuizzes = new HashMap<>();
    private Map<Integer, String> instructorCourses = new HashMap<>();
    private Map<String , Quiz> quizWithId = new HashMap<>();
    @Autowired
    private UserController userController;

    public void setCourseService(courseService courseService) {
        this.courseService = courseService;
    }

    public void setIdOfInstructorCourses(int id , String courseTitle) {
        instructorCourses.put(id, courseTitle);
        System.out.println(instructorCourses.get(3));
    }

    public Map<Integer, String> getIdOfInstructor(){
        return instructorCourses ;
    }

    public QuizService() {
        loadFromJsonFile() ;
    }

    // ------------------------------------------------------------------

    private void loadFromJsonFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(JSON_QUIZ_FILE);

            if (file.exists() && file.length() > 0) {
                List<JsonNode> courseData = mapper.readValue(file, new TypeReference<List<JsonNode>>() {});
                for (JsonNode courseNode : courseData) {
                    String courseTitle = courseNode.get("courseTitle").asText();
                    List<Quiz> quizzes = new ArrayList<>();

                    for (JsonNode assessmentNode : courseNode.get("assessments")) {
                        Quiz quiz = mapper.treeToValue(assessmentNode, Quiz.class);
                        quizzes.add(quiz);

                        // Map the instructorID to the courseTitle
                        instructorCourses.put(quiz.getInstructorID(), courseTitle);
                    }

                    // Add quizzes to the instructorQuizzes map
                    if (!quizzes.isEmpty()) {
                        int instructorID = quizzes.get(0).getInstructorID();
                        instructorQuizzes.put(instructorID, quizzes);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading quizzes from file: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------

    public void validateInstructorForCourse(int instructorID, String courseTitle) {
        String crs = instructorCourses.get(instructorID);
        System.out.println(crs);
        if (crs == null || !crs.equalsIgnoreCase(courseTitle)) {
            throw new IllegalArgumentException("The instructor is not associated with the course ...: " + courseTitle);
        }
    }

    // ------------------------------------------------------------------

    public void validateCourse(String courseTitle) {
        course crs = courseService.search_course(courseTitle);
        if (crs == null) {
            throw new IllegalArgumentException("Course not found.");
        }
    }

    // ------------------------------------------------------------------

    public void createQuiz(String courseTitle, Quiz quiz, int instructorID) {
        quiz.setInstructorID(instructorID);
        quiz.setCourseTitle(courseTitle);
        quiz.setType("quiz");

        validateInstructorForCourse(instructorID, courseTitle);
        validateCourse(courseTitle);

        course crs = courseService.search_course(courseTitle);

        if (crs.getQuestionBank().isEmpty()) {
            throw new IllegalArgumentException("No questions available in the course's question bank.");
        }

        // select Questions randooommm
        Collections.shuffle(crs.getQuestionBank());
        List<Question> selectedQuestions = crs.getQuestionBank().stream().limit(3).collect(Collectors.toList());
        quiz.setQuestions(selectedQuestions);


        instructorQuizzes.computeIfAbsent(instructorID, k -> new ArrayList<>()).add(quiz);
        instructorCourses.putIfAbsent(instructorID, courseTitle);
        quizWithId.putIfAbsent(quiz.getAssessmentID(), quiz);
        saveQuizzesToJsonFile();

    }

    // ------------------------------------------------------------------

    public List<Quiz> getQuizzesForInstructor(int instructorID, String courseTitle) {
        validateInstructorForCourse(instructorID, courseTitle);
        return instructorQuizzes.getOrDefault(instructorID, new ArrayList<>());
    }

    // ------------------------------------------------------------------

    public Quiz getQuizById(String courseTitle, String quizId, int instructorID) {
        validateInstructorForCourse(instructorID, courseTitle);
        List<Quiz> quizzes = instructorQuizzes.getOrDefault(instructorID, new ArrayList<>());

        return quizzes.stream()
                .filter(quiz -> quiz.getAssessmentID().equals(quizId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with ID: " + quizId));
    }

    // Instructors can create a questions bank per course.
    // Randomized question selection for each quiz attempt.
    // Performance Tracking Student Progress Tracking: ○ Instructors can track quiz scores
    // Adminsand Instructors can generate excel reports on student performance (including grades and attendance).

    // ------------------------------------------------------------------

    public void deleteQuiz(String courseTitle, String quizId, int instructorID) {
        validateInstructorForCourse(instructorID, courseTitle);
        List<Quiz> quizzes = instructorQuizzes.getOrDefault(instructorID, new ArrayList<>());

        quizzes.removeIf(quiz -> quiz.getAssessmentID().equals(quizId));
        if (quizzes.isEmpty()) {
            instructorQuizzes.remove(instructorID);
        }

        saveQuizzesToJsonFile();
    }

    // ------------------------------------------------------------------

    public void clearQuizzesForCourse(String courseTitle, int instructorID) {
        validateInstructorForCourse(instructorID, courseTitle);
        instructorQuizzes.remove(instructorID);
        saveQuizzesToJsonFile();
    }

    // ------------------------------------------------------------------

    private void saveQuizzesToJsonFile() {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> output = new ArrayList<>();

        for (Map.Entry<Integer, List<Quiz>> entry : instructorQuizzes.entrySet()) {
            int instructorID = entry.getKey();
            String courseTitle = instructorCourses.get(instructorID);
            List<Quiz> quizzes = entry.getValue();

            Map<String, Object> courseData = new HashMap<>();
            courseData.put("courseTitle", courseTitle);
            courseData.put("assessments", quizzes);

            output.add(courseData);
        }

        try {
            mapper.writeValue(new File(JSON_QUIZ_FILE), output);
        } catch (IOException e) {
            System.err.println("Error saving quizzes to file: " + e.getMessage());
        }
    }


    // ---------------------------------------------------------------------------------

    public String generateFeedback(int score, int totalQuestions) {
        if (score >= 85)
            return "Excellent work!" ;
        else if (score >= 70)
            return "Very Good!" ;
        else if (score >= 60)
            return "Good!" ;
        else if (score >= 50)
            return "You passed" ;
        else
            return "Oh, You not pass , Don't give up and try again!";
    }

    // ---------------------------------------------------------------------------------

    // for Student
    public Quiz getQuizById(String courseTitle, String quizId) {
        validateCourse(courseTitle);

        Quiz q = quizWithId.get(quizId);
        if (q == null)
            throw new IllegalArgumentException("Quiz not found with ID: " + quizId);

        return q;
    }

    // -------

    public Map<String , Object> attemptQuiz(String courseTitle , String quizId , int studentId , Map<Integer , String> studnetAnswers){

        Quiz quiz = getQuizById(courseTitle, quizId);

        List<Question> randomQuestions = quiz.getQuestions();
        Collections.shuffle(randomQuestions);

        int grades = 0 ;
        for (Question question : randomQuestions) {
            String correctAnswer = question.getCorrectAnswer();
            String answerOfStudnet = studnetAnswers.get(question.getId());

            if (answerOfStudnet != null && answerOfStudnet.equalsIgnoreCase(correctAnswer))
                grades++;

        }

        int totalQuestions = randomQuestions.size();
        int score = (grades * 100) / totalQuestions;

        quiz.getStudentScores().put(studentId, score);
        String feedback  = generateFeedback(score, totalQuestions);
        saveQuizzesToJsonFile();

        Map<String , Object> res = new HashMap<>();
        res.put("feedback", feedback);
        res.put("score", score);
        res.put("randomQuestions", randomQuestions);

        return res;
    }

    // -------

    public Map<Integer, Map<String , Integer>> getAllStudentGradesForCourse(String courseTitle , int instructorId) {
        validateInstructorForCourse(instructorId, courseTitle);

        List<Quiz> quizzes = instructorQuizzes.getOrDefault(instructorId, new ArrayList<>());

        if (quizzes.isEmpty())
            throw new IllegalArgumentException("Quiz not found with ID: " + instructorId);

        Map<Integer, Map<String , Integer>> studentGrades  = new HashMap<>();

        for (Quiz quiz : quizzes) {
            String quizId = quiz.getAssessmentID();

            for (Map.Entry<Integer, Integer> entry : quiz.getStudentScores().entrySet()) {
                int studentID = entry.getKey();
                int grade = entry.getValue();

                studentGrades.putIfAbsent(studentID, new HashMap<>());
                studentGrades.get(studentID).put(quizId, grade);
            }
        }
        return studentGrades;
    }

    // -------

}
