package com.lms.Learning_Managment_System.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    private int id;
    private String type; // MCQ, TRUE_FALSE, SHORT_ANSWER
    private String questionText;
    private List<String> options; // Only for MCQs
    private String correctAnswer; // Answer based on type
}