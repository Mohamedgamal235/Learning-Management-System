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
    public void sendOTPViaEmail(int id , String to ,  String name ){
        System.out.println(fromEmail);
        System.out.println("Sending OTP Via Email...");
        String otp = generateOTP(id , to) ;
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
    private static final String JSON_FILE_PATH = "OTP.json";

    public String generateOTP(int accountNumber, String email) {
        Random random = new Random();
        int otpValue = 100_000 + random.nextInt(900_000);
        String otp = String.valueOf(otpValue);
        System.out.println("OTP : " + otp);
        OTP otpInfo = new OTP(accountNumber, otp, email);
        saveOtpInfoToFile(otpInfo);  // Save OTP to file
        return otp;
    }

    private void saveOtpInfoToFile(OTP otpInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(JSON_FILE_PATH);

        try {
            // Check if the file exists, if not, create it
            if (!file.exists()) {
                file.createNewFile();  // Create the file if it doesn't exist
            }

            // We will use a List to hold OTP data and write it to the file
            List<OTP> otpList = new ArrayList<>();

            // If the file already contains data, we will append the new OTP
            if (file.length() > 0) {
                // Read existing data from file
                OTP[] existingData = objectMapper.readValue(file, OTP[].class);
                otpList.addAll(Arrays.asList(existingData)); // Add existing OTPs to the list
            }

            // Add the new OTP entry
            otpList.add(otpInfo);

            // Write the updated list of OTPs to the file
            objectMapper.writeValue(file, otpList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
