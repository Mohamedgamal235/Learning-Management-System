package com.lms.Learning_Managment_System;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.OTP;
import com.lms.Learning_Managment_System.Service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EmailServiceTest {

    @Mock
    OTP otp;
    @Mock
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    @Spy
    @InjectMocks
    EmailService emailService1, emailService2;

    @Test
    void generateOtpTest() {
        String email = "fatmaibrahim1210@gmail.com";
        String lesson = "Machine Learning";
        int id = 2;
        doNothing().when(emailService1).saveOtpInfoToFile(any(OTP.class), any(String.class));
        String generatedOtp = emailService1.generateOTP(id, email, lesson);
        assertEquals(6, generatedOtp.length());
    }

    @Test
    void sendOTPViaEmailTest() {
        String to = "fi2195227@gmail.com";
        int id = 55;
        String name = "Fatma Ibarhim";
        String lesson = "Machine Learning";
        emailService1.sendOTPViaEmail(id, to, name, lesson);
        verify(mailSender, times(1)).send((SimpleMailMessage) any());
    }
}