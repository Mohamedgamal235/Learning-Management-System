package com.lms.Learning_Managment_System.Controller;

import com.lms.Learning_Managment_System.Controller.NotificationController;
import com.lms.Learning_Managment_System.Model.Notification;
import com.lms.Learning_Managment_System.Model.User;
import com.lms.Learning_Managment_System.Service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NotificationSystemTest {

    @Mock
    private NotificationService notificationService;
    @Mock
    private UserController userController;

    @InjectMocks
    private NotificationController notificationController;
    private int validUserId = 2;
    private int invalidUserId = 3;
    private String validUserRole = "student";
    private String invalidUserRole = "invalid_role";
    private List<Notification> Mockednotifications;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockednotifications = Arrays.asList(
                new Notification("NT-1","Notification 1",true,validUserId),
                new Notification("NT-2","Notification 2",true,validUserId)
        );


    }

    @Test
    void getNotifications_ValidRequest_ReturnsAllNotifications() {
        when(userController.getLoggedInStudents()).thenReturn(Map.of("student1", validUserId));
        when(userController.getLoggedInInstructors()).thenReturn(new HashMap<>());
        List<String> messages = Arrays.asList("Notification 1", "Notification 2");
        doReturn(ResponseEntity.ok(messages))
                .when(notificationService).getNotifications(validUserId, false);
        ResponseEntity<?> response = notificationController.getNotifications(validUserRole, validUserId, false);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messages, response.getBody());
    }

    @Test
    void getNotifications_validRequest_ReturnUnreadNotifications() {
        when(userController.getLoggedInStudents()).thenReturn(Map.of("student1", validUserId));
        when(userController.getLoggedInInstructors()).thenReturn(new HashMap<>());
        List<String> unreadMessages = Arrays.asList("Notification 1");
        doReturn(ResponseEntity.ok(unreadMessages)).when(notificationService).getNotifications(validUserId, true);
        ResponseEntity<?> response = notificationController.getNotifications(validUserRole, validUserId, true);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(unreadMessages, response.getBody());
    }

    @Test
    void getNotifications_InvalidRole_ThrowsException() {
        boolean unreadOnly = true;

        doThrow(new IllegalArgumentException("Invalid user role"))
                .when(notificationService).validateUserRole(invalidUserRole, validUserId);

        assertThrows(IllegalArgumentException.class, () ->
                notificationController.getNotifications(invalidUserRole, validUserId, unreadOnly));
        verify(notificationService).validateUserRole(invalidUserRole, validUserId);
        verify(notificationService, never()).getNotifications(anyInt(), anyBoolean());
    }

    @Test
    void getNotifications_invaliduserId_ThrowsException() {
        doThrow(new IllegalArgumentException("Access Denied: Users must be logged in to receive notifications."))
                .when(notificationService).validateUserRole(validUserRole, invalidUserId);

        assertThrows(IllegalArgumentException.class, () ->
                notificationController.getNotifications(validUserRole, invalidUserId, false));

        verify(notificationService, never()).getNotifications(anyInt(), anyBoolean());
    }

    @Test
    void markAllAsRead_ValidRequest_SuccessfullyMarksNotifications() {
        int userId = 1;
        String userRole = "student";

        doNothing().when(notificationService).validateUserRole(userRole, userId);
        doNothing().when(notificationService).MarkAllAsRead(userId);

        notificationController.MarkAllAsRead(userRole, userId);

        verify(notificationService).validateUserRole(userRole, userId);
        verify(notificationService).MarkAllAsRead(userId);
    }

}

