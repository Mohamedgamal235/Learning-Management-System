package com.lms.Learning_Managment_System.Controller;
import com.lms.Learning_Managment_System.Model.course;
import com.lms.Learning_Managment_System.Model.enrolled_student;
import com.lms.Learning_Managment_System.Model.lesson;
import com.lms.Learning_Managment_System.Service.courseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.spi.ToolProvider.findFirst;

@RestController
@RequestMapping("/manage_courses")
public class Course_Controller {
    @Autowired
    private courseService service;
    @Autowired
    private com.lms.Learning_Managment_System.Service.student_coursesService student_coursesService;

    @PostMapping("/add_course")
    public String addCourse(@RequestBody course newCourse) {
        course crs = service.search_course(newCourse.getCourse_title());
        if (crs != null) {
            return "Course title : " + crs.getCourse_title() + " already exists";
        } else {
            service.addCourse(newCourse);
            return "Course added successfully." + (newCourse.getCourse_lessons() != null ? newCourse.getCourse_lessons().size() : 0) + " lessons.";
        }
    }

    @GetMapping("/view_All_courses")
    public ResponseEntity<List<course>> viewAllCourses() {
        List<course> courses = service.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/add_lesson")
    public ResponseEntity<String> addLesson(@RequestBody lesson newLesson, @RequestParam String course_title) {
        service.addLessonToCourse(course_title, newLesson);
        return ResponseEntity.ok("Lesson added to course: " + course_title);
    }

    @GetMapping("/get_lessons_ofCourse/{course_title}")
    public ResponseEntity<List<lesson>> getLessonsOfCourse(@PathVariable("course_title") String courseTitle) {
        List<lesson> lessons = service.getAllLessonsOfCourse(courseTitle);
        return ResponseEntity.ok(lessons);
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload/{courseTitle}")
    public ResponseEntity<String> uploadFileToLesson(@RequestParam("files") List<MultipartFile> files,
                                                     @RequestParam("lessonTitle") String lessonTitle,
                                                     @PathVariable String courseTitle) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("No files selected");
        }

        course course = service.search_course(courseTitle);
        if (course == null) {
            return ResponseEntity.badRequest().body("Course with title " + courseTitle + " not found.");
        }
        lesson selectedLesson = course.getCourse_lessons().stream()
                .filter(lesson -> lesson.getLessonTitle().equalsIgnoreCase(lessonTitle))
                .findFirst()
                .orElse(null);

        if (selectedLesson == null) {
            return ResponseEntity.badRequest().body("Lesson with title " + lessonTitle + " not found in course " + courseTitle);
        }

        try {
            File directory = new File(uploadDir + "/" + courseTitle + "/" + lessonTitle);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    Path filePath = Paths.get(directory.getPath(), file.getOriginalFilename());
                    Files.write(filePath, file.getBytes());
                    selectedLesson.getLesson_files().add(file.getOriginalFilename());
                }
            }
            service.saveCoursesToFile();
            return ResponseEntity.ok("Files uploaded successfully to lesson: " + lessonTitle);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("File upload failed");
        }
    }

    @GetMapping("/view_enrolled_students/{course_title}")
    public ResponseEntity<?> viewAllEnrolledStudentsInCourse(@PathVariable String course_title) {
        List<enrolled_student> enrolledStudentswithcourses = student_coursesService.getStudentsEnrolledInCourse(course_title);

        List<Map<String, Object>> enrolledStudents = new ArrayList<>();

        for (enrolled_student student : enrolledStudentswithcourses) {
            for (course enrolledCourse : student.getEnrolled_courses()) {
                if (enrolledCourse.getCourse_title().equalsIgnoreCase(course_title)) {
                    Map<String, Object> studentInfo = new HashMap<>();
                    studentInfo.put("enrolled_student_id", student.getEnrolled_student_id());
                    studentInfo.put("enrolled_student_name", student.getEnrolled_student_name());

                    enrolledStudents.add(studentInfo);
                    break;
                }
            }
        }

        if (enrolledStudents.isEmpty()) {
            return ResponseEntity.ok("No students are enrolled in the course: " + course_title);
        }

        return ResponseEntity.ok(enrolledStudents);
    }
}