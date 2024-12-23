import com.lms.Learning_Managment_System.Controller.Course_Controller;
import com.lms.Learning_Managment_System.Service.student_coursesService;
import com.lms.Learning_Managment_System.Service.NotificationService;
import com.lms.Learning_Managment_System.Controller.UserController;
import com.lms.Learning_Managment_System.Model.course;
import com.lms.Learning_Managment_System.Model.enrolled_student;
import com.lms.Learning_Managment_System.Model.lesson;
import com.lms.Learning_Managment_System.Service.courseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lms.Learning_Managment_System.Service.QuizService;
import com.lms.Learning_Managment_System.Service.EmailService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class course_Test {

    @Mock
    private UserController userController;
    @Mock
    private courseService service;
    @Mock
    private NotificationService NotificationService;

    @Mock
    private MultipartFile mockFile;
    @Mock
    private EmailService emailService;

    @Mock
    private List<course> mockCourses;

    @Mock
    private QuizService quizService;
    @InjectMocks
    private Course_Controller controller;

    private course testCourse;
    private course test_crs;
    private lesson testLesson;

    int valid_user_id = 22;
    private int invalid_user_Id = 50;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testCourse = new course();
        test_crs = new course();
        testCourse.setCourse_title("Test Course");
        testLesson = new lesson();
        testLesson.setLessonTitle("Test Lesson");
        testLesson.setLesson_files(new ArrayList<>());
        testCourse.setInstructor_id(2);
        testCourse.setCourse_lessons(new ArrayList<>(List.of(testLesson)));
        mockCourses = new ArrayList<>();
        mockCourses.add(testCourse);
    }

    @Test
    public void uploadFileToLesson_Success() throws Exception {
        int instructorId = 2;
        when(userController.getLoggedInInstructors()).thenReturn(Map.of("sara", instructorId));
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course("Test Course")).thenReturn(testCourse);
            try (MockedStatic<student_coursesService> mockedStaticStudent = mockStatic(student_coursesService.class)) {
                List<enrolled_student> mockStudents = List.of(new enrolled_student(), new enrolled_student());
                mockedStaticStudent.when(() -> student_coursesService.getStudentsEnrolledInCourse("Test Course")).thenReturn(mockStudents);
                doNothing().when(NotificationService).add(anyString(), anyInt());
                doNothing().when(emailService).sendMail(anyString(), anyString(), anyString());

                String fileName = "testFile.txt";
                byte[] fileContent = "Test content".getBytes();
                when(mockFile.getOriginalFilename()).thenReturn(fileName);
                when(mockFile.getBytes()).thenReturn(fileContent);
                when(mockFile.isEmpty()).thenReturn(false);
                ResponseEntity<String> response = controller.uploadFileToLesson(
                        List.of(mockFile),
                        "Test Lesson",
                        "Test Course",
                        instructorId
                );
//                System.out.println("Response Status: " + response.getStatusCode());
//                System.out.println("Response Body: " + response.getBody());
//                System.out.println("Lesson Files: " + testLesson.getLesson_files());
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertTrue(testLesson.getLesson_files().contains(fileName));
                assertEquals("Files uploaded successfully to lesson: Test Lesson", response.getBody());
            }
        }
    }

    @Test
    public void uploadFileToLesson_CourseNotFound() {
        String instructorId = "2";
        when(userController.getLoggedInInstructors()).thenReturn(Map.of("JohnDoe", Integer.parseInt(instructorId)));
        when(service.getAllCourses()).thenReturn(mockCourses);
        ResponseEntity<String> response = controller.uploadFileToLesson(
                List.of(mockFile),
                "Test Lesson",
                "Nonexistent Course",
                Integer.parseInt(instructorId)
        );
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Course with title Nonexistent Course not found.", response.getBody());
    }

    @Test
    public void uploadFileToLesson_EmptyFile() throws Exception {
        int instructorId = 2;
        when(userController.getLoggedInInstructors()).thenReturn(Map.of("sara", instructorId));
        try (MockedStatic<courseService> mockedStaticCourse = mockStatic(courseService.class)) {
            mockedStaticCourse.when(() -> courseService.search_course("Test Course")).thenReturn(testCourse);
            try (MockedStatic<student_coursesService> mockedStaticStudent = mockStatic(student_coursesService.class)) {
                List<enrolled_student> mockStudents = List.of(new enrolled_student(), new enrolled_student());
                mockedStaticStudent.when(() -> student_coursesService.getStudentsEnrolledInCourse("Test Course")).thenReturn(mockStudents);
                doNothing().when(NotificationService).add(anyString(), anyInt());

                when(mockFile.getOriginalFilename()).thenReturn("emptyFile.txt");
                when(mockFile.getBytes()).thenReturn(new byte[0]);  //empty array
                when(mockFile.isEmpty()).thenReturn(true);
                ResponseEntity<String> response = controller.uploadFileToLesson(
                        List.of(mockFile),
                        "Test Lesson",
                        "Test Course",
                        instructorId
                );

                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("No files selected", response.getBody());
                verify(NotificationService, times(0)).add(anyString(), anyInt());
            }
        }
    }

    @Test
    public void Add_existing_crsTest() {
        when(userController.getLoggedInInstructors()).thenReturn(Map.of("sara", valid_user_id));
        try (MockedStatic<courseService> mockedCourseService = mockStatic(courseService.class)) {
            mockedCourseService.when(() -> courseService.search_course(testCourse.getCourse_title())).thenReturn(testCourse);
            ResponseEntity<String> response = controller.addCourse(testCourse, valid_user_id);
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Course title: " + testCourse.getCourse_title() + " already exists", response.getBody());
        }
    }

    @Test
    public void Add_new_courseTest() {
        when(userController.getLoggedInInstructors()).thenReturn(Map.of("sara", valid_user_id));
        try (MockedStatic<courseService> mockedCourseService = mockStatic(courseService.class)) {
            mockedCourseService.when(() -> courseService.search_course(testCourse.getCourse_title())).thenReturn(null);
            doNothing().when(quizService).setIdOfInstructorCourses(valid_user_id, testCourse.getCourse_title());
            ResponseEntity<String> response = controller.addCourse(testCourse, valid_user_id);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Course added successfully. 1 lessons.", response.getBody());

        }


    }
    @Test
    public void AddLessonToCourseTest() {
        try (MockedStatic<courseService> mockedCourseService = mockStatic(courseService.class)) {
            mockedCourseService.when(() -> courseService.search_course(testCourse.getCourse_title())).thenReturn(testCourse);
            test_crs.setCourse_title("Test Course");
            test_crs.setCourse_lessons(new ArrayList<>());
            lesson test_lesson = new lesson();
            test_lesson.setLessonTitle("Test Lesson");
            service.addCourse(test_crs);
            service.addLessonToCourse("Test Course", test_lesson);
            course returned_crs = service.search_course("Test Course");
            assertNotNull(returned_crs);
            assertEquals("Test Lesson", returned_crs.getCourse_lessons().get(0).getLessonTitle());
        }
    }
    @Test
    public void AddCourseUserNotLoggedInTest() {
        when(userController.getLoggedInInstructors()).thenReturn(Map.of("Instructor2", invalid_user_Id));
        course newCourse = new course();
        newCourse.setCourse_title("Sample Course");
        ResponseEntity<String> response = controller.addCourse(newCourse, valid_user_id);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access Denied: You must be a logged-in instructor or admin to add a new course.", response.getBody());
    }

}