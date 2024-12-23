package com.lms.Learning_Managment_System.Model;

import java.time.LocalDateTime;

public class OTP {
    private int id;
    private String otp;
    private String email;
    private String lesson;
     public OTP() {}
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

    public String getOtp() {
        return otp;
    }

    public String getEmail() {
        return email;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLesson() {
        return lesson;
    }
}
