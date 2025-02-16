package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.OTP;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender ;
    @Value("${spring.mail.username}")
    private String fromEmail;
    public void sendOTPViaEmail(int id , String to ,  String name , String lesson ){

        System.out.println(fromEmail);
        System.out.println("Sending OTP Via Email...");
        String otp = generateOTP(id , to , lesson) ;
        System.out.println(otp);
         SimpleMailMessage message = new SimpleMailMessage() ;
        message.setTo(to);
        message.setFrom(fromEmail);
        message.setSubject("Attendance Verification Code");
        String body = "Dear " + name + "\n Verification Code : " + otp +
                "\n Kindly enter this code in the attendance verification system to ensure your attendance is recorded.\n Best regards.";
        message.setText(body);
        try {
            mailSender.send(message);
            System.out.println("Email sent successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
    public void sendMail(String toEmail,String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage() ;
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
            System.out.println("Email sent successfully...");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send email: " + e.getMessage());
        }

    }
    private static final String JSON_FILE_PATH = "OTP.json";

    public String generateOTP(int accountNumber, String email , String lesson) {
        Random random = new Random();
        int otpValue = 100_000 + random.nextInt(900_000);
        String otp = String.valueOf(otpValue);
        System.out.println("OTP : " + otp);
        OTP otpInfo = new OTP(accountNumber, otp, email , lesson);
        otpInfo.setOtp(otp);
        otpInfo.setEmail(email);
        System.out.println(otpInfo.getOtp());
        System.out.println(otpInfo.getEmail());
        saveOtpInfoToFile(otpInfo , JSON_FILE_PATH);  // Save OTP to file
        return otp;
    }

    public void saveOtpInfoToFile(OTP otpInfo, String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(path);

        try {
            List<OTP> otpList = new ArrayList<>();

            if (file.exists()) {
                OTP[] existingOtps = objectMapper.readValue(file, OTP[].class);
                otpList.addAll(Arrays.asList(existingOtps));
            }

            otpList.add(otpInfo);

            // Log the serialized data for debugging
            System.out.println("Serialized Data: " + objectMapper.writeValueAsString(otpList));

            objectMapper.writeValue(file, otpList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
