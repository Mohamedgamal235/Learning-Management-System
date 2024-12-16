package com.lms.Learning_Managment_System.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class course {
    private String course_title;
    private int crs_duration_inMonths;
    private String course_description;
    private List<lesson> course_lessons = new ArrayList<>();
}