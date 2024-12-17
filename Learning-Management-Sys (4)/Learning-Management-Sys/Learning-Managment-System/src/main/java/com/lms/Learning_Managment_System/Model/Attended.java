package com.lms.Learning_Managment_System.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Attended {

    private List<String> studentEmails;
    private String lesson;

    // Default constructor
    public Attended() {}

    // Constructor for initialization (optional)
    @JsonCreator
    public Attended(@JsonProperty("studentEmails") List<String> studentIds,
                    @JsonProperty("lesson") String lesson) {
        this.studentEmails = studentIds;
        this.lesson = lesson;
    }

    // Getter for studentIds
    public List<String> getStudentIds() {
        return studentEmails;
    }

    // Setter for studentIds
    public void setStudentEmails(List<String> studentIds) {
        this.studentEmails = studentIds;
    }

    // Getter for lesson
    public String getLesson() {
        return lesson;
    }

    // Setter for lesson
    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    // Method to add student ID to the list
    public void addEmail(String studentId) {
        this.studentEmails.add(studentId);
    }
}
