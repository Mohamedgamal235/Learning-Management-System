package com.lms.Learning_Managment_System.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.Learning_Managment_System.Model.User;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class EditProfileService {
    ArrayList<User> users = new ArrayList<>() ;
    private static final String STUDENTS_FILE_PATH = "students.json" ;
    private static final String InSTRUCTORS_FILE_PATH = "instructors.json" ;
    private ObjectMapper objectMapper = new ObjectMapper();
    public void editStudentProfile(Integer id , String firstName , String LastName , String password) throws IOException {
        users = loadUsersFromFile(STUDENTS_FILE_PATH);
        for (User user : users) {
            if (user.getId() == id) {
                user.setFirstName(firstName);
                user.setLastName(LastName);
                ;
                user.setPassword(password);
                saveUsersToFile(STUDENTS_FILE_PATH, users);
                break;
            }
        }
    }
    public void editInstructorProfile(Integer id , String firstName , String LastName , String password) throws IOException {
        users = loadUsersFromFile(InSTRUCTORS_FILE_PATH);
        objectMapper.writeValue(new File(InSTRUCTORS_FILE_PATH), Collections.emptyList());
        for (User user : users) {
            if (user.getId() == id) {
                user.setFirstName(firstName);
                user.setLastName(LastName);
                user.setPassword(password);
                saveUsersToFile(InSTRUCTORS_FILE_PATH, users);
                break;
            }
        }
    }
    private ArrayList<User> loadUsersFromFile(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                return new ArrayList<>(Arrays.asList(objectMapper.readValue(file, User[].class)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    private void saveUsersToFile(String fileName, ArrayList<User> userList) {
        try {
            objectMapper.writeValue(new File(fileName), userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
