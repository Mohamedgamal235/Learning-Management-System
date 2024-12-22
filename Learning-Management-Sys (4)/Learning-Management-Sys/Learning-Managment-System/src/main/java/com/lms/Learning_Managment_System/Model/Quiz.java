package com.lms.Learning_Managment_System.Model;


import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("quiz")
public class Quiz extends Assessment{
    private List<Question> questions = new ArrayList<>() ;
    private int instructorID;
    private String feedback;
    private Map<Integer, Integer> studentScores = new HashMap<>();

    public Map<Integer, Integer> getStudentScores() {
        return studentScores;
    }

    public void setStudentScores(Map<Integer, Integer> studentScores) {
        this.studentScores = studentScores;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(int instructorID) {
        this.instructorID = instructorID;
    }


}
