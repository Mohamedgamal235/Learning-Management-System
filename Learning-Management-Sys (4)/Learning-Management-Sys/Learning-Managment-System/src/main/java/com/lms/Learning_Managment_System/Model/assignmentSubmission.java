package com.lms.Learning_Managment_System.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class assignmentSubmission {
    private String studentId;
    private String filePath;
    private String grade=null;
    private String feedback=null;
    public assignmentSubmission(String studentId, String filePath) {
        this.studentId = studentId;
        this.filePath = filePath;
        this.feedback = null;
        this.grade = null;
    }
}
