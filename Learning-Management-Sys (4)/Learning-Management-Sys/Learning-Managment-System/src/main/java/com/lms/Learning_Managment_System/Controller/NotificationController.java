package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Model.Notification;
import com.lms.Learning_Managment_System.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("{userRole}/notification_Page/")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("{userID}/get")
    public ResponseEntity<?> getNotifications(@PathVariable String userRole, @PathVariable int userID, @RequestParam boolean unreadonly) {
        notificationService.validateUserRole(userRole,userID);
        return notificationService.getNotifications(userID,unreadonly);
    }

    @GetMapping("/MarkAllAsRead/{userID}")
    public void MarkAllAsRead(@PathVariable String userRole,@PathVariable int userID) {
        notificationService.validateUserRole(userRole,userID);
        notificationService.MarkAllAsRead(userID);
    }

}
