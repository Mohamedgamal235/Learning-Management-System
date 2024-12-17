package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.enrolled_student;
import com.lms.Learning_Managment_System.Model.course;
import com.lms.Learning_Managment_System.Model.User;
import com.lms.Learning_Managment_System.Controller.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class student_coursesService {

    @Autowired
    private UserController userController;

    private static final String ENROLLED_STUDENTS_FILE = "enrolled_students.json";
    private ObjectMapper objectMapper = new ObjectMapper();

    public String enroll_in_Course(int student_id, String course_title) {
        if (!userController.getLoggedInStudents().containsValue(student_id)) {
            return "Access Denied: You must be logged in Student to enroll in a course.";
        }


        course crs = courseService.search_course(course_title);
        if (crs == null) {
            return "Course: " + course_title + " does not exist.";
        }
        enrolled_student enrolledStudent = findEnrolledStudentById(student_id);
        if (enrolledStudent != null) {
            if (!enrolledStudent.getEnrolled_courses().contains(crs)) {
                enrolledStudent.getEnrolled_courses().add(crs);
                saveEnrolledStudentsToFile(getAllEnrolledStudents());
                return "Student with ID: " + student_id + " successfully enrolled in course: " + course_title;
            } else {
                return "Student with ID: " + student_id + " is already enrolled in course: " + course_title;
            }
        }
        enrolled_student newEnrolledStudent = createNewEnrolledStudent(student_id);
        if (newEnrolledStudent != null) {
            newEnrolledStudent.getEnrolled_courses().add(crs);
            List<enrolled_student> allEnrolledStudents = getAllEnrolledStudents();
            allEnrolledStudents.add(newEnrolledStudent);
            saveEnrolledStudentsToFile(allEnrolledStudents);
            return "Student with ID: " + student_id + " successfully enrolled in course: " + course_title;
        }

        return "Enrollment Failed, Unable to find the student.";
    }


    private enrolled_student findEnrolledStudentById(int student_id) {
        try {
            File file = new File(ENROLLED_STUDENTS_FILE);
            if (file.exists()) {
                List<enrolled_student> enrolledStudents = new ArrayList<>(Arrays.asList(objectMapper.readValue(file, enrolled_student[].class)));
                return enrolledStudents.stream()
                        .filter(es -> es.getEnrolled_student_id() == student_id)
                        .findFirst()
                        .orElse(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private enrolled_student createNewEnrolledStudent(int student_id) {
        for (User user : userController.getAllUsers()) {
            if (user.getId() == student_id && "student".equalsIgnoreCase(user.getRole())) {
                enrolled_student newEnrolledStudent = new enrolled_student();
                newEnrolledStudent.setEnrolled_student_id(user.getId());
                newEnrolledStudent.setEnrolled_student_fname(user.getFirstName());
                newEnrolledStudent.setEnrolled_student_lname(user.getLastName());
                newEnrolledStudent.setEnrolled_student_email(user.getEmail());
                return newEnrolledStudent;
            }
        }
        return null;
    }
    private List<enrolled_student> getAllEnrolledStudents() {
        try {
            File file = new File(ENROLLED_STUDENTS_FILE);
            if (file.exists()) {
                return new ArrayList<>(Arrays.asList(objectMapper.readValue(file, enrolled_student[].class)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void saveEnrolledStudentsToFile(List<enrolled_student> enrolledStudents) {
        try {
            objectMapper.writeValue(new File(ENROLLED_STUDENTS_FILE), enrolledStudents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<enrolled_student> getStudentsEnrolledInCourse(String courseTitle) {
        List<enrolled_student> enrolledStudents = getAllEnrolledStudents();
        List<enrolled_student> result = new ArrayList<>();

        for (enrolled_student student : enrolledStudents) {
            for (course enrolledCourse : student.getEnrolled_courses()) {
                if (enrolledCourse.getCourse_title().equalsIgnoreCase(courseTitle)) {
                    result.add(student);
                    break;
                }
            }
        }

        return result;
    }
}