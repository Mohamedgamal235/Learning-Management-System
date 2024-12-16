package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.Assessment;
import com.lms.Learning_Managment_System.Model.Assignment;
import com.lms.Learning_Managment_System.Model.assignmentSubmission;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class AssessmentService {
    private final courseService courseService;

    private static final String assessmentsFile = "assessments.json";
    private static final String STORAGE_DIRECTORY = "G:\\Storage";
    private Map<String, List<Assessment>> assessments = new HashMap<>();
    public AssessmentService(com.lms.Learning_Managment_System.Service.courseService courseService) {
        this.courseService = courseService;
        loadAssessmentsFromFile();
    }
    public void addAssignment(String courseTitle, Assignment assignment) {
        if (courseService.search_course(courseTitle)==null) {
            throw new IllegalArgumentException("Course not found.");
        }
        assessments.putIfAbsent(courseTitle, new ArrayList<>());
        assessments.get(courseTitle).add(assignment);
        saveAssessmentsToFile();
    }
    public List<Assignment> getAssignments(String courseTitle) {
        if (courseService.search_course(courseTitle)==null) {
            throw new IllegalArgumentException("Course not found.");
        }
        System.out.println("Fetching assignments for course: " + courseTitle);
        List<Assignment> assignmentList = new ArrayList<>();
        List<Assessment> courseAssessments = assessments.getOrDefault(courseTitle, List.of());

        for (Assessment assessment : courseAssessments) {
            if (assessment instanceof Assignment) {
                assignmentList.add((Assignment) assessment);
            }
        }

        System.out.println("Assignments found: " + assignmentList);
        return assignmentList;
    }
    private Assignment getAssignmentById(String courseTitle, String assignmentId) {
        if (courseService.search_course(courseTitle)==null) {
            throw new IllegalArgumentException("Course not found.");
        }
        List<Assignment> assignments = getAssignments(courseTitle);
        for (Assignment assignment : assignments) {
            if (assignment.getAssessmentID().equals(assignmentId)) {
                return assignment;
            }
        }
        return null;
    }
    public void saveFile(MultipartFile file, String courseTitle, String assignmentId, String studentId, String studentName) throws IOException {
        if (courseService.search_course(courseTitle)==null) {
            throw new IllegalArgumentException("Course not found.");
        }
        if(file == null) {
            throw new NullPointerException("File is null");
        }
        File storageDir = new File(STORAGE_DIRECTORY);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                throw new IOException("Failed to create storage directory.");
            }
        }
        var target = new File(STORAGE_DIRECTORY + File.separator + file.getOriginalFilename());
        if(!Objects.equals(target.getParent(), STORAGE_DIRECTORY)){
            throw new SecurityException("Unsupported filename!");
        }
        Files.copy(file.getInputStream(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        // Add the file path to the corresponding assignment and associate it with the student
        Assignment assignment = getAssignmentById(courseTitle, assignmentId);
        if (assignment != null) {
            assignmentSubmission submission=new assignmentSubmission(studentId, studentName, target.getAbsolutePath());
            assignment.addSubmission(submission);
            saveAssessmentsToFile();
        } else {
            throw new IllegalArgumentException("Assignment not found.");
        }
    }
    public void gradeAssignment(String courseTitle, String assignmentId, String studentId, String grade, String feedback) {
        if (courseService.search_course(courseTitle)==null) {
            throw new IllegalArgumentException("Course not found.");
        }
        Assignment assignment = getAssignmentById(courseTitle, assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found.");
        }
        Optional<assignmentSubmission> submission = assignment.getSubmissions().stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst();
        if (!submission.isPresent()) {
            throw new IllegalArgumentException("Assignment not submitted by this student.");
        }
        submission.get().setGrade(grade);
        submission.get().setFeedback(feedback);
        saveAssessmentsToFile();
    }

    public List<assignmentSubmission> getSubmissionsWithFeedback(String courseTitle, String assignmentId) {
        if (courseService.search_course(courseTitle)==null) {
            throw new IllegalArgumentException("Course not found.");
        }
        Assignment assignment = getAssignmentById(courseTitle, assignmentId);
        if (assignment != null) {
            return assignment.getSubmissions();
        } else {
            throw new IllegalArgumentException("Assignment not found.");
        }
    }
    public List<assignmentSubmission> trackSubmissionsProgress(String courseTitle, String assignmentId) {
        if (courseService.search_course(courseTitle)==null) {
            throw new IllegalArgumentException("Course not found.");
        }
        Assignment assignment = getAssignmentById(courseTitle, assignmentId);
        if (assignment != null) {
            return assignment.getSubmissions();
        } else {
            throw new IllegalArgumentException("Assignment not found.");
        }
    }
    public Map<String, Object> getProgressAnalytics(String courseTitle) {
        List<Assignment> assignments = getAssignments(courseTitle);
        int totalAssignments = assignments.size();
        int totalSubmissions = 0;
        int gradedAssignments = 0;
        double totalGrade = 0;
        for (Assignment assignment : assignments) {
            List<assignmentSubmission> submissions = assignment.getSubmissions();
            totalSubmissions += submissions.size();
            for (assignmentSubmission submission : submissions) {
                if (submission.getGrade() != null) {
                    gradedAssignments++;
                    totalGrade += Double.parseDouble(submission.getGrade());
                }
            }
        }
        Map<String, Object> progressData = new HashMap<>();
        progressData.put("totalAssignments", totalAssignments);
        progressData.put("totalSubmissions", totalSubmissions);
        progressData.put("gradedAssignments", gradedAssignments);
        progressData.put("averageGrade", totalSubmissions > 0 ? totalGrade / totalSubmissions : 0);
        progressData.put("completionRate", totalAssignments > 0 ? (gradedAssignments / (double) totalAssignments) * 100 : 0);
        return progressData;
    }
    private void loadAssessmentsFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerSubtypes(Assignment.class);
        try {
            File file = new File(assessmentsFile);
            if (file.exists()) {
                // Read raw content to check the JSON structure
                String rawContent = new String(Files.readAllBytes(file.toPath()));
                System.out.println("Raw JSON content: " + rawContent);

                // Deserialize into a Map with the correct type information
                assessments = objectMapper.readValue(file,
                        objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, List.class));

                System.out.println("Assessments loaded: " + assessments);
            } else {
                System.out.println("File not found: " + assessmentsFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAssessmentsToFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(assessmentsFile), assessments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}