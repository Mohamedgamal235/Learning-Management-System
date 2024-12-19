package com.lms.Learning_Managment_System.Model;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, "Admin");
    }
}