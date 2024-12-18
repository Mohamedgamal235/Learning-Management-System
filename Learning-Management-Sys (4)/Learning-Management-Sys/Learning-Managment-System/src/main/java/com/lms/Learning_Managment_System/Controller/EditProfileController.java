package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.User;
import com.lms.Learning_Managment_System.Service.EditProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class EditProfileController {

    private final EditProfileService editProfileService;

    @Autowired
    private UserController userController;

    @PostMapping("/{userId}/edit")
    public ResponseEntity<String> editProfile(@RequestBody Map<String, Object> payload, @PathVariable int userId) {
        try {
            String firstName = (String) payload.get("firstName");
            String lastName = (String) payload.get("lastName");
            String password = (String) payload.get("password");

            if (userController.getLoggedInStudents().containsValue(userId)) {
                editProfileService.editStudentProfile(userId, firstName, lastName, password);
            } else if (userController.getLoggedInInstructors().containsValue(userId)) {
                editProfileService.editInstructorProfile(userId, firstName, lastName, password);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: You must be logged in to Edit");
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Profile successfully edited");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while editing the profile");
        }
    }
}
