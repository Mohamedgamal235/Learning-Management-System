package com.lms.Learning_Managment_System.Model;

import java.time.LocalDateTime;

public class OTP {
    private int id ;
    private String otp ;
    private String email ;
    public OTP(){}
    public OTP(int id , String otp , String email) {
        this.otp = otp ;
        this.email = email ;
        this.id = id;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public int getId() {

        return id;
    }
}
