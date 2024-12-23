package com.lms.Learning_Managment_System.Service;
import com.lms.Learning_Managment_System.Model.Assignment;
import com.lms.Learning_Managment_System.Model.assignmentSubmission;
import com.lms.Learning_Managment_System.Model.course;
import com.lms.Learning_Managment_System.Model.enrolled_student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AssignmentServiceTest {
    @InjectMocks
    private AssignmentService assignmentService;
    private List<enrolled_student> enrolledStudents;
    private static final String COURSE_TITLE = "software";
    private static final String ASSIGNMENT_ID = "A1";
    private static final String STUDENT_ID = "1";
    private static final String STUDENT_ID2 = "3";
    private static final String GRADE = "100";
    private static final String FEEDBACK = "good job";

    private course test_crs;

    @BeforeEach
    public void setUp() {
        enrolledStudents = new ArrayList<>();
        enrolled_student student = new enrolled_student();
        student.setEnrolled_student_id(Integer.parseInt(STUDENT_ID));
        student.setEnrolled_student_email("test@test.com");
        enrolledStudents.add(student);
        student.setEnrolled_student_id(Integer.parseInt(STUDENT_ID2));
        student.setEnrolled_student_email("test2@test.com");
        enrolledStudents.add(student);
        assignmentService.getAssignments(COURSE_TITLE).clear();
        test_crs = new course();
        test_crs.setCourse_title(COURSE_TITLE);
    }
    @Test
    public void testValidateCourse() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            assertDoesNotThrow(() -> assignmentService.validateCourse(COURSE_TITLE),
                    "Should not throw exception for valid course");


            mockedStatic.when(() -> courseService.search_course("nonexistent"))
                    .thenReturn(null);

            String invalidCourse = "nonexistent";
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> assignmentService.validateCourse(invalidCourse),
                    "Should throw IllegalArgumentException for invalid course");
            assertEquals("Course not found.", exception.getMessage());
        }
    }

    @Test
    public void testAddAssignment() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment for Software Engineering", "2024-12-15", "assignment");

            assignmentService.addAssignment(COURSE_TITLE, assignment);
            List<Assignment> assignments = assignmentService.getAssignments(COURSE_TITLE);
            assertTrue(assignments.contains(assignment), "Assignment should be added to the course.");

            mockedStatic.when(() -> courseService.search_course("nonexistent"))
                    .thenReturn(null);
            String invalidCourse = "nonexistent";
            assertThrows(IllegalArgumentException.class,
                    () -> assignmentService.addAssignment(invalidCourse, assignment),
                    "Should throw IllegalArgumentException for invalid course");
        }
    }
    @Test
    public void testGetAssignmentById() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment for Software Engineering", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            Assignment retrieved = assignmentService.getAssignmentById(COURSE_TITLE, ASSIGNMENT_ID);
            assertNotNull(retrieved, "Should return assignment for valid ID");
            assertEquals(ASSIGNMENT_ID, retrieved.getAssessmentID());

            String invalidId = "nonexistent";
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> assignmentService.getAssignmentById(COURSE_TITLE, invalidId),
                    "Should throw IllegalArgumentException for invalid assignment ID");
            assertTrue(exception.getMessage().contains("Assignment with ID " + invalidId + " not found."));
        }
    }

    @Test
    public void testSaveFile() throws IOException {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment for Software Engineering", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            MultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain", "Sample content".getBytes());
            assignmentService.saveFile(file, COURSE_TITLE, ASSIGNMENT_ID, STUDENT_ID);

            File savedFile = new File("C:\\Storage" + File.separator + "testfile.txt");
            assertTrue(savedFile.exists(), "File should be saved in the storage directory.");

            Assignment savedAssignment = assignmentService.getAssignments(COURSE_TITLE).get(0);
            assertFalse(savedAssignment.getSubmissions().isEmpty(), "Assignment should have a submission");
            assertEquals(STUDENT_ID, savedAssignment.getSubmissions().get(0).getStudentId(),
                    "Submission should be linked to correct student");

            savedFile.delete();
        }
    }
    @Test
    public void testSaveFile_WithNullFile() {
        assertThrows(NullPointerException.class,
                () -> assignmentService.saveFile(null, COURSE_TITLE, ASSIGNMENT_ID, STUDENT_ID),
                "Should throw NullPointerException when file is null");
    }

    @Test
    public void testSaveFile_WithMaliciousFileName() throws IOException {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            MultipartFile maliciousFile = new MockMultipartFile(
                    "file",
                    "../../../malicious.txt",
                    "text/plain",
                    "content".getBytes()
            );

            assertThrows(SecurityException.class,
                    () -> assignmentService.saveFile(maliciousFile, COURSE_TITLE, ASSIGNMENT_ID, STUDENT_ID),
                    "Should reject files with potentially malicious paths");
        }
    }
    @Test
    public void testGradeAssignment() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment for Software Engineering", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            assignmentSubmission submission = new assignmentSubmission(STUDENT_ID, "filePath");
            assignment = assignmentService.getAssignmentById(COURSE_TITLE, ASSIGNMENT_ID);
            assignment.addSubmission(submission);

            assignmentService.gradeAssignment(COURSE_TITLE, ASSIGNMENT_ID, STUDENT_ID, GRADE, FEEDBACK);

            List<Assignment> assignments = assignmentService.getAssignments(COURSE_TITLE);
            Assignment gradedAssignment = assignments.stream()
                    .filter(a -> a.getAssessmentID().equals(ASSIGNMENT_ID))
                    .findFirst()
                    .orElseThrow();

            Optional<assignmentSubmission> gradedSubmission = gradedAssignment.getSubmissions().stream()
                    .filter(s -> s.getStudentId().equals(STUDENT_ID))
                    .findFirst();

            assertTrue(gradedSubmission.isPresent());
            assertEquals(GRADE, gradedSubmission.get().getGrade());
            assertEquals(FEEDBACK, gradedSubmission.get().getFeedback());

            // Test invalid course
            mockedStatic.when(() -> courseService.search_course("nonexistent"))
                    .thenReturn(null);
            String invalidCourse = "nonexistent";
            assertThrows(IllegalArgumentException.class,
                    () -> assignmentService.gradeAssignment(invalidCourse, ASSIGNMENT_ID, STUDENT_ID, GRADE, FEEDBACK),
                    "Should throw IllegalArgumentException for invalid course");
        }
    }

    @Test
    public void testGradeAssignment_WithoutSubmission() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            assertThrows(IllegalArgumentException.class,
                    () -> assignmentService.gradeAssignment(COURSE_TITLE, ASSIGNMENT_ID, STUDENT_ID, GRADE, FEEDBACK),
                    "Should throw exception when trying to grade non-existent submission");
        }
    }

    @Test
    public void testGetSubmissionsWithFeedback() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment for Software Engineering", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            assignmentSubmission submission = new assignmentSubmission(STUDENT_ID, "filePath");
            assignment.addSubmission(submission);
            assignmentService.gradeAssignment(COURSE_TITLE, ASSIGNMENT_ID, STUDENT_ID, GRADE, FEEDBACK);

            List<assignmentSubmission> submissionsWithFeedback = assignmentService.getSubmissionsWithFeedback(COURSE_TITLE, ASSIGNMENT_ID);
            assertFalse(submissionsWithFeedback.isEmpty(), "Submissions should not be empty.");

            assignmentSubmission gradedSubmission = submissionsWithFeedback.stream()
                    .filter(s -> s.getStudentId().equals(STUDENT_ID))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

            assertEquals(GRADE, gradedSubmission.getGrade(), "Grade should be assigned.");
            assertEquals(FEEDBACK, gradedSubmission.getFeedback(), "Feedback should be assigned.");
        }
    }

    @Test
    public void testTrackSubmissionsProgress() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment for Software Engineering", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            assignmentSubmission submission = new assignmentSubmission(STUDENT_ID, "filePath");
            assignment.addSubmission(submission);

            List<assignmentSubmission> submissions = assignmentService.trackSubmissionsProgress(COURSE_TITLE, ASSIGNMENT_ID);
            assertNotNull(submissions, "Submissions list should not be null");
            assertFalse(submissions.isEmpty(), "Submissions list should not be empty");
            assertEquals(1, submissions.size(), "Should have one submission");
            assertEquals(STUDENT_ID, submissions.get(0).getStudentId());

            // Test invalid course
            mockedStatic.when(() -> courseService.search_course("nonexistent"))
                    .thenReturn(null);
            String invalidCourse = "nonexistent";
            assertThrows(IllegalArgumentException.class,
                    () -> assignmentService.trackSubmissionsProgress(invalidCourse, ASSIGNMENT_ID),
                    "Should throw IllegalArgumentException for invalid course");
        }
    }

    @Test
    public void testGetProgressAnalytics() {
        try (MockedStatic<courseService> mockedCourseSvc = mockStatic(courseService.class);
             MockedStatic<student_coursesService> mockedStudentSvc = mockStatic(student_coursesService.class)) {

            mockedCourseSvc.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);
            mockedStudentSvc.when(() -> student_coursesService.getStudentsEnrolledInCourse(COURSE_TITLE))
                    .thenReturn(enrolledStudents
                    );

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment for Software Engineering", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            assignmentSubmission submission = new assignmentSubmission(STUDENT_ID, "filePath");
            assignment.addSubmission(submission);
            assignmentService.gradeAssignment(COURSE_TITLE, ASSIGNMENT_ID, STUDENT_ID, GRADE, FEEDBACK);

            Map<String, Object> progressAnalytics = assignmentService.getProgressAnalytics(COURSE_TITLE);

            assertNotNull(progressAnalytics, "Progress analytics should not be null.");
            assertEquals(1, progressAnalytics.get("totalAssignments"), "There should be 1 assignment.");
            assertEquals(2, progressAnalytics.get("totalStudents"), "There should be 2 enrolled students.");
            assertEquals(2, progressAnalytics.get("expectedTotalSubmissions"), "Expected submissions should be totalAssignments * totalStudents.");
            assertEquals(1, progressAnalytics.get("actualSubmissions"), "There should be 1 actual submission.");
            assertEquals(1, progressAnalytics.get("gradedSubmissions"), "There should be 1 graded submission.");
            assertEquals(100.0, progressAnalytics.get("averageGrade"), "Average grade should be 100.0 for the one graded submission.");
            assertEquals(50.0, progressAnalytics.get("completionRate"), "Completion rate should be 50% (1 submission out of 2 expected).");
            assertEquals(1, progressAnalytics.get("studentsWithAllSubmissions"), "one student has submitted all assignments.");
            assertEquals(50.0, progressAnalytics.get("studentCompletionRate"), "Student completion rate should be 50% as one student completed all assignments.");
            mockedCourseSvc.when(() -> courseService.search_course("nonexistent"))
                    .thenReturn(null);
            String invalidCourse = "nonexistent";
            assertThrows(IllegalArgumentException.class,
                    () -> assignmentService.getProgressAnalytics(invalidCourse),
                    "Should throw IllegalArgumentException for invalid course");
        }
    }

    @Test
    public void testGetProgressAnalytics_WithNoAssignments() {
        try (MockedStatic<courseService> mockedCourseSvc = mockStatic(courseService.class);
             MockedStatic<student_coursesService> mockedStudentSvc = mockStatic(student_coursesService.class)) {

            mockedCourseSvc.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);
            mockedStudentSvc.when(() -> student_coursesService.getStudentsEnrolledInCourse(COURSE_TITLE))
                    .thenReturn(enrolledStudents);

            Map<String, Object> analytics = assignmentService.getProgressAnalytics(COURSE_TITLE);

            assertEquals(0, analytics.get("totalAssignments"));
            assertEquals(2, analytics.get("totalStudents"));
            assertEquals(0, analytics.get("expectedTotalSubmissions"));
            assertEquals(0, analytics.get("actualSubmissions"));
            assertEquals(0, analytics.get("gradedSubmissions"));
            assertEquals(0.0, analytics.get("averageGrade"));
            assertEquals(0.0, analytics.get("completionRate"));
            assertEquals(0, analytics.get("studentsWithAllSubmissions"));
            assertEquals(0.0, analytics.get("studentCompletionRate"));
        }
    }

    @Test
    public void testGetProgressAnalytics_WithUnsubmittedAssignments() {
        try (MockedStatic<courseService> mockedCourseSvc = mockStatic(courseService.class);
             MockedStatic<student_coursesService> mockedStudentSvc = mockStatic(student_coursesService.class)) {

            mockedCourseSvc.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);
            mockedStudentSvc.when(() -> student_coursesService.getStudentsEnrolledInCourse(COURSE_TITLE))
                    .thenReturn(enrolledStudents);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            Map<String, Object> analytics = assignmentService.getProgressAnalytics(COURSE_TITLE);

            assertEquals(1, analytics.get("totalAssignments"));
            assertEquals(2, analytics.get("totalStudents"));
            assertEquals(2, analytics.get("expectedTotalSubmissions"));
            assertEquals(0, analytics.get("actualSubmissions"));
            assertEquals(0, analytics.get("gradedSubmissions"));
            assertEquals(0.0, analytics.get("averageGrade"));
            assertEquals(0.0, analytics.get("completionRate"));
            assertEquals(0, analytics.get("studentsWithAllSubmissions"));
            assertEquals(0.0, analytics.get("studentCompletionRate"));
        }
    }
    @Test
    public void testGetAssignmentNameById() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            Assignment assignment = new Assignment(ASSIGNMENT_ID, "Assignment 1", COURSE_TITLE,
                    "First assignment for Software Engineering", "2024-12-15", "assignment");
            assignmentService.addAssignment(COURSE_TITLE, assignment);

            String name = assignmentService.getassinmentnameBYID(ASSIGNMENT_ID, COURSE_TITLE);
            assertNotNull(name, "Should return assignment name for valid ID");
            assertEquals("Assignment 1", name);

            String invalidId = "nonexistent";
            String nullName = assignmentService.getassinmentnameBYID(invalidId, COURSE_TITLE);
            assertNull(nullName, "Should return null for invalid assignment ID");
        }
    }
    @Test
    public void testGetAssignmentNameById_WithNonexistentCourse() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course("nonexistent"))
                    .thenReturn(null);

            String name = assignmentService.getassinmentnameBYID(ASSIGNMENT_ID, "nonexistent");
            assertNull(name, "Should return null for non-existent course");
        }
    }

    @Test
    public void testGetAssignmentNameById_WithEmptyAssignmentList() {
        try (MockedStatic<courseService> mockedStatic = mockStatic(courseService.class)) {
            mockedStatic.when(() -> courseService.search_course(COURSE_TITLE))
                    .thenReturn(test_crs);

            String name = assignmentService.getassinmentnameBYID(ASSIGNMENT_ID, COURSE_TITLE);
            assertNull(name, "Should return null when assignment list is empty");
        }
    }
}