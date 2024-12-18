package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.Question;
import com.lms.Learning_Managment_System.Model.course;
import com.lms.Learning_Managment_System.Model.lesson;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class courseService {
    private static final String COURSES_FILE = "courses_with_lessons.json";
    private static List<course> courses = new ArrayList<>();

    public courseService() {
        loadCoursesFromFile();
    }

    public void addCourse(course newCourse) {
        courses.add(newCourse);
        saveCoursesToFile();
    }

    public static List<course> getAllCourses() {
        return courses;
    }
    public void addLessonToCourse(String courseTitle, lesson newLesson) {
        course course = search_course(courseTitle);
        if (course != null) {
            if (course.getCourse_lessons() == null) {
                course.setCourse_lessons(new ArrayList<>());
            }
            course.getCourse_lessons().add(newLesson);
            saveCoursesToFile();
        } else {
            throw new IllegalArgumentException("Course with title " + courseTitle + " not found.");
        }
    }
    public List<lesson> getAllLessonsOfCourse(String courseTitle) {
        course course = search_course(courseTitle);
        if (course != null) {
            return course.getCourse_lessons();
        } else {
            throw new IllegalArgumentException("Course with title " + courseTitle + " not found.");
        }
    }


    private void loadCoursesFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File(COURSES_FILE);
            if (file.exists()) {
                courses = objectMapper.readValue(file,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, course.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveCoursesToFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(COURSES_FILE), courses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static course search_course(String title) {
        for (course course : courses) {
            if (course.getCourse_title().equalsIgnoreCase(title)) {
                return course;
            }
        }
        return null;
    }

    public void addQuestionsToBank(String courseTitle, List<Question> questions) {
        course course = search_course(courseTitle);
        if (course == null) {
            throw new IllegalArgumentException("Course not found.");
        }
        course.getQuestionBank().addAll(questions);
        saveCoursesToFile();
    }



    public List<Question> getQuestionBank(String courseTitle){
        course crs = search_course(courseTitle);
        if (crs != null) {
            return crs.getQuestionBank();
        }
        throw new IllegalArgumentException("Course not found: " + courseTitle);
    }

    

}
