package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Service.AttendenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class OTPController {
    private final AttendenceService attendenceService;

    @PostMapping("/request")
    public ResponseEntity<String> requestOtp(@RequestBody Map<String, Object> payload) {
        try {
            String email = (String) payload.get("email");
            String name = (String) payload.get("name");
            int studentId = (int) payload.get("studentId");
            String lesson = (String) payload.get("lesson");

            attendenceService.attend(email, name, studentId, lesson);
            return ResponseEntity.ok("OTP sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send OTP.");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateOtp(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String otp = payload.get("otp");
            String lesson = payload.get("lesson");
            int studentId = Integer.parseInt(payload.get("studentId"));
            System.out.println(otp);
            if (attendenceService.validateOTP(email, otp)) {
                attendenceService.markAttendance(lesson , email);
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
