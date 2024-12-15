package com.lms.Learning_Managment_System.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class lesson {
    private String lessonTitle;
    private  String lesson_objectives;
    private List<String> lesson_files = new ArrayList<>();

}
