//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.lms.Learning_Managment_System.Controller.UserController;
//import com.lms.Learning_Managment_System.Model.course;
//import com.lms.Learning_Managment_System.Model.enrolled_student;
//import com.lms.Learning_Managment_System.Service.student_coursesService;
//import com.lms.Learning_Managment_System.Service.courseService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.*;
//
//public class course_EnrollmentTest {
//
//    @Mock
//    private UserController userController;
//
//    @Mock
//    private courseService courseService;
//
//    @Mock
//    private student_coursesService studentCoursesService;
//
//    private int validStudentId = 6;
//    private int invalidStudentId = 100;
//    private String courseTitle = "Test Course";
//    private course testCourse;
//    private enrolled_student Student;
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        testCourse = new course();
//        testCourse.setCourse_title(courseTitle);
//        Student = new enrolled_student();
//        Student.setEnrolled_student_id(validStudentId);
//        Student.setEnrolled_courses(new ArrayList<>(List.of(testCourse)));
//    }
//
//    @Test
//
//    public void EnrollInCourse_newStudentTest() {
//        when(userController.getLoggedInStudents()).thenReturn(Map.of("fatma", validStudentId));
//        when(studentCoursesService.findEnrolledStudentById(validStudentId)).thenReturn(null);
//        try (MockedStatic<courseService> mockedCourseService = mockStatic(courseService.class)) {
//            mockedCourseService.when(() -> courseService.search_course(courseTitle)).thenReturn(testCourse);
//            when(studentCoursesService.enroll_in_Course(validStudentId, courseTitle))
//                    .thenReturn("Student with ID: " + validStudentId + " successfully enrolled in course: " + courseTitle);
//            String result = studentCoursesService.enroll_in_Course(validStudentId, courseTitle);
//            assertEquals("Student with ID: 6 successfully enrolled in course: Test Course", result);
//        }
//
//    }
//    @Test
//    public void EnrollInCourse_AlreadyEnrolledTest() {
//        when(userController.getLoggedInStudents()).thenReturn(Map.of("student1", validStudentId));
//        try (MockedStatic<courseService> mockedCourseService = mockStatic(courseService.class)) {
//            mockedCourseService.when(() -> courseService.search_course(courseTitle)).thenReturn(testCourse);
//            when(studentCoursesService.findEnrolledStudentById(validStudentId)).thenReturn(Student);
//            when(studentCoursesService.enroll_in_Course(validStudentId, courseTitle))
//                    .thenReturn("Student with ID: " + validStudentId + " is already enrolled in course: " + courseTitle);
//            String result = studentCoursesService.enroll_in_Course(validStudentId, courseTitle);
//            assertEquals("Student with ID: 6 is already enrolled in course: Test Course", result);
//        }
//
//    }
//    @Test
//    public void EnrollInCourse_StudentNotLoggedInTest() {
//        when(userController.getLoggedInStudents()).thenReturn(Map.of("student2", invalidStudentId));
//        when(studentCoursesService.enroll_in_Course(validStudentId, courseTitle))
//                .thenReturn(  "Access Denied: You must be logged in Student to enroll in a course.");
//        String result = studentCoursesService.enroll_in_Course(validStudentId, courseTitle);
//        assertEquals("Access Denied: You must be logged in Student to enroll in a course.", result);
//    }
//    @Test
//    public void EnrollInCourse_CourseDoesNotExistTest() {
//        when(userController.getLoggedInStudents()).thenReturn(Map.of("student1", validStudentId));
//        try (MockedStatic<courseService> mockedCourseService = mockStatic(courseService.class)) {
//            mockedCourseService.when(() -> courseService.search_course(courseTitle)).thenReturn(null);
//            when(studentCoursesService.enroll_in_Course(validStudentId, courseTitle))
//                    .thenReturn("Course: " + courseTitle + " does not exist.");
//            String result = studentCoursesService.enroll_in_Course(validStudentId, courseTitle);
//            assertEquals("Course: Test Course does not exist.", result);
//        }
//    }
//
//}