package com.lms.Learning_Managment_System.Model;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Assignment.class, name = "assignment"),
        @JsonSubTypes.Type(value = Quiz.class, name = "quiz")
})
public class Assessment {
    private String assessmentID;
    private String assessmentName;
    private String courseTitle;
    private String assessmentDescription;
    private String assessmentDate;
}
