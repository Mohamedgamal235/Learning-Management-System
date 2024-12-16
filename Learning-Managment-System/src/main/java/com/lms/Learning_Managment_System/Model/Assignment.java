package com.lms.Learning_Managment_System.Model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("assignment")
@Data
public class Assignment extends Assessment {

    private List<assignmentSubmission> submissions= new ArrayList<>();

    public Assignment(String assessmentID, String assessmentName,String courseTitle,String assessmentDescription,String assessmentDate){
        super(assessmentID,assessmentName,courseTitle,assessmentDescription,assessmentDate);
    }

    public void addSubmission(assignmentSubmission submission) {
        this.submissions.add(submission);
    }


    public void gradeSubmission(String studentId, String grade, String feedback) {
        for (assignmentSubmission submission : submissions) {
            if (submission.getStudentId().equals(studentId)) {
                submission.setGrade(grade);
                submission.setFeedback(feedback);
                break;
            }
        }
    }

}
