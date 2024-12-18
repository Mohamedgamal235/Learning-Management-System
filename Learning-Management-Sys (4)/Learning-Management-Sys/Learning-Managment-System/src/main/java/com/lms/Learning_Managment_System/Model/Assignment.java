package com.lms.Learning_Managment_System.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, // Use a 'name' property to determine the type
        include = JsonTypeInfo.As.PROPERTY, // Type info as a property in JSON
        property = "type", // Property name to specify the type
        visible = true // Makes 'type' property available during deserialization
)
@JsonTypeName("assignment")
@Data
public class Assignment extends Assessment {
    @JsonProperty("submissions")
    private List<assignmentSubmission> submissions= new ArrayList<>();
    @JsonCreator
    public Assignment(
            @JsonProperty("assessmentID") String assessmentID,
            @JsonProperty("assessmentName") String assessmentName,
            @JsonProperty("courseTitle") String courseTitle,
            @JsonProperty("assessmentDescription") String assessmentDescription,
            @JsonProperty("assessmentDate") String assessmentDate,
            @JsonProperty("type") String type
    ) {
        super(assessmentID, assessmentName, courseTitle, assessmentDescription, assessmentDate,type);
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
