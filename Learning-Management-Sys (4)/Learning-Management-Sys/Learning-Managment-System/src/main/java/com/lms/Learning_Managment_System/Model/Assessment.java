package com.lms.Learning_Managment_System.Model;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("assessmentID")
    private String assessmentID;
    @JsonProperty("assessmentName")
    private String assessmentName;
    @JsonProperty("courseTitle")
    private String courseTitle;
    @JsonProperty("assessmentDescription")
    private String assessmentDescription;
    @JsonProperty("assessmentDate")
    private String assessmentDate;
    @JsonProperty("type")
    private String type;
}
