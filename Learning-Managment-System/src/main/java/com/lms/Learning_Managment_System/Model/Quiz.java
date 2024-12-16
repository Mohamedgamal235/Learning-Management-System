package com.lms.Learning_Managment_System.Model;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("quiz")
public class Quiz extends Assessment{
    private List<Question> questions = new ArrayList<>() ;
}
