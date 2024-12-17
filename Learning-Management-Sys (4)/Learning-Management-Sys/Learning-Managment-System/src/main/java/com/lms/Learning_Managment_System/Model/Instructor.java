package com.lms.Learning_Managment_System.Model;

public class Instructor extends User {

    public Instructor(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, "Instructor");
    }
}