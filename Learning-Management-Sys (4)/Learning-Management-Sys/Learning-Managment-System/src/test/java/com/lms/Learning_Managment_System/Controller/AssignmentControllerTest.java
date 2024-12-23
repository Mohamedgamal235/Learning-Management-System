package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.Assignment;
import com.lms.Learning_Managment_System.Model.assignmentSubmission;
import com.lms.Learning_Managment_System.Model.enrolled_student;
import com.lms.Learning_Managment_System.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AssignmentControllerTest {

    @InjectMocks
    private AssignmentController assignmentController;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private UserController userController;

    private student_coursesService studentCoursesService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailService mailService;

    private Assignment testAssignment;
    private List<enrolled_student> enrolledStudents;
    private static final int INSTRUCTOR_ID = 2;
    private static final int STUDENT_ID = 1;
    private static final String COURSE_TITLE = "Java Course";
    private static final String ASSIGNMENT_ID = "A123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testAssignment = new Assignment(
                ASSIGNMENT_ID,
                "Test Assignment",
                COURSE_TITLE,
                "Test Description",
                "2024-12-31",
                "assignment"
        );

        enrolledStudents = new ArrayList<>();
        enrolled_student student = new enrolled_student();
        student.setEnrolled_student_id(STUDENT_ID);
        student.setEnrolled_student_email("test@test.com");
        enrolledStudents.add(student);

        Map<String, Integer> loggedInInstructors = new HashMap<>();
        loggedInInstructors.put("instructor@test.com", INSTRUCTOR_ID);
        Map<String, Integer> loggedInStudents = new HashMap<>();
        loggedInStudents.put("student@test.com", STUDENT_ID);

        when(userController.getLoggedInInstructors()).thenReturn(loggedInInstructors);
        when(userController.getLoggedInStudents()).thenReturn(loggedInStudents);
    }

    @Test
    void addAssignment_Success() {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(enrolledStudents);
            doNothing().when(assignmentService).addAssignment(eq(COURSE_TITLE), eq(testAssignment));
            ResponseEntity<?> response = assignmentController.addAssignment(
                    COURSE_TITLE,
                    testAssignment,
                    INSTRUCTOR_ID
            );
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Assignment added successfully", response.getBody());
            verify(assignmentService).addAssignment(eq(COURSE_TITLE), eq(testAssignment));
            verify(notificationService).add(anyString(), anyInt());
            verify(mailService).sendMail(anyString(), anyString(), anyString());
        }
    }
    @Test
    void addAssignment_UnauthorizedInstructor() {
        ResponseEntity<?> response = assignmentController.addAssignment(
                COURSE_TITLE,
                testAssignment,
                999 // Invalid instructor ID
        );
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access Denied: You must be a logged in instructor to add assignments", response.getBody());
        verify(assignmentService, never()).addAssignment(anyString(), any(Assignment.class));
    }
    @Test
    void addAssignment_ServiceThrowsException() {
        doThrow(new IllegalArgumentException("Course not found"))
                .when(assignmentService).addAssignment(anyString(), any(Assignment.class));
        ResponseEntity<?> response = assignmentController.addAssignment(
                "Java Course",
                testAssignment,
                INSTRUCTOR_ID
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void addAssignment_UnexpectedException() {
        doThrow(new RuntimeException("Unexpected error"))
                .when(assignmentService).addAssignment(anyString(), any(Assignment.class));
        ResponseEntity<?> response = assignmentController.addAssignment(
                "Java Course",
                testAssignment,
                INSTRUCTOR_ID
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void uploadAssignmentFile_Success() throws IOException {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(enrolledStudents);

            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.pdf",
                    "application/pdf",
                    "test content".getBytes()
            );
            ResponseEntity<?> response = assignmentController.uploadAssignmentFile(
                    COURSE_TITLE,
                    ASSIGNMENT_ID,
                    String.valueOf(STUDENT_ID),
                    file
            );
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("File uploaded successfully.", response.getBody());
            verify(assignmentService).saveFile(file, COURSE_TITLE, ASSIGNMENT_ID, String.valueOf(STUDENT_ID));
        }
    }
    @Test
    void uploadAssignmentFile_StudentNotEnrolled() throws IOException {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(new ArrayList<>());
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.pdf",
                    "application/pdf",
                    "test content".getBytes()
            );

            ResponseEntity<?> response = assignmentController.uploadAssignmentFile(
                    COURSE_TITLE,
                    ASSIGNMENT_ID,
                    String.valueOf(STUDENT_ID),
                    file
            );

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("Access Denied: The student is not enrolled in this course", response.getBody());
            verify(assignmentService, never()).saveFile(any(), anyString(), anyString(), anyString());
        }
    }
    @Test
    void uploadAssignmentFile_IOExceptionHandling() throws IOException {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(enrolledStudents);
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.pdf",
                    "application/pdf",
                    "test content".getBytes()
            );
            doThrow(new IOException("File processing error"))
                    .when(assignmentService).saveFile(any(), anyString(), anyString(), anyString());

            ResponseEntity<?> response = assignmentController.uploadAssignmentFile(
                    COURSE_TITLE,
                    ASSIGNMENT_ID,
                    String.valueOf(STUDENT_ID),
                    file
            );

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
            assertTrue(response.getBody().toString().contains("Error uploading file"));
        }
    }
    @Test
    void uploadAssignmentFile_UnauthorizedStudent() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
        ResponseEntity<?> response = assignmentController.uploadAssignmentFile(
                "Java Course",
                "A123",
                "999", // Invalid student ID
                file
        );
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(assignmentService, never()).saveFile(any(), anyString(), anyString(), anyString());
    }

    @Test
    void gradeAssignment_Success() {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(enrolledStudents);
            when(assignmentService.getassinmentnameBYID(anyString(), anyString()))
                    .thenReturn("Test Assignment");
            ResponseEntity<?> response = assignmentController.gradeAssignment(
                    "Java Course",
                    "A123",
                    "1",
                    INSTRUCTOR_ID,
                    "90",
                    "Good work!"
            );
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(assignmentService).gradeAssignment("Java Course", "A123", "1", "90", "Good work!");
            verify(notificationService).add(anyString(), eq(1));
            verify(emailService).sendMail(anyString(), anyString(), anyString());
        }
    }
    @Test
    void gradeAssignment_UnauthorizedInstructor() {
        ResponseEntity<?> response = assignmentController.gradeAssignment(
                COURSE_TITLE,
                ASSIGNMENT_ID,
                String.valueOf(STUDENT_ID),
                999, // Invalid instructor ID
                "90",
                "Good work!"
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access Denied: You must be a logged in instructor to grade assignments", response.getBody());
        verify(assignmentService, never()).gradeAssignment(anyString(), anyString(), anyString(), anyString(), anyString());
    }
    @Test
    void gradeAssignment_StudentNotEnrolled() {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(new ArrayList<>());

            ResponseEntity<?> response = assignmentController.gradeAssignment(
                    COURSE_TITLE,
                    ASSIGNMENT_ID,
                    String.valueOf(STUDENT_ID),
                    INSTRUCTOR_ID,
                    "90",
                    "Good work!"
            );

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("Access Denied: The student is not enrolled in this course", response.getBody());
        }
    }
    @Test
    void getFeedback_Success() {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(enrolledStudents);
            List<assignmentSubmission> submissions = new ArrayList<>();
            assignmentSubmission submission = new assignmentSubmission("1", "path/to/file");
            submission.setGrade("90");
            submission.setFeedback("Good work!");
            submissions.add(submission);
            when(assignmentService.getSubmissionsWithFeedback("Java Course", "A123"))
                    .thenReturn(submissions);
            ResponseEntity<?> response = assignmentController.getFeedback(
                    "Java Course",
                    "A123",
                    "1"
            );
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().toString().contains("Grade: 90"));
            assertTrue(response.getBody().toString().contains("Feedback: Good work!"));
        }
    }
    @Test
    void getFeedback_UnauthorizedStudent() {
        ResponseEntity<?> response = assignmentController.getFeedback(
                COURSE_TITLE,
                ASSIGNMENT_ID,
                "999" // Invalid student ID
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access Denied: You must be a logged in student to receive feedback", response.getBody());
    }

    @Test
    void getFeedback_NoSubmissionFound() {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(enrolledStudents);
            when(assignmentService.getSubmissionsWithFeedback(COURSE_TITLE, ASSIGNMENT_ID))
                    .thenReturn(new ArrayList<>()); // Empty list means no submission found

            ResponseEntity<?> response = assignmentController.getFeedback(
                    COURSE_TITLE,
                    ASSIGNMENT_ID,
                    String.valueOf(STUDENT_ID)
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Submission not found.", response.getBody());
        }
    }

    @Test
    void getAssignments_Success() {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(COURSE_TITLE))
                    .thenReturn(enrolledStudents);

            List<Assignment> assignments = new ArrayList<>();
            assignments.add(testAssignment);
            when(assignmentService.getAssignments(COURSE_TITLE)).thenReturn(assignments);

            ResponseEntity<?> response = assignmentController.getAssignments(
                    String.valueOf(STUDENT_ID),
                    COURSE_TITLE
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody() instanceof List);
            List<?> resultList = (List<?>) response.getBody();
            assertFalse(resultList.isEmpty());
            assertEquals(1, resultList.size());
        }
    }
    @Test
    void getAssignments_UnauthorizedStudent() {
        ResponseEntity<?> response = assignmentController.getAssignments(
                "999", // Invalid student ID
                COURSE_TITLE
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access Denied: You must be a logged in student to view assignments", response.getBody());
    }

    @Test
    void getAssignments_StudentNotEnrolled() {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(COURSE_TITLE))
                    .thenReturn(new ArrayList<>()); // Empty list means student not enrolled

            ResponseEntity<?> response = assignmentController.getAssignments(
                    String.valueOf(STUDENT_ID),
                    COURSE_TITLE
            );

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("Access Denied: You are not enrolled in this course", response.getBody());
        }
    }

    @Test
    void getAssignments_EmptyAssignmentsList() {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(enrolledStudents);
            when(assignmentService.getAssignments(COURSE_TITLE))
                    .thenReturn(new ArrayList<>()); // Empty assignments list

            ResponseEntity<?> response = assignmentController.getAssignments(
                    String.valueOf(STUDENT_ID),
                    COURSE_TITLE
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Test
    void getAssignments_CourseNotFound() {
        try (MockedStatic<student_coursesService> mockedStatic = mockStatic(student_coursesService.class)) {
            mockedStatic.when(() -> student_coursesService.getStudentsEnrolledInCourse(anyString()))
                    .thenReturn(enrolledStudents);
            when(assignmentService.getAssignments(COURSE_TITLE))
                    .thenThrow(new IllegalArgumentException("Course not found"));

            ResponseEntity<?> response = assignmentController.getAssignments(
                    String.valueOf(STUDENT_ID),
                    COURSE_TITLE
            );

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }
    @Test
    void trackSubmissions_Success() {
        List<assignmentSubmission> submissions = new ArrayList<>();
        submissions.add(new assignmentSubmission("1", "path/to/file"));
        when(assignmentService.trackSubmissionsProgress("Java Course", "A123"))
                .thenReturn(submissions);
        ResponseEntity<?> response = assignmentController.trackSubmissions(
                INSTRUCTOR_ID,
                "Java Course",
                "A123"
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    @Test
    void trackSubmissions_UnauthorizedInstructor() {
        ResponseEntity<?> response = assignmentController.trackSubmissions(
                999, // Invalid instructor ID
                COURSE_TITLE,
                ASSIGNMENT_ID
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access Denied: You must be a logged in instructor to track assignments submissions", response.getBody());
    }

    @Test
    void trackSubmissions_CourseNotFound() {
        when(assignmentService.trackSubmissionsProgress(COURSE_TITLE, ASSIGNMENT_ID))
                .thenThrow(new IllegalArgumentException("Course not found"));

        ResponseEntity<?> response = assignmentController.trackSubmissions(
                INSTRUCTOR_ID,
                COURSE_TITLE,
                ASSIGNMENT_ID
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
    @Test
    void getCourseAnalytics_Success() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalAssignments", 5);
        analytics.put("totalSubmissions", 10);
        when(assignmentService.getProgressAnalytics("Java Course"))
                .thenReturn(analytics);
        ResponseEntity<?> response = assignmentController.getCourseAnalytics(
                INSTRUCTOR_ID,
                "Java Course"
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
    }
    @Test
    void getCourseAnalytics_UnauthorizedInstructor() {
        ResponseEntity<?> response = assignmentController.getCourseAnalytics(
                999, // Invalid instructor ID
                COURSE_TITLE
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access Denied: You must be a logged in instructor to get Course Analytics", response.getBody());
    }

    @Test
    void getCourseAnalytics_CourseNotFound() {
        when(assignmentService.getProgressAnalytics(COURSE_TITLE))
                .thenThrow(new IllegalArgumentException("Course not found"));

        ResponseEntity<?> response = assignmentController.getCourseAnalytics(
                INSTRUCTOR_ID,
                COURSE_TITLE
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
}