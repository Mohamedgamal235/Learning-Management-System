package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.OTP;
import com.lms.Learning_Managment_System.Service.AttendenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class OTPController {
    private final AttendenceService attendenceService;
    @Autowired
    private UserController userController;
    String lesson = "" ;
    @PostMapping("/{StudentId}/request")
    public ResponseEntity<String> requestOtp(@RequestBody Map<String, Object> payload , @PathVariable int StudentId) {
        if (!userController.getLoggedInStudents().containsValue(StudentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in Student to manage courses");
        }
        try {
            String email = userController.getEmailById(StudentId);
            String name = userController.getStudentNameById(StudentId) ;
             lesson = (String) payload.get("lesson");

            attendenceService.attend(email, name, StudentId, lesson);
            return ResponseEntity.ok("OTP sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send OTP.");
        }
    }

    @PostMapping("/{StudentId}/validate")
    public ResponseEntity<String> validateOtp(@RequestBody Map<String, String> payload , @PathVariable int StudentId) {
        if (!userController.getLoggedInStudents().containsValue(StudentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be a logged in Student to manage courses");
        }
        try {
            String email = userController.getEmailById(StudentId) ;
            String otp = payload.get("otp");
            System.out.println(otp);
            if (attendenceService.validateOTP(email, otp ,lesson)) {
                attendenceService.markAttendance(email);
                return ResponseEntity.ok("Attendance marked successfully.");
            } else {
                return ResponseEntity.status(401).body("Invalid OTP.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error during validation.");
        }
    }
}
