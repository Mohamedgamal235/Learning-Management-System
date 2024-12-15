package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.Notification;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class NotificationService {
    private static final String NOTIFICATION_FILE = "Notifications_of_users.json";
    private List<Notification> notifications = new ArrayList<>();

    public NotificationService() {
        loadNotificationsFromfile();
    }
    public void add(String message,String userID){
        Notification notification = new Notification(UUID.randomUUID().toString(),message,userID);
        notifications.add(notification);
        saveNotificationsTofile();
    }
    public List<Notification> getNotifications(String userID,boolean unreadOnly){
        return notifications.stream()
                .filter(notification -> notification.getUser_ID().equals(userID))
                .filter(notification -> !unreadOnly || !notification.is_read())
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
    public void MarkAllAsRead(String userID){
        for (Notification notification : notifications) {
            if (notification.getUser_ID().equals(userID)) {
                notification.setStatus(true);
            }
        }
        saveNotificationsTofile();
    }
}
