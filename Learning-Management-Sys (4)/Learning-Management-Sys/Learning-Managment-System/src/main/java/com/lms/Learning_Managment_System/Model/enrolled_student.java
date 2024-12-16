package com.lms.Learning_Managment_System.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class enrolled_student {
    private int enrolled_student_id;
    private String enrolled_student_name;
    private List<course> enrolled_courses = new ArrayList<>();
}
