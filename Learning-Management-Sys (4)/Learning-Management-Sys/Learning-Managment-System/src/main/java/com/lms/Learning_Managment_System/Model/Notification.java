package com.lms.Learning_Managment_System.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {
    String ID;
    String message;
    boolean status;
    int user_ID;
    public Notification(String ID, String message, int user_ID) {
        this.ID = ID;
        this.message = message;
        this.status = false;
        this.user_ID = user_ID;
    }
    public boolean is_read(){
        return status;
    }
}
