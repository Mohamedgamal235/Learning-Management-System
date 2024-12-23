package com.lms.Learning_Managment_System;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Service.AttendenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendenceServiceTest {

    @Mock
    ObjectMapper objectMapper;
    @Spy
    @InjectMocks
    private AttendenceService attendenceService;
    private static final String ATTENDANCE_FILE_PATH = "attendance.json";

    @Test
    void testSuccessfulValidation() throws IOException {
        String email = "fi2195227@gmail.com";
        String otpNumber = "348933";
        String lesson = "Machine Learning";
        assertTrue(attendenceService.validateOTP(email, otpNumber, lesson));
    }

    @Test
    void testUnSuccessfulValidationWrongEmail() throws IOException {
        String email = "fatma@gmail.com";
        String otpNumber = "348933";
        String lesson = "Machine Learning";
        assertFalse(attendenceService.validateOTP(email, otpNumber, lesson));
    }

    @Test
    void testUnSuccessfulValidationWrongOTp() throws IOException {
        String email = "fi2195227@gmail.com";
        String otpNumber = "555865";
        String lesson = "Machine Learning";
        assertFalse(attendenceService.validateOTP(email, otpNumber, lesson));
    }

    @Test
    void testUnSuccessfulValidationWrongLesson() throws IOException {
        String email = "fi2195227@gmail.com";
        String otpNumber = "348933";
        String lesson = "Machine";
        assertFalse(attendenceService.validateOTP(email, otpNumber, lesson));
    }

}