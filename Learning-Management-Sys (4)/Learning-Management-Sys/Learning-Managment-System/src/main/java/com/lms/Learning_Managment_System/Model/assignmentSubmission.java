package com.lms.Learning_Managment_System.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class assignmentSubmission {
    private String studentId;
    private String studentName;
    private String filePath;
    private String grade=null;
    private String feedback=null;
    public assignmentSubmission(String studentId, String studentName, String filePath) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.filePath = filePath;
        this.feedback = null;
        this.grade = null;
    }
}
