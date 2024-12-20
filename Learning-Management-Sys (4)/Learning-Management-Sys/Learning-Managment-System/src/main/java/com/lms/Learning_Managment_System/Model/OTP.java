package com.lms.Learning_Managment_System.Model;

import java.time.LocalDateTime;

public class OTP {
    private int id;
    private String otp;
    private String email;
    private String lesson;

    public OTP(int id, String otp, String email, String lesson) {
        this.otp = otp;
        this.email = email;
        this.id = id;
        this.lesson = lesson;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {

        return id;
    }

    public String getLesson() {
        return lesson;
    }
}
