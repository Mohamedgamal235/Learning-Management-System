package com.lms.Learning_Managment_System.Model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class course {
    private int instructor_id;
    private String course_title;
    private int crs_duration_inMonths;
    private String course_description;
    private List<lesson> course_lessons = new ArrayList<>();
    private List<Question> questionBank = new ArrayList<>();
    private boolean isAvailableForRegistration ;
}
