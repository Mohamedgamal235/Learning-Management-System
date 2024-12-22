package com.lms.Learning_Managment_System.Service;

import com.lms.Learning_Managment_System.Controller.UserController;
import com.lms.Learning_Managment_System.Model.Assignment;
import com.lms.Learning_Managment_System.Model.User;
import com.lms.Learning_Managment_System.Model.assignmentSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class GradeReportService {

    @Autowired
    private QuizService quizService;
    @Autowired
    private UserController userController;
    @Autowired
    private ReportService reportService;
    @Autowired
    private AttendenceService attendenceService;
    @Autowired
    private AssignmentService assignmentService;

    public String generateGradeReportForCourse(String courseTitle, int instructorId, String fileName) throws IOException {
        quizService.validateInstructorForCourse(instructorId , courseTitle);
        Map<Integer , Map<String  , Integer>> studentGrades = quizService.getAllStudentGradesForCourse(courseTitle , instructorId);

        List<Map<String , Object>> gradesData = new ArrayList<>();

        for (Map.Entry<Integer, Map<String, Integer>> studentEntry : studentGrades.entrySet()) {
            int studentId = studentEntry.getKey();
            Map<String, Integer> quizzesGrades = studentEntry.getValue();

            User student = userController.getAllUsers().stream()
                    .filter(user -> user.getId() == studentId && "student".equalsIgnoreCase(user.getRole()))
                    .findFirst().orElse(null);

            if (student != null) {
                for (Map.Entry<String, Integer> quizEntry : quizzesGrades.entrySet()) {
                   String quizId = quizEntry.getKey();
                   int grade = quizEntry.getValue();

                   Map<String , Object> recordGrades = new HashMap<>();
                   recordGrades.put("email" , student.getEmail());
                   recordGrades.put("firstName" , student.getFirstName());
                   recordGrades.put("lastName" , student.getLastName());
                   recordGrades.put("Quiz ID" , quizId);
                   recordGrades.put("grade" , grade);
                   gradesData.add(recordGrades);
                }
            }
        }
        return reportService.generateGradesReport(gradesData , fileName);
    }

    public String generateAttendanceReportForLessons(String fileName) throws IOException {
        Map<String , List<String>> attendanceData = attendenceService.getAttendanceInfo();

        List<Map<String , Object>> attendanceRecords = new ArrayList<>();

        for (Map.Entry<String , List<String>> lessonEntry : attendanceData.entrySet()){
            String lesson = lessonEntry.getKey();
            List<String> studentEmails = lessonEntry.getValue();

            for (String email : studentEmails) {
                User student = userController.getAllUsers().stream()
                        .filter(user -> user.getEmail().equalsIgnoreCase(email) && "student".equalsIgnoreCase(user.getRole()))
                        .findFirst().orElse(null);

                if (student != null) {
                    Map<String , Object> recordAttendance = new HashMap<>();
                    recordAttendance.put("email" , student.getEmail());
                    recordAttendance.put("firstName" , student.getFirstName());
                    recordAttendance.put("lastName" , student.getLastName());
                    recordAttendance.put(lesson , lesson) ;
                    recordAttendance.put("attendance" , true) ;
                    attendanceRecords.add(recordAttendance);
                }
            }
        }

        return reportService.generateAttendanceReport(attendanceRecords , fileName);
    }


    public String generateAssignmentGradesReport(String courseTitle, int instructorId, String fileName) throws IOException {
        assignmentService.validateCourse(courseTitle);

        List<Assignment> assignments = assignmentService.getAssignments(courseTitle);
        List<Map<String, Object>> assignmentGradesData = new ArrayList<>();

        for (Assignment assignment : assignments) {
            for (assignmentSubmission submission : assignment.getSubmissions()) {
                User student = userController.getAllUsers().stream()
                        .filter(user -> user.getId() == Integer.parseInt(submission.getStudentId())
                                && "student".equalsIgnoreCase(user.getRole()))
                        .findFirst().orElse(null);

                if (student != null) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("email", student.getEmail());
                    record.put("firstName", student.getFirstName());
                    record.put("lastName", student.getLastName());
                    record.put("assignmentId", assignment.getAssessmentID());
                    record.put("grade", submission.getGrade());
                    record.put("feedback", submission.getFeedback());
                    assignmentGradesData.add(record);
                }
            }
        }

        return reportService.generateAssignmentGradesReport(assignmentGradesData, fileName);
    }

}
