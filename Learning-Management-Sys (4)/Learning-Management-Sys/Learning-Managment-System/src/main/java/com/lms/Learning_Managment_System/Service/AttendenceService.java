package com.lms.Learning_Managment_System.Service;
import java.nio.file.Files;
import java.nio.file.Paths;

import ch.qos.logback.core.joran.sanity.Pair;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.Attended;
import com.lms.Learning_Managment_System.Model.OTP;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.*;
 class pair{
    public String first ;
    public String second ;
    pair(String first , String second){
        this.first = first ;
        this.second = second ;
    }

     public String getSecond() {
         return second;
     }

     public String getFirst() {
         return first;
     }
 }
@Service
public class AttendenceService {
    private final EmailService emailService;
    private Attended student = new Attended();
    private Map<String, pair> otpInfo = new HashMap<>();
    private Map<String, List<String>> attendenceInfo = new HashMap<>();
    private static final String ATTENDANCE_FILE_PATH = "Attendence.json";
    private static final String OTP_FILE_PATH = "OTP.json";
    private pair pair = new pair("" , "") ;

    public AttendenceService(EmailService emailService) {
        this.emailService = emailService;
    }

    public Map<String, List<String>> getAttendanceInfo() { // for make Excel report
        return attendenceInfo;
    }

    public void attend(String email, String name, int studentId, String lesson) {
        List<String> emails = new ArrayList<>();
        emails.add(email);
        student = new Attended(emails, lesson);
        emailService.sendOTPViaEmail(studentId , email, name , lesson);
        System.out.println("OTP Sent for lesson: " + lesson);
    }

    public Boolean validateOTP(String email, String otpNumber , String Lesson) throws IOException {
        System.out.println("info : " + email + " " + otpNumber + " " + Lesson);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonArray = mapper.readTree(new File(OTP_FILE_PATH));

            for (JsonNode node : jsonArray) {
                String Email = node.get("email").asText();
                String otp = node.get("otp").asText();
                System.out.println("otp" + otp);
                String lesson = node.get("lesson").asText() ;
                 pair = new pair(otp , lesson) ;
                otpInfo.put(Email,pair);  // Store email and OTP and lwsson in the map
            }
            System.out.println(otpInfo.isEmpty());
            for (Map.Entry<String, pair> entry : otpInfo.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue().getFirst() + " " + entry.getValue().getSecond());
                if (entry.getKey().equals(email) && entry.getValue().getFirst().equals(otpNumber)&& entry.getValue().getSecond().equals(Lesson)) {

                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void markAttendance(String email) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String lesson = pair.getSecond() ;
        File file = new File(ATTENDANCE_FILE_PATH);

        // Step 1: Load existing data from file
        if (file.exists() && file.length() > 0) {
            attendenceInfo = mapper.readValue(file, mapper.getTypeFactory()
                    .constructMapType(HashMap.class, String.class, List.class));
        } else {
            attendenceInfo = new HashMap<>(); // Initialize if file does not exist
        }

        // Step 2: Add new email to the list for the lesson
        List<String> emails = attendenceInfo.getOrDefault(lesson, new ArrayList<>());

        if (!emails.contains(email)) { // Avoid duplicates
            emails.add(email);
        }

        attendenceInfo.put(lesson, emails);

        // Step 3: Write the updated map back to the JSON file
        try {
            mapper.writeValue(file, attendenceInfo);
            System.out.println("Attendance updated successfully: " + attendenceInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
