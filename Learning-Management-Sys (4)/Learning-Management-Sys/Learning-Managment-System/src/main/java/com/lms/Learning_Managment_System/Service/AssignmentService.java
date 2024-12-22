package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.lms.Learning_Managment_System.Model.Assignment;
import com.lms.Learning_Managment_System.Model.assignmentSubmission;
import com.lms.Learning_Managment_System.Model.course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class AssignmentService {
    @Autowired
    courseService courseService;

    private static final String assignmentsFile = "assignments.json";
    protected static  String STORAGE_DIRECTORY = "C:\\Storage";
    private Map<String, List<Assignment>> asssignments = new HashMap<>();
    @Autowired
    private com.lms.Learning_Managment_System.Controller.student_courses_Controller student_courses_Controller;
    public AssignmentService() {
        loadFromJsonFile();
    }

    public void validateCourse(String courseTitle){
        course crs = courseService.search_course(courseTitle.toLowerCase());
        if (crs == null)
            throw new IllegalArgumentException("Course not found.");
    }
    public void addAssignment(String courseTitle, Assignment assignment) {
        validateCourse(courseTitle);
        asssignments.putIfAbsent(courseTitle, new ArrayList<>());
        asssignments.get(courseTitle).add(assignment);
        saveAssignmentsToFile();
    }


    public List<Assignment> getAssignments(String courseTitle) {
        List<Assignment> courseAssignments = asssignments.getOrDefault(courseTitle.toLowerCase(), new ArrayList<>());
        System.out.println("Assignments for " + courseTitle + ": " + courseAssignments);
        return courseAssignments;
    }


    public Assignment getAssignmentById(String courseTitle, String assignmentId) {
        List<Assignment>crsAssignments=asssignments.getOrDefault(courseTitle, new ArrayList<>());
        return crsAssignments.stream().filter(assignment ->
                assignment.getAssessmentID().equals(assignmentId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Assignment with ID " + assignmentId + " not found."));
    }


    public void saveFile(MultipartFile file, String courseTitle, String assignmentId, String studentId) throws IOException {
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
            assignmentSubmission submission=new assignmentSubmission(studentId, target.getAbsolutePath());
            assignment.addSubmission(submission);
            saveAssignmentsToFile();
        } else {
            throw new IllegalArgumentException("Assignment not found.");
        }
    }
    public void gradeAssignment(String courseTitle, String assignmentId, String studentId, String grade, String feedback) {
        validateCourse(courseTitle);
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
        saveAssignmentsToFile();
    }

    public List<assignmentSubmission> getSubmissionsWithFeedback(String courseTitle, String assignmentId) {
        Assignment assignment = getAssignmentById(courseTitle, assignmentId);
        if (assignment != null) {
            return assignment.getSubmissions();
        } else {
            throw new IllegalArgumentException("Assignment not found.");
        }
    }
    public List<assignmentSubmission> trackSubmissionsProgress(String courseTitle, String assignmentId) {
        validateCourse(courseTitle);
        Assignment assignment = getAssignmentById(courseTitle, assignmentId);
        if (assignment != null) {
            return assignment.getSubmissions();
        } else {
            throw new IllegalArgumentException("Assignment not found.");
        }
    }
    public Map<String, Object> getProgressAnalytics(String courseTitle) {
        validateCourse(courseTitle);
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
//    public void loadFromJsonFile(){
//        ObjectMapper mapper = new ObjectMapper();
//        try{
//            File file = new File(assignmentsFile);
//            if (file.exists())
//                asssignments = mapper.readValue(file , mapper.getTypeFactory().constructMapType(HashMap.class , String.class , List.class));
//            else
//                System.out.println("File does not exist");
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//    public void saveAssignmentsToFile(){
//        ObjectMapper mapper = new ObjectMapper();
//        try{
//            mapper.writeValue(new File(assignmentsFile) , asssignments);
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//    }
    public void loadFromJsonFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(assignmentsFile).getAbsoluteFile();

            // Add detailed logging
            System.out.println("Attempting to load file: " + file.getAbsolutePath());
            System.out.println("File exists: " + file.exists());
            System.out.println("File is readable: " + file.canRead());

            if (file.exists() && file.canRead()) {
                TypeReference<Map<String, List<Assignment>>> typeRef =
                        new TypeReference<Map<String, List<Assignment>>>() {};

                // Enable polymorphic deserialization
                mapper.registerSubtypes(Assignment.class);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                asssignments = mapper.readValue(file, typeRef);

                System.out.println("Loaded assignments: " + asssignments);
            } else {
                System.out.println("File cannot be read or does not exist");
                asssignments = new HashMap<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading assignments file: " + e.getMessage());
            e.printStackTrace();
            asssignments = new HashMap<>();
        }
    }
    private void saveAssignmentsToFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(assignmentsFile).getAbsoluteFile();

            // Ensure parent directory exists
            file.getParentFile().mkdirs();

            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.registerSubtypes(Assignment.class);

            mapper.writeValue(file, asssignments);

            System.out.println("Saved assignments to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving assignments file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public String getassinmentnameBYID(String assinment_id, String courseTitle) {
        List<Assignment> assignments = getAssignments(courseTitle);
            for (Assignment assignment : assignments) {
                if (assignment.getAssessmentID() != null && assignment.getAssessmentID().equals(assinment_id)) {
                    return assignment.getAssessmentName();
                }
            }
        return null;
    }
}