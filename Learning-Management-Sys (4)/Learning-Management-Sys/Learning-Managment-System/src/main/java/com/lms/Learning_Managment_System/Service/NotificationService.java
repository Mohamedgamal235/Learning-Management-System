package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Controller.UserController;
import com.lms.Learning_Managment_System.Model.Notification;
import com.lms.Learning_Managment_System.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class NotificationService {
    @Autowired
    private com.lms.Learning_Managment_System.Controller.UserController UserController;

    private static final String NOTIFICATION_FILE = "Notifications_of_users.json";
    private List<Notification> notifications = new ArrayList<>();

    public NotificationService() {
        loadNotificationsFromfile();
    }
    public void add(String message,int userID){
        if (!UserController.getLoggedInStudents().containsValue(userID) && !UserController.getLoggedInInstructors().containsValue(userID)) {
            throw new IllegalStateException("Access Denied: You must be logged in to send a notification.");
        }
        Notification notification = new Notification(UUID.randomUUID().toString(),message,userID);
        notifications.add(notification);
        saveNotificationsTofile();
    }

    public List<String> getNotifications(int userID,boolean unreadOnly){
        if (!UserController.getLoggedInStudents().containsValue(userID) && !UserController.getLoggedInInstructors().containsValue(userID)) {
            throw new IllegalStateException("Access Denied: Users must be logged in to receive notifications.");
        }
        return notifications.stream()
                .filter(notification -> (notification.getUser_ID() == userID))
                .filter(notification -> !unreadOnly || !notification.is_read())
                .map(Notification::getMessage)
                .collect(Collectors.toList());
    }

    private void loadNotificationsFromfile(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File(NOTIFICATION_FILE);
            if (file.exists()) {
                notifications = objectMapper.readValue(file,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Notification.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveNotificationsTofile(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(NOTIFICATION_FILE), notifications);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void MarkAllAsRead(int userID){
        for (Notification notification : notifications) {
            if (notification.getUser_ID() == userID) {
                notification.setStatus(true);
            }
        }
        saveNotificationsTofile();
    }
    public void validateUserRole(String userRole,int userID) {
        if (!"student".equalsIgnoreCase(userRole) && !"instructor".equalsIgnoreCase(userRole)&& !userRole.equalsIgnoreCase(getUserRolebasedonId(userID))) {
            throw new IllegalArgumentException("Invalid user role: " + userRole);
        }
    }

    private String getUserRolebasedonId(int userID){
        List<User> users = UserController.getAllUsers();
        for (User user : users) {
            if (user.getId() == userID) {
                return user.getRole();
            }
        }
        return null;
    }

}
