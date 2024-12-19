package com.lms.Learning_Managment_System.Controller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lms.Learning_Managment_System.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.Learning_Managment_System.Model.Question;
import com.lms.Learning_Managment_System.Model.course;
import com.lms.Learning_Managment_System.Model.enrolled_student;
import com.lms.Learning_Managment_System.Model.lesson;
import com.lms.Learning_Managment_System.Service.courseService;


@RestController
@RequestMapping("/manage_courses")
public class Course_Controller {

    @Autowired
    private courseService service;
    @Autowired
    private com.lms.Learning_Managment_System.Service.student_coursesService student_coursesService;
    @Autowired
    private UserController userController;
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/{user_id}/add_course")
    public ResponseEntity<String> addCourse(@RequestBody course newCourse, @PathVariable int user_id) {
        boolean isInstructor = userController.getLoggedInInstructors().containsValue(user_id);
        boolean isAdmin = userController.getLoggedInAdmins().containsValue(user_id);
        if (!isInstructor && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You must be a logged-in instructor or admin to add a new course.");
        }
        course crs = service.search_course(newCourse.getCourse_title());
        if (crs != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Course title: " + crs.getCourse_title() + " already exists");
        }
        if (isInstructor) {
            newCourse.setInstructor_id(user_id);
        }
        service.addCourse(newCourse);
        String message = "Dear Students, we are pleased to announce that a new course, ["+ newCourse.getCourse_title() +"] has been added to the platform. Enroll now to enhance your learning journey and expand your knowledge";
        for (Map.Entry<String, Integer> entry : userController.getLoggedInStudents().entrySet()) {
            Integer Userid = entry.getValue();
            notificationService.add(message,Userid);
        }
        return ResponseEntity.ok("Course added successfully. " + (newCourse.getCourse_lessons() != null ? newCourse.getCourse_lessons().size() : 0) + " lessons.");
        }


    @GetMapping("/{instructor_id}/view_All_courses")
    public ResponseEntity<?> viewAllCourses(@PathVariable int instructor_id) {
        if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in instructor to manage courses");
        }
        List<course> courses = service.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/{instructor_id}/add_lesson")
    public ResponseEntity<String> addLesson(@RequestBody lesson newLesson, @RequestParam String course_title, @PathVariable int instructor_id) {
        if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in instructor to manage lessons");
        }
        service.addLessonToCourse(course_title, newLesson);
        String message = "Dear Students, Kindly be informed that a new lesson, "+ newLesson.getLessonTitle() +" has been added to the course: "+ course_title;
        List<enrolled_student> enrolledStudents = student_coursesService.getStudentsEnrolledInCourse(course_title);
        for (enrolled_student student : enrolledStudents) {
            Integer Userid = student.getEnrolled_student_id();
            notificationService.add(message,Userid);
        }
        return ResponseEntity.ok("Lesson added to course: " + course_title);
    }

    @GetMapping("/{instructor_id}/get_lessons_ofCourse/{course_title}")
    public ResponseEntity<List<lesson>> getLessonsOfCourse(@PathVariable("course_title") String courseTitle, @PathVariable int instructor_id) {
        if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<lesson> lessons = service.getAllLessonsOfCourse(courseTitle);
        return ResponseEntity.ok(lessons);
    }


    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/{instructor_id}/upload/{courseTitle}")
    public ResponseEntity<String> uploadFileToLesson(@RequestParam("files") List<MultipartFile> files,
                                                     @RequestParam("lessonTitle") String lessonTitle,
                                                     @PathVariable String courseTitle,
                                                     @PathVariable int instructor_id) {
        if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in instructor to upload files");
        }
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
            String message = "Dear Students, kindly be informed that new resources have been uploaded to the lesson,"+ lessonTitle +" in the course: "+ courseTitle + " These materials are now available for your reference and study";
            List<enrolled_student> enrolledStudents = student_coursesService.getStudentsEnrolledInCourse(courseTitle);
            for (enrolled_student student : enrolledStudents) {
                Integer Userid = student.getEnrolled_student_id();
                notificationService.add(message,Userid);
            }
            return ResponseEntity.ok("Files uploaded successfully to lesson: " + lessonTitle);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("File upload failed");
        }
    }

    @GetMapping("/{instructor_id}/view_enrolled_students/{course_title}")
    public ResponseEntity<?> viewAllEnrolledStudentsInCourse(@PathVariable String course_title,@PathVariable int instructor_id) {
        if (!userController.getLoggedInInstructors().containsValue(instructor_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in instructor to view enrolled students");
        }
        List<enrolled_student> enrolledStudentswithcourses = student_coursesService.getStudentsEnrolledInCourse(course_title);

        List<Map<String, Object>> enrolledStudents = new ArrayList<>();

        for (enrolled_student student : enrolledStudentswithcourses) {
            for (course enrolledCourse : student.getEnrolled_courses()) {
                if (enrolledCourse.getCourse_title().equalsIgnoreCase(course_title)) {
                    Map<String, Object> studentInfo = new HashMap<>();
                    studentInfo.put("enrolled_student_id", student.getEnrolled_student_id());
                    studentInfo.put("enrolled_student_fname", student.getEnrolled_student_fname());
                    studentInfo.put("enrolled_student_lname", student.getEnrolled_student_lname());
                    studentInfo.put("enrolled_student_email", student.getEnrolled_student_email());

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

    @PostMapping("/{courseTitle}/addQuestions")
    public ResponseEntity<?> addQuestions(@PathVariable("courseTitle") String course_title,
                                          @RequestBody List<Question> questions) {
        try {
            service.addQuestionsToBank(course_title, questions);
            String message = "Dear Students, kindly be informed that additional questions have been added to the course: "+ course_title + " Please review the newly added content";
            List<enrolled_student> enrolledStudents = student_coursesService.getStudentsEnrolledInCourse(course_title);
            for (enrolled_student student : enrolledStudents) {
                Integer Userid = student.getEnrolled_student_id();
                notificationService.add(message,Userid);
            }
            return new ResponseEntity<>(questions, HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error on server while adding Questions ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{courseTitle}/questionBank")
    public ResponseEntity<List<Question>> getQuestionBank(@PathVariable String courseTitle) {
        List<Question> questions = new ArrayList<>();
        try {
            questions = service.getQuestionBank(courseTitle);
            return new ResponseEntity<>(questions , HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body(questions);
    }


}