package com.lms.Learning_Managment_System.Model;

import lombok.Data;

@Data
public class Notification {
    String ID;
    String message;
    boolean status;
    String user_ID;
    public Notification(String ID, String message, String user_ID) {
        this.ID = ID;
        this.message = message;
        this.status = false;
        this.user_ID = user_ID;
    }
    public boolean is_read(){
        return status;
    }
}
