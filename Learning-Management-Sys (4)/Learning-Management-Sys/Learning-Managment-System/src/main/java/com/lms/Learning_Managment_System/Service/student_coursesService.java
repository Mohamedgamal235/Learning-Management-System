package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.course;
import com.lms.Learning_Managment_System.Model.enrolled_student;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class student_coursesService {

    private static final String ENROLLED_STUDENTS_FILE = "enrolled_students.json";
    private List<enrolled_student> students = new ArrayList<>();
    private List<course> courses = new ArrayList<>();

    public student_coursesService() {
        loadStudentsFromFile();
    }

    public String enroll_in_Course(int en_studID, String course_title) {
        students.add(new enrolled_student(1,"fatma",new ArrayList<>()));
        enrolled_student stud = students.stream()
                .filter(s -> s.getEnrolled_student_id() == en_studID)
                .findFirst()
                .orElse(null);

        course crs = courseService.search_course(course_title);


        if (stud == null) {
            return "Student with ID: " + en_studID + " does not exist.";
        }
        if (crs == null) {
            return "Course: " + course_title + " does not exist.";
        }
        if (stud.getEnrolled_courses().contains(crs)) {
            return "Student with ID: " + en_studID + " is already enrolled in course: " + course_title;
        }

        stud.getEnrolled_courses().add(crs);
        saveStudentsToFile();

        return "Student with ID: " + en_studID + " enrolled successfully in course: " + course_title;
    }

    public List<enrolled_student> getStudentsEnrolledInCourse(String courseTitle) {
        List<enrolled_student> enrolledStudents = new ArrayList<>();
        for (enrolled_student student : students) {
            for (course enrolledCourse : student.getEnrolled_courses()) {
                if (enrolledCourse.getCourse_title().equalsIgnoreCase(courseTitle)) {
                    enrolledStudents.add(student);
                    break;
                }
            }
        }

        return enrolledStudents;
    }



    private void saveStudentsToFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(ENROLLED_STUDENTS_FILE), students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStudentsFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File(ENROLLED_STUDENTS_FILE);
            if (file.exists()) {
                students = objectMapper.readValue(file,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, enrolled_student.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
